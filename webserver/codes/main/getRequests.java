package main;

import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import com.mongodb.*;
import java.net.UnknownHostException;

public class getRequests
{
	static DBCollection collection;			
	
	static	ArrayList<String> timeType = new ArrayList<String>();
	static	ArrayList<String> metrics = new ArrayList<String>();
	
	static String data = "";
	
	public getRequests(DB db, String collName) throws MongoException 
	{
		try 
		{
			collection = db.getCollection(collName);
			timeType.clear();
			timeType.add("min");
		} 
		catch(MongoException e) {
			System.out.println(e.getMessage());
				e.printStackTrace();
		}	
	}
	
	public getRequests() {}
	
	public static String getResult(String clickedIP, String dataType)
	{
		//##### Set metrics to show####//
		metrics.clear();
		
		if(dataType.equals("request"))
		{
			metrics.add("req");
			metrics.add("res");
			metrics.add("ratio");
		}

		//#############################//		
		Cursor cursor = getCursor(clickedIP);
				
		while(cursor.hasNext())
		{
			HashSet<dataFormat> tsData = new HashSet<dataFormat>();			
			BasicDBObject result = (BasicDBObject) cursor.next();	
			dataFormat dataForm;
			
			for(int i=0; timeType.size() > i; i++)
			{
				List<BasicDBObject> object = (ArrayList<BasicDBObject>)result.get(timeType.get(i));

				if(object == null)
					continue;

				
				for(int j=0; object.size() > j; j++)
				{									
					for(int k=0; metrics.size() > k; k++)
					{
						String addr = (String)object.get(j).get("address");
						String type = metrics.get(k);
						String value = (String)object.get(j).get(type);
						
						if(value != null)
						{			
							String strTS = (String)object.get(j).get("TS");
							int ts = Integer.parseInt(strTS);
							
							if(ts > (int)(System.currentTimeMillis()/1000))
								continue;

							dataForm = new dataFormat(ts, addr, value, type);	
							tsData.add(dataForm);							
						}
					}
				}
			}
											
			data = "[";
				
			ArrayList<dataFormat> newTsUtil = sortTS(tsData);
			
			for(int i = 0; i < newTsUtil.size(); i++)
			{
				dataForm = newTsUtil.get(i);		

				data += "{\"Type\" : \"" + dataForm.getType() + "\",";
				data += "\"Addr\" : \"" + dataForm.getAddr() + "\",";
				data += "\"TS\" : \"" + dataForm.getTS() + "\",";
				data += "\"Value\" : \"" + dataForm.getValue() + "\"}";	
		
				if(i+1 != newTsUtil.size())
					data += ",";					
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
	
	public static Cursor getCursor(String ip) throws MongoException
	{		
		ArrayList<String> monitorGroup = new ArrayList<String>();
		
		monitorGroup.add(ip);
		
//db.data.aggregate({$project:{"IP":"$IP","min":"$MIN.RQ.neighbor"}},{$match:{'IP':{$in:["10.1.128.24"]}}})
		BasicDBObject query = new BasicDBObject();		
		query.put("IP","$IP");
		query.put("min","$MIN.RQ.neighbor");

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

