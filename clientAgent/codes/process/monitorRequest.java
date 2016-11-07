package process; 

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;

import java.io.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;

import java.util.ArrayList;  
import java.util.Map;  
import java.util.*;  

import configuration.Configuration;

@SuppressWarnings("restriction")
public class monitorRequest {

	static String reqFileName = null;
	static String resFileName = null;
	
	static ArrayList<String> reqIP = new ArrayList<String>();
	static ArrayList<String> resIP = new ArrayList<String>();
	
	static HashMap<String, Integer> reqIPcnt = new HashMap<String, Integer>();
	
    public monitorRequest() 
	{
		reqIP.clear();
		resIP.clear();
		
		reqFileName = Configuration.getReqFile();
		resFileName = Configuration.getResFile();
	}

	public void analyze()
	{
		try
		{
			File reqFile = new File(reqFileName);
			BufferedReader bfReq = new BufferedReader(new FileReader(reqFile));			
			String reqLine = null;
			
			File resFile = new File(resFileName);
			BufferedReader bfRes = new BufferedReader(new FileReader(resFile));			
			String resLine = null;
			
			while((reqLine = bfReq.readLine()) != null)
			{
				Iterator<String> it = reqIPcnt.keySet().iterator();
				boolean flag = false;
				
				while(it.hasNext())
				{
					String key = it.next();
						
					if(reqLine.equals(key))
					{
						int value = reqIPcnt.get(key);
						value++;
						
						reqIPcnt.put(key, value);
						flag = true;
					}
				}
				
				if(!flag) // this is the first IP shown
				{
					reqIPcnt.put(reqLine, 0);
				}
			}
			
			Iterator<String> it = reqIPcnt.keySet().iterator();
			// print
			while(it.hasNext())
			{
				String key = it.next();
				int value = reqIPcnt.get(key);

				System.out.println(">>> MAP " + key + " > " + value);
			}
		} catch(Exception ex){
	    	ex.printStackTrace();
		}	
	}
	

    public double decimalFormat(double usage) {
        DecimalFormat format = new DecimalFormat();
        format.applyLocalizedPattern("0.##");

        double result = Double.parseDouble(format.format(usage));
        return result;
    }
}