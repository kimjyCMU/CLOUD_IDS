package datatype;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestInfo implements Serializable {
    private static final long serialVersionUID = 1L;
	public String myIP = null;
	public String neighIP = null;
    public int port;
    public int req;
	public int res;
	public double ratio;
	private ArrayList<neighborInfo> neighbor = new ArrayList<neighborInfo>();
	
    public RequestInfo(String myIP, String neighIP, int port, int req, int res, double ratio) 
	{
        this.myIP = myIP;
		this.neighIP = neighIP;
		this.port = port;
        this.req = req;
        this.res = res;
		this.ratio = ratio;
    }
}