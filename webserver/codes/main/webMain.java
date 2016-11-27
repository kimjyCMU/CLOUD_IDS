/* Main class */

package main;

import configuration.*;
import servlet.*;

import java.util.*;
import java.lang.*;
import com.mongodb.*;
import java.net.UnknownHostException;

public class webMain
{
// Set physical information about mongocollection
	static String dbIP = null; // IP address
	static int dbPort; // Port number
	static String dbName = null; // 
	static String collName = null; // 

//	public static void main(String args[])
	public webMain() throws UnknownHostException
	{
		Configuration.setConfiguration();
		dbIP = Configuration.getDBIP();
		dbPort = Configuration.getDBPort();
		dbName = Configuration.getDBName();
		collName = Configuration.getCollName();
		
		DBconnection db = new DBconnection(dbIP, dbPort, dbName);
			
		ActionServlet servlet = new ActionServlet();
		getUtilization util = new getUtilization(db.getThisDB(), collName);	
		getRequests RQ = new getRequests(db.getThisDB(), collName);	
		getUA ua = new getUA(db.getThisDB(), collName);
		getAllUAs allUAs = new getAllUAs(db.getThisDB(), collName);
//		System.out.println(ua.getResult("10.1.1.2", "UA"));
//		System.out.println(util.getResult("10.1.1.2", "system"));
	}
}
