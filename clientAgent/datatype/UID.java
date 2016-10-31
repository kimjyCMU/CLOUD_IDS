package datatype;

import java.io.Serializable;

public class UID implements Serializable {
    /**
  * 
  */
    private static final long serialVersionUID = 1L;
    private String routerIP;
    private String pmIP;
    private String vmIP;

    public UID(String ip1, String ip2, String ip3) {
        this.routerIP = ip1;
        this.pmIP = ip2;
        this.vmIP = ip3;

    }

    public String getrouterIP() {
        return this.routerIP;
    }

    public String getpmIP() {
        return this.pmIP;
    }

    public String getvmIP() {
        return this.vmIP;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pmIP == null) ? 0 : pmIP.hashCode());
        result = prime * result
                + ((routerIP == null) ? 0 : routerIP.hashCode());
        result = prime * result + ((vmIP == null) ? 0 : vmIP.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UID other = (UID) obj;
        if (pmIP == null) {
            if (other.pmIP != null)
                return false;
        } else if (!pmIP.equals(other.pmIP))
            return false;
        if (routerIP == null) {
            if (other.routerIP != null)
                return false;
        } else if (!routerIP.equals(other.routerIP))
            return false;
        if (vmIP == null) {
            if (other.vmIP != null)
                return false;
        } else if (!vmIP.equals(other.vmIP))
            return false;
        return true;
    }

}