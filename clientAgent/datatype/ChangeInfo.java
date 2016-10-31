package datatype;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ChangeInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public boolean changed = false;// false indicates nothing changes since last
                                   // time
    public boolean relationchange = false;
    public int relation = 0;
    public Set<String> newclients;
    public Set<String> newservers;
    public Set<String> oldclients;
    public Set<String> oldservers;
    public String localhost;
    public String pmIPaddr;

    public ChangeInfo(String pmIPaddr, String ip) {
        this.pmIPaddr = pmIPaddr;
        this.localhost = ip;
        this.newclients = new HashSet<String>();
        this.newservers = new HashSet<String>();
        this.oldclients = new HashSet<String>();
        this.oldservers = new HashSet<String>();
        relation = 0;
        changed = false;
        relationchange = false;
    }
}