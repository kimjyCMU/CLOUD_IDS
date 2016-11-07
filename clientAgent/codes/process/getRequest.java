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

public class getRequest extends Thread{
	Thread t;
	
    public getRequest(){		
		t = new SendRequest();
		t.start();
    }
}

class SendRequest extends Thread {
	private RequestInfo req_info;
    
	public void run() {
		String ip = Main.getSystemIP();
		int port = Configuration.getServicePort();
		this.req_info = new RequestInfo(ip, port);
		
//		monitorRequest monitorRequest = new monitorRequest();
		capturePackets packet = new capturePackets(ip);		
		
		
/*		while(true)
		{
			packet.capture();
			
			Sendtoserver(Configuration.REQUEST);
			 
			try{
				Thread.sleep(Configuration.getRequestInterval());
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
 */   }
	
	public void Sendtoserver(int infoType) 
	{
        RequestType request = new RequestType();
        request.SetIsXml(infoType);
		
		request.SetRequestInfo(this.req_info);
		
        Send2Server.Send(request);
    }
}