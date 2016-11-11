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
    
	public void run() {
		String ip = Main.getSystemIP();
		monitorRequests packet = new monitorRequests(ip);		
				
		while(true)
		{
			packet.capture();
			 
			try{
				Thread.sleep(Configuration.getRequestInterval());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
   }
}