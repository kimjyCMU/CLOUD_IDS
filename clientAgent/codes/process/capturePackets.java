package process;

import configuration.*; 

import java.util.ArrayList;  
import java.util.Date;  
import java.util.List;  
import java.math.BigInteger;
  
import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.Payload; 
import org.jnetpcap.packet.format.*;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.tcpip.Http;
import org.jnetpcap.protocol.network.Ip4;


public class capturePackets {  
	static String ip = null;
	static PcapIf nic = null;
	static StringBuilder errbuf;
	
	static int servicePort;
	
	public capturePackets(String ip)
	{
		this.ip = ip;
		servicePort = Configuration.getServicePort();
		
		List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs  
        errbuf = new StringBuilder(); // For any error msgs  
  
        int r = Pcap.findAllDevs(alldevs, errbuf);  
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {  
            System.err.printf("Can't read list of devices, error is %s", errbuf  
                .toString());  
            return;  
        }  
  
        System.out.println("\n==== Network devices found ====");  
        int i = 0;  
        for (PcapIf device : alldevs) 
		{  
            String description =  
                (device.getDescription() != null) ? device.getDescription()  
                    : "No description available";  
					
			System.out.printf("Find #%d: %s [%s]\n", i++, device.getName(), description);
			
			if(device.toString().contains(ip))
			{
				nic = device;
				System.out.println("We are going to choose < " + nic.getName() + " > ");
				System.out.println("===================================\n");
			}
        }  
	}
  
    public static void capture() 
	{  
        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 10 * 1000;           // 10 seconds in millis  
        Pcap pcap = Pcap.openLive(nic.getName(), snaplen, flags, timeout, errbuf);  
  
        if (pcap == null) 
		{  
            System.err.printf("Error while opening device for capture: "  
                + errbuf.toString());  
            return;  
        }

        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() 
		{  
            Tcp tcp = new Tcp();
            Udp udp = new Udp();
            Ip4 ip = new Ip4(); 
			Http http = new Http();

            byte[] sIP = new byte[4];
            byte[] dIP = new byte[4];
			
            String srcIP = "";
            String dstnIP = "";
			int srcPort;
            int dstnPort;
			
			Payload payload = new Payload();
			boolean data = false;
			
			byte[] payloadContent;
			String content = "## No payload";
			
			StringBuilder str = new StringBuilder();
			
            public void nextPacket(PcapPacket packet, String user) 
			{
				if (packet.hasHeader(ip))
				{
					sIP = packet.getHeader(ip).source();
					srcIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
					dIP = packet.getHeader(ip).destination();
					dstnIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
				}
				
				if (packet.hasHeader(tcp)) 
				{
					srcPort = tcp.source();
					dstnPort = tcp.destination();
					
					// Should be analyzed
					if((servicePort == srcPort) || (servicePort == dstnPort))
					{
						// HTTP packet
						if(servicePort == 80)
						{
							if(packet.hasHeader(http) && packet.hasHeader(payload))
							{			
								payloadContent = http.getPayload();
								content = "## HTTP : " + new String(payloadContent);
								
								//#################
//								count (srcPort, dstnPort);
							}
						}
						
						else
						{
//							count (srcPort, dstnPort);
						}
						
						System.out.println(srcIP + "->" + dstnIP + " " + srcPort + "->" + dstnPort + " ");
						System.out.println(content);
					}					
				}				
            }
			
			
/*			public void countRQRS(String srcPort, String dstnPort)
			{
				// request
				if(dstnPort == servicePort)
				{
					count srcIP --> by ip
				}
				
				// response
				if(srcPort == servicePort)
				{
					count dstnIP --> by ip
				}
			}
*/		};
		
		pcap.loop(5, jpacketHandler, "jNetPcap rocks!");
		
        pcap.close();  
    }  
}  
