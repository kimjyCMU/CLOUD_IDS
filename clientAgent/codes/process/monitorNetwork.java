package process;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import org.hyperic.sigar.*;
import java.io.File;
import java.text.DecimalFormat;

import configuration.Configuration;

@SuppressWarnings("restriction")
public class monitorNetwork extends Thread {
    Thread t;

    static double rx = 0;
    static double tx = 0;

    static File readFile;

    OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();
	
	 @SuppressWarnings("deprecation")
    public void getNetwork() 
	{
        Sigar sigar = new Sigar();
		
        try {
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(Configuration.getNIC());
			
            long rxBytes = ifstat.getRxBytes();
            long txBytes = ifstat.getTxBytes();
			System.out.println("");
			
            t = new TxRxThread();
            t.start();
            Thread.sleep(1000);

			long rxNextBytes = TxRxThread.getNextRx();
            long txNextBytes = TxRxThread.getNextTx();

            rx = rxNextBytes - rxBytes;
            tx = txNextBytes - txBytes;

            t.stop();
        } catch (SigarException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            sigar.close();
        }
	}

    public double decimalFormat(double usage) {
        DecimalFormat format = new DecimalFormat();
        format.applyLocalizedPattern("0.##");

        double result = Double.parseDouble(format.format(usage));
        return result;
    }

    public double getInboundTraffic() {
        return decimalFormat(rx);
    }

    public double getOutboundTraffic() {
        return decimalFormat(tx);
    }
}

class TxRxThread extends Thread {
    static long rxNextBytes = 0;
    static long txNextBytes = 0;

    public void run() {
        try {
            Thread.sleep(1000-150);
            Sigar sigar = new Sigar();
            try {
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(Configuration.getNIC());

                rxNextBytes = ifstat.getRxBytes();
                txNextBytes = ifstat.getTxBytes();
            } catch (SigarException e) {
            } finally {
                sigar.close();
            }
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static long getNextRx() {
        return rxNextBytes;
    }

    public static long getNextTx() {
        return txNextBytes;
    }
}