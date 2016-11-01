package monitor;

import configuration.*;
import java.util.HashMap;
import java.util.Map;
import java.net.DatagramSocket;
import java.net.SocketException;

public class Monitor {
	static String dbIP = null;
	static int dbPort;
	static String dbName = null;
	
	static String collName = null;

	public static void main(String[] args) {
	
		// Read and assign configuration parameters from the configuration file 
		Configuration.setConfiguration();		
		dbIP = Configuration.getDBIP();
		dbPort = Configuration.getDBPort();
		dbName = Configuration.getDBName();
		collName = Configuration.getCollection();
		
		try
		{
			// open a socket for communication with clients
			// initialize communication classes using the socket
			DatagramSocket socket = new DatagramSocket(Configuration.getSocketport());
			Socketserver server = new Socketserver(socket);
			
			// initialize the collection
			setCollection collection = new setCollection(dbIP, dbPort, dbName, collName);
		
			// start receiving data from clients
			server.start();
			
			// start updating the collection
			collection.initialize();			
			
		} catch (SocketException e) {
			e.printStackTrace();
		}		
	}
}
