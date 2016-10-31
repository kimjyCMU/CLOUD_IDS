package datatype;

import java.io.Serializable;
import java.util.ArrayList;

public class RequestType implements Serializable {
    // infoType: 0 stands for heartbeat, 1 stands for relationship info, 2
    // stands for utilization info
    private static final long serialVersionUID = 1L;
    private int infoType = 0;
    private UtilizInfo util_info = null;
    private UID id;
    private UID dstUID;
    private IPPortPair remoteserver;

    public void setremote(IPPortPair pair) {
        this.remoteserver = pair;
    }

    public IPPortPair getRemote() {
        return this.remoteserver;
    }

    public void setDstUID(UID id) {
        this.dstUID = id;
    }

    public UID getDstUID() {
        return this.dstUID;
    }

    public void setUID(UID id) {
        this.id = id;
    }

    public UID getUID() {
        return this.id;
    }

    public void SetUtilizInfo(UtilizInfo ui) {
        this.util_info = ui;
    }

    public void SetIsXml(int type) {
        this.infoType = type;
    }

    public UtilizInfo getUtil() {
        return this.util_info;
    }

    public int getType() {
        return this.infoType;
    }

}