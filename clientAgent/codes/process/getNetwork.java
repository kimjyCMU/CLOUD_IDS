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
	double oldIn = 0;
	double oldOut = 0;
    
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
					
			if(oldIn != this.network_info.netin || oldOut != this.network_info.netout)
			{
				System.out.println("\n\n <================ Update Network ==============>");
				Sendtoserver(Configuration.NETWORK);
				oldIn = this.network_info.netin;
				oldOut = this.network_info.netout;
			}
								 
			 // Check Unit action
			 boolean flag = false;
			 
			if(this.network_info.netin > Configuration.getThresholdInbound() || this.network_info.netout > Configuration.getThresholdOutbound())
					flag = true;
				
			if((this.network_info.netin > Configuration.getThresholdInbound() * 0.5) && (this.network_info.netout * Configuration.getThresholdInOutRatio() < this.network_info.netin)
					flag = true;
			
			unitAction ua = new unitAction();
			ua.setUnitAction(Configuration.NETWORK, flag);
			
			flag = false;
			 
			if(this.network_info.netout > Configuration.getThresholdOutbound() * 0.5 && this.network_info.netin * Configuration.getThresholdInOutRatio() < this.network_info.netout)
					flag = true;
			
			ua = new unitAction();
			ua.setUnitAction(Configuration.SPOOFEDNETWORK, flag);
			 
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