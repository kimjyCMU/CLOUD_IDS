package main;

import java.util.*;
import java.lang.*;
import com.mongodb.*;

public class DBconnection
{	
	static DB db;
	
	public DBconnection(String dbIP, int dbPort, String dbName) throws MongoException
	{
		try {		
			Mongo mongoClient = new Mongo(dbIP, dbPort);
			db = mongoClient.getDB(dbName);			
		
		} catch(MongoException e) {
			System.out.println(e.getMessage());
				e.printStackTrace();
		}	
	}
	
	
	public static DB getThisDB()
	{
		return db;
	}
}