package datatype;

import java.io.Serializable;

public class RequestType implements Serializable {
    // stands for utilization info
    private static final long serialVersionUID = 1L;
    private int infoType = 0;
    private SystemInfo system_info = null;
	private NetworkInfo network_info = null;
	private RequestInfo request_info = null;
	
	// For the client and/or the server
    public int getType() {
        return this.infoType;
    }
	
	public void SetIsXml(int type) {
        this.infoType = type;
    }
	
	// ### System ###
    public void SetSystemInfo(SystemInfo si) {
        this.system_info = si;
    }

    public SystemInfo getSystemInfo() {
        return this.system_info;
    }
	
	// ### Network ###
	public void SetNetworkInfo(NetworkInfo ni) {
        this.network_info = ni;
    }

    public NetworkInfo getNetworkInfo() {
        return this.network_info;
    }
	
	// ### Request ###
	public void SetRequestInfo(RequestInfo ri) {
        this.request_info = ri;
    }

    public RequestInfo getRequestInfo() {
        return this.request_info;
    }
}