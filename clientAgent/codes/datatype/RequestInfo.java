package datatype;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestInfo implements Serializable {
    private static final long serialVersionUID = 1L;
	public String ip = null;
    public int port;
    public int total_req;
	public int total_res;
	public double total_ratio;
	private ArrayList<neighborInfo> neighbor = new ArrayList<neighborInfo>();
	
    public RequestInfo(String ip, int port) 
	{
        this.ip = ip;
		this.port = port;
        this.total_req = 0;
        this.total_res = 0;
		this.total_ratio = 0;
    }
}

class neighborInfo
{
	public String neighIP = null;
	public int req;
	public int res;
	public double ratio;
	
	public neighborInfo(String neighIP, int req, int res, double ratio)
	{
		this.neighIP = neighIP;
		this.req = req;
		this.res = res;
		this.ratio = ratio;
	}
	
	public String getNeighIP()
	{
		return neighIP;
	}
	
	public int getReq()
	{
		return req;
	}
	
	public int getRes()
	{
		return res;
	}
	
	public double getRatio()
	{
		return ratio;
	}
}