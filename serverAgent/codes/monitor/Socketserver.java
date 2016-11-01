package monitor;

import configuration.*;

import java.io.*;  
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.*;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.Cursor;
import com.mongodb.*;

import datatype.*;


public class Socketserver extends Thread{  
	
    private Mongo mongoClient;
    private DB db;
	
	private String myIP = null;		
	private static DatagramSocket socket;
	
	setCollection dataCollection;
	
    @SuppressWarnings("deprecation")
	public Socketserver(DatagramSocket socket){
		this.socket = socket;
    	mongoClient = new MongoClient(Configuration.getDBIP(), Configuration.getDBPort() );
    	db = mongoClient.getDB(Configuration.getDBName());
    }
	
	public Socketserver(){}
 	 
    public void run() {  		
    	try 
		{
			// for socket communication
			byte[] in_buff = new byte[2048];
			
			DatagramPacket in_packet = new DatagramPacket(in_buff, in_buff.length);
			System.out.println("Socket server has started!");
			
			// find this server's address
			myIP = getIp();
			System.out.println("# My IP address is " + myIP);		

			// keep receiving data from clients			
			while (true) 
			{  
				socket.receive(in_packet);
				
				ByteArrayInputStream in_byteArrayStream = new ByteArrayInputStream(in_buff);
				ObjectInputStream in_objectStream = new ObjectInputStream(in_byteArrayStream);
				
				InetAddress client = in_packet.getAddress();

				try 
				{
					RequestType request = (RequestType) in_objectStream.readObject();
					String pmIP = "";
					
					System.out.println("\n\n=====================\nReceived a packet from : " + client.getHostAddress());
										
					
					// receive utilization data from clients
					if(request.getType()==Configuration.UTILIZATION)
					{
						//this is a utilization info message from an agent
						UtilizInfo uInfo = request.getUtil();
						pmIP = uInfo.pmIPaddr;

						if(Double.toString(uInfo.cpu) != null && !Double.toString(uInfo.cpu).equals("null"))
							dataCollection.updateCollection(pmIP, "CPU", Double.toString(uInfo.cpu));	
							
						if(Double.toString(uInfo.mem) != null && !Double.toString(uInfo.mem).equals("null"))
							dataCollection.updateCollection(pmIP, "RAM", Double.toString(uInfo.mem));
							
						if(Double.toString(uInfo.disk) != null && !Double.toString(uInfo.disk).equals("null"))
							dataCollection.updateCollection(pmIP, "Disk", Double.toString(uInfo.disk));

						if(Double.toString(uInfo.netin) != null && !Double.toString(uInfo.netin).equals("null"))	
							dataCollection.updateCollection(pmIP, "Inbound", Double.toString(uInfo.netin));	
							
						if(Double.toString(uInfo.netout) != null && !Double.toString(uInfo.netout).equals("null"))	
							dataCollection.updateCollection(pmIP, "Outbound", Double.toString(uInfo.netout));												
					}
				} 
				catch (ClassNotFoundException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Class not found");
				} 
			}  
		}
		catch (SocketException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		catch (IOException e1) 
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
	
	// get the server's IP address
	// needed to set 'isPublicIP' in the configuration file (true : get a public IP, false : get a private IP)  
	public static String getIp() throws UnknownHostException
	{
		try {
			InetAddress candidateAddress = null;
			String resultIP = "";
			boolean isPublicIP = Configuration.getIsPublicIP();
			
			// Iterate all NICs (network interface cards)...
			for (Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				boolean foundFlag = false;
				// Iterate all IP addresses assigned to each card...
				for (Enumeration<InetAddress> inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) 
				{
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if(foundFlag == true)
						break;
						
					if (!inetAddr.isLoopbackAddress()) 
					{					
						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it immediately...
							resultIP = inetAddr.toString().split("/")[1];
								
							if(isPublicIP)
							{
								System.out.println("# " + resultIP + " is not a public IP. Keep finding my IP address");
								continue;
							}
								
							return resultIP;
						}
						
						else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily site-local.
							// Store it as a candidate to be returned if site-local address is not subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
							// only the first. For subsequent iterations, candidate will be non-null.												
						}
						
						else
						{
							if(candidateAddress instanceof Inet4Address) 
							{
								resultIP = inetAddr.getLocalHost().getHostAddress();
								
								if(!isPublicIP)
								{
									System.out.println("# " + resultIP + " is not a private IP. Keep finding my IP address");
									continue;
								}
								foundFlag = true;

								return resultIP;
							}
						}
					}
				}
			}			
			
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other non-loopback address.
				// Server might have a non-site-local address assigned to its NIC (or it might be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
//				System.out.println("\n>>> candidateAddress is not null ");
			}
		
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost() returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress.toString().split("/")[1];
		}
		catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}
} 