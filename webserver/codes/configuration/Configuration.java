package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class Configuration {
    private static String dbIP = null;
    private static int dbPort;
	private static String dbName=null;
	private static String collName=null;
		
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

				if(parameter.equals("DB IP address"))
					dbIP = value;
				
				else if(parameter.equals("DB port"))
					dbPort = Integer.parseInt(value);
				
				else if(parameter.equals("DB name"))
					dbName = value;

				else if(parameter.equals("Collection name"))
					collName = value;
					
				sLine = null;
			}
			inFile.close();
		}
		catch(Exception ex)
		{
	    	ex.printStackTrace();
		}
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
	
	public static String getCollName(){
    	return collName;
    }
}