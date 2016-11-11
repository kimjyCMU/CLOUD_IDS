package process;

import configuration.*; 
import datatype.*; 

import java.util.*;  
import java.text.DecimalFormat;
  
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


public class monitorRequests {  
	static String ip = null;
	static PcapIf nic = null;
	static StringBuilder errbuf;
	
	static int servicePort;
	
	static HashMap<String, numReqRes> cntRQ = new HashMap<String, numReqRes>();
	
	public monitorRequests(String ip)
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
  
		cntRQ.clear();
		
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
							if(packet.hasHeader(http))
							{
								if(packet.hasHeader(payload))
								{	
									
									payloadContent = http.getPayload();
									content = "## HTTP : " + new String(payloadContent);
								}
								
							}analyze (srcPort, dstnPort);
						}
						
						else
							analyze (srcPort, dstnPort);
						
						System.out.println("\n >> PACKET : " + srcIP + "->" + dstnIP + " " + srcPort + "->" + dstnPort + " ");
//						System.out.println(content);
					}					
				}				
            }
						
			public void analyze(int srcPort, int dstnPort)
			{
				Iterator<String> itRQ = cntRQ.keySet().iterator();

				// request
				if(dstnPort == servicePort)
				{	
					boolean flag = false;
					
					while(itRQ.hasNext())
					{	
						String key = itRQ.next();
						
						if(key.equals(srcIP))
						{						
							numReqRes RQ = cntRQ.get(key);
							
							int value = RQ.getReq();
							value++;
							
							RQ.setReq(value);
							
							cntRQ.put(key, RQ);
							flag = true;
							
							break;
						}
					}
					
					if(!flag) // this it the first IP show
					{	
						numReqRes RQ = new numReqRes (1, 0);
						cntRQ.put(srcIP, RQ);
					}
				}
				
				// response
				if(srcPort == servicePort)
				{	
					boolean flag = false;
					
					while(itRQ.hasNext())
					{	
						String key = itRQ.next();
						
						if(key.equals(dstnIP))
						{	
							numReqRes RQ = cntRQ.get(key);
							
							int value = RQ.getRes();
							value++;
							
							RQ.setRes(value);
							
							cntRQ.put(key, RQ);
							flag = true;
							
							break;
						}
					}
					
					if(!flag) // this it the first IP show
					{	
						numReqRes RQ = new numReqRes (0, 1);
						cntRQ.put(dstnIP, RQ);
					}
				}
				
			}
			
		};
		
		pcap.loop(5, jpacketHandler, "jNetPcap rocks!");	
		sendRequest();
		
        pcap.close();  
    }
	
	public static void sendRequest()
	{
		RequestInfo req_info;
		RequestType request = new RequestType();
		int infoType = Configuration.REQUEST;
		double ratio = 0;
		
		Iterator<String> itRQ = cntRQ.keySet().iterator();
			
		System.out.println("############## MyIP <-> neighIP : REQ , RES, RES/REQ ################");			
		while(itRQ.hasNext())
		{
			String key = itRQ.next();
			numReqRes RQ = cntRQ.get(key);
			
			int req = RQ.getReq();
			int res = RQ.getRes();
			
			if(req != 0)
				ratio = decimalFormat((double)res / req);
			
			System.out.println(ip + "<->" + key + " : " + req + " , " + res + " , " + ratio);
			

			req_info = new RequestInfo(ip, key, servicePort, req, res, ratio);
			request.SetIsXml(infoType);
			request.SetRequestInfo(req_info);
			
			Send2Server.Send(request);			
		}		
	}
	
	public static double decimalFormat(double usage) {
        DecimalFormat format = new DecimalFormat();
        format.applyLocalizedPattern("0.##");

        double result = Double.parseDouble(format.format(usage));
        return result;
    }
}  