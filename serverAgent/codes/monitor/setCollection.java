package monitor;

import datatype.*;
import com.mongodb.*;
import java.util.*;
import java.text.DecimalFormat;

public class setCollection
{		
	static Mongo mongo;
	static DB db;	
	static DBCollection collection;
	
	static ArrayList<String> dataTypes = new ArrayList<String>();
	
	// Configure the history DB according to the configuration file
	public setCollection(String dbIP, int dbPort, String dbName, String collName) throws MongoException 
	{	
		try {
				mongo = new Mongo(dbIP, dbPort);
				db = mongo.getDB(dbName);
				collection = db.getCollection(collName);
			
		} catch(MongoException e) {
			System.out.println(e.getMessage());
				e.printStackTrace();
		}
	}
	
	public setCollection(){}
	
	// All changes are updated into MINUTE fields 
	public static void updateUtilization(String ip, String type, String value)
	{					
		BasicDBObject queryIP = new BasicDBObject("IP",ip);
		BasicDBObject newValue = new BasicDBObject("TS", (int)(System.currentTimeMillis()/1000))
									.append(type,value);
		BasicDBObject updateValue = new BasicDBObject("$push", new BasicDBObject("MIN",newValue));
						
		collection.update(queryIP,updateValue,true,false);
			
		BasicDBObject updateLastValue = new BasicDBObject("$set", new BasicDBObject("L"+type, value));			
		collection.update(queryIP, updateLastValue, false, false); 
	}
	
	// All changes are updated into MINUTE fields 
	public static void updateRequest(String type, RequestInfo request)
	{	
		String ip = request.myIP;
		
		BasicDBObject queryIP = new BasicDBObject("IP",ip);
		BasicDBObject neighbor = new BasicDBObject("neighbor", new BasicDBObject("TS", Long.toString(System.currentTimeMillis()/1000))
																.append("address", request.neighIP+"_"+Integer.toString(request.port))
																.append("req", Integer.toString(request.req))
																.append("res", Integer.toString(request.res))
																.append("ratio", Double.toString(request.ratio)));
		
		BasicDBObject newValue = new BasicDBObject("RQ", neighbor);
		BasicDBObject updateValue = new BasicDBObject("$push", new BasicDBObject("MIN",newValue));

		collection.update(queryIP, updateValue,true,false);							
	}
}
