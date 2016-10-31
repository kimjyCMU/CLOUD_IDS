package configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Configuration {
    private static String remote = null;
	private static String NIC = null;
    private static int socketPort;// The port that socket servers are listening
	private static int interval;
	
    public static final int UTILINFO = 1;

    public static void setConfiguration() {
        try {
            File readFile = new File("./Config.txt");
            BufferedReader inFile = new BufferedReader(new FileReader(readFile));
            String sLine = null;

            while ((sLine = inFile.readLine()) != null) {
                String parameter = sLine.split("=")[0];
                String value = sLine.split("=")[1];
				
				if (parameter.equals("remote IP address"))
                    remote = value;
					
		        else if (parameter.equals("NIC"))
                    NIC = value;		
                
                else if (parameter.equals("socket port"))
                    socketPort = Integer.parseInt(value);
					
				else if (parameter.equals("interval"))
                    interval = Integer.parseInt(value);

                sLine = null;
            }
            inFile.close();
			
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String getRemoteserver() {
        return remote;
    }

	public static String getNIC() {
        return NIC;
    }
	
	public static int getSocketPort() {
        return socketPort;
    }
	
	public static int getInterval() {
        return interval;
    }
}