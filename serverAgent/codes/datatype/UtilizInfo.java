package datatype;

import java.io.Serializable;

public class UtilizInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    // cpu, mem, disk are represented by percentage while netin and netout are
    // actual numbers
    public double cpu;
    public double mem;
    public double disk;
    public double netin;
    public double netout;
    public String pmIPaddr;

    public UtilizInfo(String pmIPaddr) {
        this.pmIPaddr = pmIPaddr;
        this.cpu = 0;
        this.mem = 0;
        this.disk = 0;
        this.netin = 0;
        this.netout = 0;
    }
}