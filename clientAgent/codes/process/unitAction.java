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


class unitAction extends Thread {
	private boolean flag;
	private static String UA = "normal";
	Thread t;
	
	public void setUnitAction(int type, boolean flag)
	{
		this.flag = flag;
		
		if(flag)
		{
			if(type == Configuration.SYSTEM)
				UA = Configuration.getSystemUA();
			
			if(type == Configuration.NETWORK)
				UA = Configuration.getNetworkUA();
			
			if(type == Configuration.REQUESTPAIR)
				UA = Configuration.getRequestPairUA();
			
			if(type == Configuration.REQUESTRATIO)
				UA = Configuration.getRequestRatioUA();						
		}
			
		t = new sendUA (UA);
		t.start(); 
	}
}

class sendUA extends Thread 
{
	String ip = Main.getSystemIP();
	UAInfo UA_info = new UAInfo(ip);
	
	public sendUA(String UA)
	{
		this.UA_info.UA = UA;		
	}

	public void run() 
	{						
		Sendtoserver(Configuration.UNITACTION);
		System.out.println("## UA >> " + ip + " : " + UA_info.UA);
	}	
	
	public void Sendtoserver(int infoType) 
	{
        RequestType request = new RequestType();
        request.SetIsXml(infoType);
		
		request.SetUAInfo(this.UA_info);   
		
        Send2Server.Send(request);
    }
}