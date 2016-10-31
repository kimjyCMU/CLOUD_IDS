package process;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import configuration.Configuration;
import datatype.*;

public class Send2Server {

    private static DatagramSocket socket;
    private static boolean debug=true;
	private static boolean start;
	

    public Send2Server(DatagramSocket s) {
        socket = s;
    }
    public void init(){
        start = false; 
    }
    
    public static boolean getStart(){
        return start;
    }
	
    public static void Send(RequestType request) {
        try {
            IPPortPair pair = RemoteServer.getIPPort();
            DatagramPacket packet = new DatagramPacket(new byte[0], 0,
                    InetAddress.getByName(pair.getIP()), pair.getPort());

            // these output streams will send information to the server.
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            ObjectOutputStream objectStream = new ObjectOutputStream(
                    byteArrayStream);

            objectStream.writeObject(request);

            // transform the data to bytes in order to use UDP
            byte[] arr = byteArrayStream.toByteArray();
            packet.setData(arr);

            socket.send(packet);
			
			start = true;

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}