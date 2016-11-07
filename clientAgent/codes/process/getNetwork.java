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

public class getNetwork extends Thread{
	Thread t;
	
    public getNetwork(){		
		t = new SendNetwork();
		t.start();
    }
}

class SendNetwork extends Thread {
	private NetworkInfo network_info;
    
	public void run() {
		String ip = Main.getSystemIP();
		this.network_info = new NetworkInfo(ip);
		
		while(true)
		{
			monitorNetwork monitorNetwork = new monitorNetwork();
			monitorNetwork.getNetwork();

			this.network_info.netin = monitorNetwork.getInboundTraffic();
			this.network_info.netout = monitorNetwork.getOutboundTraffic();

			System.out.println("IP: " + ip
					+ ", inbound: " + this.network_info.netin + ", outbound: "
					+ this.network_info.netout);
					
			 Sendtoserver(Configuration.NETWORK);
			 
			try{
				Thread.sleep(Configuration.getNetworkInterval());
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
	
	public void Sendtoserver(int infoType) 
	{
        RequestType request = new RequestType();
        request.SetIsXml(infoType);
		
		if (infoType == Configuration.NETWORK) {
            request.SetNetworkInfo(this.network_info);
        }   
		
        Send2Server.Send(request);
    }
}