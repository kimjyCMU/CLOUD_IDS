package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Configuration {

    private static String serverIP = null;
    private static int socketPort;
	
	private static String NIC = null;
	private static int servicePort;
	
	private static int systemInterval;
	private static int networkInterval;
	private static int requestInterval;
	
	private static int avgCPU;
	private static int avgRAM;
	private static int avgDISK;
	private static int avgInbound;
	private static int avgOutbound;	
	private static int avgRequestPair;
	private static double avgRequestRatio;

	private static int thresholdCPU;
	private static int thresholdRAM;
	private static int thresholdDISK;
	private static int thresholdInbound;
	private static int thresholdOutbound;	
	private static int thresholdRequestPair;
	private static double thresholdRequestRatio;
	
    public static final int SYSTEM = 1;
	public static final int NETWORK = 2;
	public static final int REQUEST = 3;
	public static final int UNITACTION = 4;

    public static void setConfiguration() 
	{
        try {
            File readFile = new File("./Config.txt");
            BufferedReader inFile = new BufferedReader(new FileReader(readFile));
            String sLine = null;

            while ((sLine = inFile.readLine()) != null) {
                String parameter = sLine.split("=")[0];
                String value = sLine.split("=")[1];
				
				//###### Set info about the server agent ######				
				if (parameter.equals("remote server"))
                    serverIP = value;
					
				else if (parameter.equals("socket_port"))
                    socketPort = Integer.parseInt(value);				

				//###### Set info about the NIC to be monitored
		        else if (parameter.equals("NIC"))
                    NIC = value;		
				
				else if (parameter.equals("service_port"))
                    servicePort = Integer.parseInt(value);	
                
				//###### Set monitoring intervals ######
				else if (parameter.equals("system_interval"))
                    systemInterval = Integer.parseInt(value);

				else if (parameter.equals("network_interval"))
                    networkInterval = Integer.parseInt(value);

				else if (parameter.equals("request_interval"))
                    requestInterval = Integer.parseInt(value);		

				//###### Set monitoring intervals ######
				else if (parameter.equals("system_interval"))
                    systemInterval = Integer.parseInt(value);

				else if (parameter.equals("network_interval"))
                    networkInterval = Integer.parseInt(value);

				else if (parameter.equals("request_interval"))
                    requestInterval = Integer.parseInt(value);
				
				//###### Set an average value for each metric  ######
				else if (parameter.equals("CPU_avg"))
                    avgCPU = Integer.parseInt(value);

				else if (parameter.equals("RAM_avg"))
                    avgRAM = Integer.parseInt(value);

				else if (parameter.equals("DISK_avg"))
                    avgDISK = Integer.parseInt(value);

				else if (parameter.equals("Inbound_avg"))
                    avgInbound = Integer.parseInt(value);

				else if (parameter.equals("Outbound_avg"))
                    avgOutbound = Integer.parseInt(value);

				else if (parameter.equals("Request_pair_avg"))
                    avgRequestPair = Integer.parseInt(value);

				else if (parameter.equals("Request_ratio_avg"))
                    avgRequestRatio = Double.parseDouble(value);	
				
				//###### Set a threshold for each metric  ######
				else if (parameter.equals("CPU_threshold"))
                    thresholdCPU = Integer.parseInt(value);

				else if (parameter.equals("RAM_threshold"))
                    thresholdRAM = Integer.parseInt(value);

				else if (parameter.equals("DISK_threshold"))
                    thresholdDISK = Integer.parseInt(value);

				else if (parameter.equals("Inbound_threshold"))
                    thresholdInbound = Integer.parseInt(value);

				else if (parameter.equals("Outbound_threshold"))
                    thresholdOutbound = Integer.parseInt(value);
					
				else if (parameter.equals("Request_pair_threshold"))
                    thresholdRequestPair = Integer.parseInt(value);

				else if (parameter.equals("Request_ratio_threshold"))
                    thresholdRequestRatio = Double.parseDouble(value);					
				
                sLine = null;
            }
            inFile.close();
			
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //################################
	public static String getRemoteserver() {
        return serverIP;
    }

	public static int getSocketPort() {
        return socketPort;
    }
	
	//################################
	public static String getNIC() {
        return NIC;
    }
	
	public static int getServicePort() {
        return servicePort;
    }
	
	//#################################
	public static int getSystemInterval() {
        return systemInterval;
    }
	
	public static int getNetworkInterval() {
        return networkInterval;
    }
	
	public static int getRequestInterval() {
        return requestInterval;
    }
	
	//##################################
	public static int getAvgCPU() {
        return avgCPU;
    }
	
	public static int getAvgRAM() {
        return avgRAM;
    }
	
	public static int getAvgDISK() {
        return avgDISK;
    }

	public static int getAvgInbound() {
        return avgInbound;
    }
	
	public static int getAvgOutbound() {
        return avgOutbound;
    }
	
	public static int getAvgRequestPair() {
        return avgRequestPair;
    }
	
	public static double getAvgRequestRatio() {
        return avgRequestRatio;
    }
	
	//##################################
	public static int getThresholdCPU() {
        return thresholdCPU;
    }
	
	public static int getThresholdRAM() {
        return thresholdRAM;
    }
	
	public static int getThresholdDISK() {
        return thresholdDISK;
    }

	public static int getThresholdInbound() {
        return thresholdInbound;
    }
	
	public static int getThresholdOutbound() {
        return thresholdOutbound;
    }
	
	public static int getThresholdRequestPair() {
        return thresholdRequestPair;
    }
	
	public static double getThresholdRequestRatio() {
        return thresholdRequestRatio;
    }
	
	//######################## Test #####
	public static void printAll()
	{
		System.out.println(getRemoteserver());
		System.out.println(getSocketPort());
		System.out.println(getNIC());
		System.out.println(getServicePort());
		System.out.println(getSystemInterval());
		System.out.println(getNetworkInterval());
		System.out.println(getRequestInterval());
		System.out.println(getAvgCPU());
		System.out.println(getAvgRAM());
		System.out.println(getAvgDISK());	
		System.out.println(getAvgInbound());
		System.out.println(getAvgOutbound());
		System.out.println(getAvgRequestPair());
		System.out.println(getAvgRequestRatio());
		System.out.println(getThresholdCPU());	
		System.out.println(getThresholdRAM());
		System.out.println(getThresholdDISK());
		System.out.println(getThresholdInbound());
		System.out.println(getThresholdOutbound());
		System.out.println(getThresholdRequestPair());			
		System.out.println(getThresholdRequestRatio());			
	}
}