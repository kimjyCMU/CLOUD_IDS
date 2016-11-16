package main;

import java.util.*;
import java.lang.*;
import java.text.SimpleDateFormat;
import com.mongodb.*;
import java.net.UnknownHostException;

public class getAllUAs
{
	static DBCollection collection;			
	
	static	ArrayList<String> timeType = new ArrayList<String>();
	static	ArrayList<String> metrics = new ArrayList<String>();
	
	static String data = "";
	
	public getAllUAs(DB db, String collName) throws MongoException 
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
	
	public getAllUAs() {}
	
	public static String getResult(String dataType)
	{
		//##### Set metrics to show####//
		metrics.clear();
		
		if(dataType.equals("UA"))
		{
			metrics.add("UA");
		}

		//#############################//
		
		Cursor cursor = getCursor();
		HashSet<dataFormat> tsData = new HashSet<dataFormat>();
		
		while(cursor.hasNext())
		{			
			BasicDBObject result = (BasicDBObject) cursor.next();
			String ip = (String)result.get("IP");
			
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
						String type = metrics.get(k);
						String value = (String)object.get(j).get(type);
						
						if(value != null)
						{				
							int ts = (int)object.get(j).get("TS");
							
							if(ts > (int)(System.currentTimeMillis()/1000))
								continue;
							
							dataForm = new dataFormat(ts, ip, value, type);		
							tsData.add(dataForm);
							break;
						}
					}
				}
			}
												
			data = "[";
				
			Iterator<dataFormat> it = tsData.iterator();
			
			while(it.hasNext())
			{
				dataForm = it.next();	
					
				data += "{\"IP\" : \"" + dataForm.getAddr() + "\",";
				data += "\"TS\" : \"" + dataForm.getTS() + "\",";
				data += "\"Value\" : \"" + dataForm.getValue() + "\"}";	
					
				if(it.hasNext())
						data += ",";					
			}			

			if(cursor.hasNext())
				data += ",";
		}
			
		data += "]";
		
		return data;
	}
	
	public static Cursor getCursor() throws MongoException
	{		
		ArrayList<String> monitorGroup = new ArrayList<String>();
		
//	Find all IPs		
		BasicDBObject query1 = new BasicDBObject();
		BasicDBObject field = new BasicDBObject();
		field.put("IP", 1);
		DBCursor cursor1 = collection.find(query1,field);
		
		while (cursor1.hasNext()) {
			BasicDBObject obj = (BasicDBObject) cursor1.next();
			monitorGroup.add(obj.getString("IP"));
		}
		System.out.println("## " + monitorGroup);
		
// db.data.aggregate({$project:{"IP":"$IP","min":"$MIN"}},{$match:{'IP':{$in:["10.1.128.27","10.1.128.24","10.1.128.122"]}}})
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