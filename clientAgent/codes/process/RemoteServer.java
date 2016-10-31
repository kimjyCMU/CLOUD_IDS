package process;

import datatype.*;

public class RemoteServer {
    private static String ip;
    private static int port;

    public static synchronized void SetIPPort(String s, int p) {
        ip = s;
        port = p;
    }

    public static synchronized IPPortPair getIPPort() {
        IPPortPair pair = new IPPortPair(ip, port);
        return pair;
    }
}
