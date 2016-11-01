package process;

import configuration.Configuration;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datatype.*;

public class Main {

    RemoteServer rs = new RemoteServer();

	static private String pmIP = null;
	static private String NIC = null;

    public static void main(String[] args) throws UnknownHostException,
            SocketException, IOException, InterruptedException {
        
		// Loading configuration options
        Configuration.setConfiguration();
		
        // Get the IP address of the physical server hosting this agent
        pmIP = getIp();

		// Get the NIC 
		NIC = Configuration.getNIC();

        // Initial contact of the agent. Maybe the relay server or the monitoring server May change subsequently.
        RemoteServer.SetIPPort(Configuration.getRemoteserver(),
                Configuration.getSocketPort());

        // s2s handles all of the transmit of packets. Whenever a part of code
        // wants to send packets, s2s utility is called.
        DatagramSocket socket = new DatagramSocket(
                Configuration.getSocketPort());
        Send2Server s2s = new Send2Server(socket);
        s2s.init();

        // exit if IP of the pm is not known.
        if (pmIP == null) {
            System.err.println("Cannot get the IP of this server.");
        }
        
        getData data = new getData(pmIP); // This engine is not for any specific vm, it's
                                // used for utilization and
                                // handshake with server
    }

    public static String getIp() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;

            // Iterate all NICs (network interface cards)...
            for (Enumeration<NetworkInterface> ifaces = NetworkInterface
                    .getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces
                        .nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration<InetAddress> inetAddrs = iface
                        .getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs
                            .nextElement();
                    //System.out.println(inetAddr.toString());
                    if (!inetAddr.isLoopbackAddress()) {

                        if (extractIPv4(inetAddr.toString())) {
                            // Found non-loopback site-local address. Return it
                            // immediately...
                            String resultIP = inetAddr.toString().split("/")[1];

                            return resultIP;
                        } 
						
						else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily
                            // site-local.
                            // Store it as a candidate to be returned if
                            // site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback
                            // non-site-local addresses as candidates,
                            // only the first. For subsequent iterations,
                            // candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other
                // non-loopback address.
                // Server might have a non-site-local address assigned to its
                // NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress.toString().split("/")[1];
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost()
            // returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException(
                        "The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress.toString().split("/")[1];
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
    
    public static boolean extractIPv4(String str) {
        String IPV4_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        Pattern patternIP = Pattern.compile(IPV4_PATTERN);
        Matcher matcher = patternIP.matcher(str);

        return matcher.find();
    }
}