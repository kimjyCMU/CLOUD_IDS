package datatype;

import java.io.Serializable;

public class numReqRes implements Serializable 
{
    private static final long serialVersionUID = 1L;

	public int req = 0;
	public int res = 0;
	
	public numReqRes(int req, int res)
	{
		this.req = req;
		this.res = res;
	}
	
	public void setReq(int req)
	{
		this.req = req;
	}
	
	public void setRes(int res)
	{
		this.res = res;
	}
	
	public int getReq()
	{
		return req;
	}
	
	public int getRes()
	{
		return res;
	}
}