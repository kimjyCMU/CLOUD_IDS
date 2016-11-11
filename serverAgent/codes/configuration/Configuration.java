package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Configuration {
	private static boolean isPublicIP = false;
    public static int socketPort;
	
    private static String dbIP = null;
    private static int dbPort;
	private static String dbName=null;
	private static String collection=null;
	
	// message codes
	public static final int SYSTEM = 1;
	public static final int NETWORK = 2;
	public static final int REQUEST = 3;
	public static final int UNITACTION = 4;
	
	/*** read a configuration file and assign all the configuration parameters to variables ***/
	public static void setConfiguration()
	{
		try
		{
			File readFile = new File("./Config.txt");
			BufferedReader inFile = new BufferedReader(new FileReader(readFile));
			String sLine = null;
			
			while((sLine = inFile.readLine()) != null)
			{
				String parameter = sLine.split("=")[0];
				String value = sLine.split("=")[1];
								
				if(parameter.equals("isPublicIP"))
					isPublicIP = Boolean.valueOf(value);

				else if(parameter.equals("socket port"))
					socketPort = Integer.parseInt(value);

				else if(parameter.equals("DB IP address"))
					dbIP = value;
					
				else if(parameter.equals("DB port"))
					dbPort = Integer.parseInt(value);
				
				else if(parameter.equals("DB name"))
					dbName = value;				
										
				else if(parameter.equals("collection"))
					collection = value;

				sLine = null;
			}
			inFile.close();
		}
		catch(Exception ex)
		{
	    	ex.printStackTrace();
		}
	}	
	
	/*** return the configuration parameters ***/	
	public static boolean getIsPublicIP(){
        return isPublicIP;
    }
	
	public static int getSocketport(){
    	return socketPort;
    }       
            
    public static String getDBIP(){
    	return dbIP;
    }
    
    public static int getDBPort(){
    	return dbPort;
    }
	
    public static String getDBName(){
    	return dbName;
    }

    public static String getCollection(){
    	return collection;
    }
}
