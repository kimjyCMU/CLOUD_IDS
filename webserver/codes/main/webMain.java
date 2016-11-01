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

//	public static void main(String args[]){
	public webMain() throws UnknownHostException{
		Configuration.setConfiguration();
		dbIP = Configuration.getDBIP();
		dbPort = Configuration.getDBPort();
		dbName = Configuration.getDBName();
		collName = Configuration.getCollName();
		
		DBconnection db = new DBconnection(dbIP, dbPort, dbName);
			
		ActionServlet servlet = new ActionServlet();
		getData data = new getData(db.getThisDB(), collName);	
	}
}
