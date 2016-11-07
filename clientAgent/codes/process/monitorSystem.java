package process;

import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import org.hyperic.sigar.*;
import java.io.File;
import java.text.DecimalFormat;

import configuration.Configuration;

@SuppressWarnings("restriction")
public class monitorSystem{
    static final int kiloByte = 1024;
    static final int megaByte = 1024 * 1024;
    static final int gigaByte = 1024 * 1024 * 1024;

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
}