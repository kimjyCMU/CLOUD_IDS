package datatype;

import java.io.Serializable;

public class UAInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String ip;
    public String UA = null;
	
    public UAInfo(String ip) {
        this.ip = ip;
    }
}