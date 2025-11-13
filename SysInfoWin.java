import java.io.*;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import com.sun.management.OperatingSystemMXBean;

public class SysInfoWin {

    public static void main(String[] args) {
        System.out.println("=== System Info (Java) ===\n");

        // --- ОС ---
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));

        // --- Имя компьютера ---
        System.out.println("Computer Name: " + getHostName());

        // --- Имя пользователя ---
        System.out.println("User: " + System.getProperty("user.name"));

        // --- Архитектура и процессоры ---
        String arch = System.getProperty("os.arch");
        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        int processors = os.getAvailableProcessors();
        System.out.println("Architecture: " + arch);
        System.out.println("Processors: " + processors);

        // --- Память ---
        long totalMB = os.getTotalPhysicalMemorySize() / (1024 * 1024);
        long freeMB = os.getFreePhysicalMemorySize() / (1024 * 1024);
        System.out.printf("RAM: %d MB used / %d MB total%n", totalMB - freeMB, totalMB);
        System.out.printf("Memory Load: %.0f%%%n", (totalMB - freeMB) * 100.0 / totalMB);
        System.out.printf("Virtual Memory (committed): %d MB%n", os.getCommittedVirtualMemorySize() / (1024 * 1024));

        // --- Pagefile ---
        printPagefileInfo();

        // --- Логические диски ---
        System.out.println("\nDrives:");
        for (File root : File.listRoots()) {
            long totalGB = root.getTotalSpace() / (1024L * 1024L * 1024L);
            long freeGB = root.getFreeSpace() / (1024L * 1024L * 1024L);
            System.out.printf("  - %s  %d GB free / %d GB total%n", root.getPath(), freeGB, totalGB);
        }
    }

    private static String getHostName() {
        try { return InetAddress.getLocalHost().getHostName(); }
        catch (Exception e) { return "unknown"; }
    }

    private static void printPagefileInfo() {
        try {
            Process proc = Runtime.getRuntime().exec("wmic pagefile get AllocatedBaseSize,CurrentUsage");
            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            reader.readLine(); // пропускаем заголовок
            String line = reader.readLine();
            if (line != null && !line.isBlank()) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    System.out.printf("Pagefile: %s MB used / %s MB total%n", parts[1], parts[0]);
                }
            }
        } catch (Exception e) {
            System.out.println("Pagefile info: not available");
        }
    }
}