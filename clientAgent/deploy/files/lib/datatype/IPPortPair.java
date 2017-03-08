package datatype;

import java.io.Serializable;

public class IPPortPair implements Serializable {
    private String ip;
    private int port;

    public IPPortPair(String s, int t) {
        this.ip = s;
        this.port = t;
    }

    public String getIP() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }
}
