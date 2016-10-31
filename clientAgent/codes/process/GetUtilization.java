package process;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import org.hyperic.sigar.*;
import java.io.File;
import java.text.DecimalFormat;

import configuration.Configuration;

@SuppressWarnings("restriction")
public class GetUtilization extends Thread {
    Thread t;

    static final int kiloByte = 1024;
    static final int megaByte = 1024 * 1024;
    static final int gigaByte = 1024 * 1024 * 1024;

    static double rx = 0;
    static double tx = 0;

    static File readFile;

    OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();

    public double getCPUUsage() {
        double cpu = 0.0;

        cpu = bean.getSystemCpuLoad() * 100;
        cpu = decimalFormat(cpu);

        return cpu;
    }

    public double getMemoryUsage() {
        double totalRAM = bean.getTotalPhysicalMemorySize() / megaByte;
        double freeRAM = bean.getFreePhysicalMemorySize() / megaByte;

        double ram = (totalRAM - freeRAM) / totalRAM * 100;
        ram = decimalFormat(ram);

        return ram;
    }

    public double getDiskUsage() {
        File f = new File("/");

        double totalDisk = f.getTotalSpace() / gigaByte;
        double freeDisk = f.getFreeSpace() / gigaByte;

        double disk = (totalDisk - freeDisk) / totalDisk * 100;
        disk = decimalFormat(disk);

        return disk;
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

    @SuppressWarnings("deprecation")
    public void getNetworkUtil() {
        Sigar sigar = new Sigar();
        try {
            NetInterfaceStat ifstat = sigar.getNetInterfaceStat(Configuration.getNIC());
			
            long rxBytes = ifstat.getRxBytes();
            long txBytes = ifstat.getTxBytes();
			System.out.println("");
			
            t = new TxRxThread();
            t.start();
            Thread.sleep(Configuration.getInterval());

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
}

class TxRxThread extends Thread {
    static long rxNextBytes = 0;
    static long txNextBytes = 0;

    public void run() {
        try {
            Thread.sleep(Configuration.getInterval()-150);
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