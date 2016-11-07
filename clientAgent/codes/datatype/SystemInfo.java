package datatype;

import java.io.Serializable;

public class SystemInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String ip;
    public double cpu;
    public double mem;
    public double disk;

    public SystemInfo(String ip) {
        this.ip = ip;
        this.cpu = 0;
        this.mem = 0;
        this.disk = 0;
    }
}