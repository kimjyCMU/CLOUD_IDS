package process;

import configuration.*; 

import java.util.ArrayList;  
import java.util.Date;  
import java.util.List;  
  
import org.jnetpcap.Pcap;  
import org.jnetpcap.PcapIf;  
import org.jnetpcap.packet.PcapPacket;  
import org.jnetpcap.packet.PcapPacketHandler; 
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.packet.JFlowMap;


public class capturePackets {  
	static String ip = null;
	static PcapIf nic = null;
	static StringBuilder errbuf;
	
	public capturePackets(String ip)
	{
		this.ip = ip;
		
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
        for (PcapIf device : alldevs) {  
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
  
    public static void capture() {  

        int snaplen = 64 * 1024;           // Capture all packets, no trucation  
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets  
        int timeout = 10 * 1000;           // 10 seconds in millis  
        Pcap pcap =  
            Pcap.openLive(nic.getName(), snaplen, flags, timeout, errbuf);  
  
        if (pcap == null) {  
            System.err.printf("Error while opening device for capture: "  
                + errbuf.toString());  
            return;  
        }  

        PcapPacketHandler<String> jpacketHandler = new PcapPacketHandler<String>() 
		{  
            byte[] sIP = new byte[4];
            byte[] dIP = new byte[4];
			
            String srcIP = "";
            String dstnIP = "";
			int srcPort;
            int dstnPort;
			
            Tcp tcp = new Tcp();
            Udp udp = new Udp();
            Ip4 ip = new Ip4(); 
			
            public void nextPacket(PcapPacket packet, String user) 
			{ 
				if (packet.hasHeader(ip) && packet.hasHeader(tcp)) 
				{
					srcPort = tcp.source();
					dstnPort = tcp.destination();
					sIP = packet.getHeader(ip).source();
					srcIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
					dIP = packet.getHeader(ip).destination();
					dstnIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);
						
					System.out.println(srcIP + "->" + dstnIP + " " + srcPort + "->" + dstnPort);
				} 
					
				else if (packet.hasHeader(ip) && packet.hasHeader(udp)) 
				{
					srcPort = udp.source();
					dstnPort = udp.destination();
					sIP = packet.getHeader(ip).source();
					srcIP = org.jnetpcap.packet.format.FormatUtils.ip(sIP);
					dIP = packet.getHeader(ip).destination();
					dstnIP = org.jnetpcap.packet.format.FormatUtils.ip(dIP);

					System.out.println(srcIP + "->" + dstnIP + " " + srcPort + "->" + dstnPort);
				}

  
                System.out.printf("Received packet at %s caplen=%-4d len=%-4d %s\n",  
                    new Date(packet.getCaptureHeader().timestampInMillis()),   
                    packet.getCaptureHeader().caplen(),  // Length actually captured  
                    packet.getCaptureHeader().wirelen(), // Original length   
                    user                                 // User supplied object  
                );  
            }  
		};
		
		pcap.loop(10, jpacketHandler, "jNetPcap rocks!");
		
        pcap.close();  
    }  
}  
