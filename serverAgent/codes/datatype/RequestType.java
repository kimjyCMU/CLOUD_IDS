package datatype;

import java.io.Serializable;

public class RequestType implements Serializable {
    // stands for utilization info
    private static final long serialVersionUID = 1L;
    private int infoType = 0;
    private UtilizInfo util_info = null;
	
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