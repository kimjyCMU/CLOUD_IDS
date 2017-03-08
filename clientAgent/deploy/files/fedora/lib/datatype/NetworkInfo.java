package datatype;

import java.io.Serializable;

public class NetworkInfo implements Serializable {
    private static final long serialVersionUID = 1L;
	public String ip;
    public double netin;
    public double netout;


    public NetworkInfo(String ip) {
        this.ip = ip;
        this.netin = 0;
        this.netout = 0;
    }
}