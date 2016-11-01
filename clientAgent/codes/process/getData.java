package process;

import configuration.Configuration;
import datatype.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class getData {
    boolean debug = false;
	String pmIP = null;
	
	private UtilizInfo util_info;

    public getData(String pmIP) throws UnknownHostException, IOException{
        this.util_info = new UtilizInfo(pmIP);
		this.pmIP = pmIP;
		
		try{
			while(true)
			{
				SendUtil();
				Thread.sleep(Configuration.getInterval());
			}
		}catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void SendUtil() throws UnknownHostException, IOException {
        GetUtilization getUtil = new GetUtilization();

        this.util_info.cpu = getUtil.getCPUUsage();
        this.util_info.mem = getUtil.getMemoryUsage();
        this.util_info.disk = getUtil.getDiskUsage();

        getUtil.getNetworkUtil();
        this.util_info.netin = getUtil.getInboundTraffic();
        this.util_info.netout = getUtil.getOutboundTraffic();

        System.out.println("IP: " + pmIP
                + ", CPU: " + this.util_info.cpu + ", RAM: "
                + this.util_info.mem + ", Disk: " + this.util_info.disk
                + ", inbound: " + this.util_info.netin + ", outbound: "
                + this.util_info.netout);
			
         Sendtoserver(Configuration.UTILINFO);
    }
	
	public void Sendtoserver(int infoType) {
        /*
         * construct the information to be sent. If this is a initial set up
         * message, we should set isXml to false. Otherwise, set it to be true.
         */
        RequestType request = new RequestType();
        request.SetIsXml(infoType);
		
		if (infoType == Configuration.UTILINFO) {
            request.SetUtilizInfo(this.util_info);
        } 
		
        Send2Server.Send(request);
    }
}