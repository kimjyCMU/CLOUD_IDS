package monitor;

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

	// Set the first values of utilization/response time into the history table 
	public static void initialize()
	{
		dataTypes.clear();

		dataTypes.add("CPU");
		dataTypes.add("RAM");
		dataTypes.add("Disk");
		dataTypes.add("Inbound");
		dataTypes.add("Outbound");
		dataTypes.add("RT");
	}
	
	// All changes are updated into MINUTE fields 
	public static void updateCollection(String ip, String type, String value)
	{			
		if(collection.count() == 0)
			initialize();
			
		BasicDBObject queryIP = new BasicDBObject("IP",ip);
		BasicDBObject newValue = new BasicDBObject("TS", (int)(System.currentTimeMillis()/1000))
				.append(type,value);
		BasicDBObject updateValue = new BasicDBObject("$push", new BasicDBObject("MIN",newValue));
						
		collection.update(queryIP,updateValue,true,false);
			
		BasicDBObject updateLastValue = new BasicDBObject("$set", new BasicDBObject("L"+type, value));			
		collection.update(queryIP, updateLastValue, false, false); 
	}
}
