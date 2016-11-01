package main;

import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import com.mongodb.*;
import java.net.UnknownHostException;

public class getData
{
	static DBCollection collection;			
	
	static	ArrayList<String> timeType = new ArrayList<String>();
	static	ArrayList<String> utilType = new ArrayList<String>();
	
	static String data = "";
	
	public getData(DB db, String collName) throws MongoException 
	{
		try 
		{
			collection = db.getCollection(collName);
			
			timeType.add("min");
			
			utilType.add("CPU");
			utilType.add("RAM");
			utilType.add("Disk");
			utilType.add("Inbound");
			utilType.add("Outbound");
		} 
		catch(MongoException e) {
			System.out.println(e.getMessage());
				e.printStackTrace();
		}	
	}
	
	public getData() {}
	
	public static String getResult(String clickedIP)
	{		
		Cursor cursor = getCursor(clickedIP);
		
		while(cursor.hasNext())
		{
			HashSet<dataFormat> tsData = new HashSet<dataFormat>();
			
			BasicDBObject result = (BasicDBObject) cursor.next();	
			
			String ip = "";
			dataFormat dataForm;
			
			for(int i=0; timeType.size() > i; i++)
			{
				List<BasicDBObject> object = (ArrayList<BasicDBObject>)result.get(timeType.get(i));
				
				if(object == null)
					continue;
				
				for(int j=0; object.size() > j; j++)
				{					
					for(int k=0; utilType.size() > k; k++)
					{
						String type = utilType.get(k);
						String value = (String)object.get(j).get(type);
						
						if(value != null)
						{				
							int ts = (int)object.get(j).get("TS");
							
							if(ts > (int)(System.currentTimeMillis()/1000))
								continue;
							
							dataForm = new dataFormat(ts, value, type);		
							tsData.add(dataForm);
							break;
						}
					}
				}
			}
			
			// no utilization in history like VMs
			if(tsData == null)
			{
				data = "[";

				ArrayList<String> lastUtil = getLastData(clickedIP);				
				
				for(int k=0; utilType.size() >k; k++)
				{		
					String value = "0.0";
					
					if(lastUtil.get(k) != null && !lastUtil.get(k).equals("null"))
							value = lastUtil.get(k);
							
					data += "{\"Type\" : \"" + utilType.get(k) + "\",";
					data += "\"TS\" : \"" + (int)((System.currentTimeMillis()/1000)) + "\",";
					data += "\"Value\" : \"" + lastUtil.get(k) + "\"}";
					
					if(utilType.size() != k+1)
						data += ",";								
				}
			}
			
			else
			{		
				ArrayList<String> lastUtil = getLastData(clickedIP);
				
				for(int k=0; utilType.size() >k; k++)
				{
					dataForm = new dataFormat((int)(System.currentTimeMillis()/1000), lastUtil.get(k), utilType.get(k));
					tsData.add(dataForm);
				}			
									
				data = "[";
				
				ArrayList<dataFormat> newTsUtil = sortTS(tsData);
			
				for(int i = 0; i < newTsUtil.size(); i++)
				{
					dataForm = newTsUtil.get(i);		
						
					data += "{\"Type\" : \"" + dataForm.getType() + "\",";
					data += "\"TS\" : \"" + dataForm.getTS() + "\",";
					data += "\"Value\" : \"" + dataForm.getValue() + "\"}";	
					
					if(i+1 != newTsUtil.size())
						data += ",";					
				}			
			}
			if(cursor.hasNext())
				data += ",";
		}
			
		data += "]";
		
		return data;
	}
	
	public static ArrayList<dataFormat> sortTS(HashSet<dataFormat> old)
	{
		ArrayList<dataFormat> newUtil = new ArrayList<dataFormat>();
		ArrayList<dataFormat> oldUtil = new ArrayList<dataFormat>(old);
		
		// make a list of TS
		HashSet<Integer> tsList = new HashSet<Integer>();
		dataFormat dataForm;
		
		for(int i = 0; oldUtil.size() > i; i++)
		{
			dataForm = oldUtil.get(i);
			tsList.add(dataForm.getTS());
		}
		
		// sort
		List sortedList = new ArrayList(tsList);
		Collections.sort(sortedList);
		
		// get newUtil
		for(int i = 0; sortedList.size() > i; i++)
		{
			int minTS = (int)sortedList.get(i);
			
			for(int j = 0; oldUtil.size() > j; j++)
			{
				dataForm = oldUtil.get(j);
				int myTS = dataForm.getTS();
				
				if(myTS == minTS)
				{
					newUtil.add(dataForm);
					oldUtil.remove(j);
					j--;
				}
			}
		}
				
		return newUtil;
	}
	
	public static ArrayList<String> getLastData(String ip)
	{
		ArrayList<String> lastUtilValues = new ArrayList<String>();
		ArrayList<String> monitorGroup = new ArrayList<String>();
		monitorGroup.add(ip);
		
		BasicDBObject query = new BasicDBObject();
		query.put("IP","$IP");
		
		for(int i=0; utilType.size() > i; i++)
		{
			String type = utilType.get(i);
			query.put(type,"$L"+type);
		}
		
		BasicDBObject project = new BasicDBObject("$project",query);	

		BasicDBObject match = new BasicDBObject("$match",new BasicDBObject("IP", new BasicDBObject("$in", monitorGroup)));
				
		List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();

		pipeline.add(project);	
		pipeline.add(match);				
			
		AggregationOptions aggregationOptions = AggregationOptions.builder()
			.batchSize(100)
			.outputMode(AggregationOptions.OutputMode.CURSOR)
			.build();
		
		Cursor cursor = collection.aggregate(pipeline,aggregationOptions);
		
		while(cursor.hasNext())
		{

			BasicDBObject result = (BasicDBObject) cursor.next();	
			
			for(int i=0; utilType.size() > i; i++)
			{
				String type = utilType.get(i);
				String value = (String)result.get(type);	
				lastUtilValues.add(value);			
			}
		}
		
		return lastUtilValues;
	}
	
	public static Cursor getCursor(String ip) throws MongoException
	{		
		ArrayList<String> monitorGroup = new ArrayList<String>();
		
		monitorGroup.add(ip);
		
// db.history.aggregate({$project:{"IP":"$IP","min":"$MIN","hour":"$HOUR"}},{$match:{'IP':{$in:["10.214.10.15","10.1.0.21"]}}})
		BasicDBObject query = new BasicDBObject();		
		query.put("IP","$IP");
		query.put("min","$MIN");

		BasicDBObject project = new BasicDBObject("$project",query);	

		BasicDBObject match = new BasicDBObject("$match",new BasicDBObject("IP", new BasicDBObject("$in", monitorGroup)));
				
		List<BasicDBObject> pipeline = new ArrayList<BasicDBObject>();

		pipeline.add(project);	
		pipeline.add(match);				
			
		AggregationOptions aggregationOptions = AggregationOptions.builder()
			.batchSize(100)
			.outputMode(AggregationOptions.OutputMode.CURSOR)
			.build();
		
		Cursor cursor = collection.aggregate(pipeline,aggregationOptions);	
		
		return cursor;
	}
	
	static String getDate(int time)
	{
		long milliseconds = time * 1000L;
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
		Calendar cal = Calendar.getInstance();
       cal.setTimeInMillis(milliseconds);
	   
	   return sdf.format(cal.getTime());
	}
}

class dataFormat
{
	int TS;
	String value;
	String type;
	
	public dataFormat(int TS, String value, String type)
	{
		this.TS = TS;
		this.value = value;
		this.type = type;
	}
	
	public int getTS()
	{
		return TS;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public String getType()
	{
		return type;
	}
}