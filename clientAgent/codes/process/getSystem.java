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

public class getSystem extends Thread{
	Thread t;
	
    public getSystem(){		
		t = new SendSystem();
		t.start();
    }
}

class SendSystem extends Thread {
	private SystemInfo system_info;
	double oldCPU = 0;
	double oldRAM = 0;
	double oldDisk = 0;
    
	public void run() {
		String ip = Main.getSystemIP();
		this.system_info = new SystemInfo(ip);
		
		while(true)
		{
			monitorSystem monitorSystem = new monitorSystem();

			this.system_info.cpu = monitorSystem.getCPUUsage();
			this.system_info.mem = monitorSystem.getMemoryUsage();
			this.system_info.disk = monitorSystem.getDiskUsage();

			System.out.println("IP: " + ip
					+ ", CPU: " + this.system_info.cpu + ", RAM: "
					+ this.system_info.mem + ", Disk: " + this.system_info.disk);
			
			if(oldCPU != this.system_info.cpu || oldRAM != this.system_info.mem || oldDisk != this.system_info.disk)
			{
				System.out.println("\n\n <================ Update System ==============>");
				Sendtoserver(Configuration.SYSTEM);
				oldCPU = this.system_info.cpu;
				oldRAM = this.system_info.mem;
				oldDisk = this.system_info.disk;
			}			 
			 
			 boolean flag = false;
			 
			 // Check Unit action
			 if(this.system_info.cpu > Configuration.getThresholdCPU() ||
				this.system_info.mem > Configuration.getThresholdRAM() ||
				this.system_info.disk > Configuration.getThresholdDISK())
					flag = true;
			
			unitAction ua = new unitAction();
			ua.setUnitAction(Configuration.SYSTEM, flag);
				
			 
			 try{
				Thread.sleep(Configuration.getSystemInterval());
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
		
		if (infoType == Configuration.SYSTEM) {
            request.SetSystemInfo(this.system_info);
        }   
		
        Send2Server.Send(request);
    }
}