import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class Statistics {
    private int totalRequests = 0;
    private int botCount = 0;

    private ZonedDateTime minTime = null;
    private ZonedDateTime maxTime = null;

    private Map<HttpMethod, Integer> requestsByMethod = new HashMap<>();
    private Map<Integer, Integer> requestsByCode = new HashMap<>();
    private long totalResponseSize = 0;
    private Map<Integer, Integer> requestsByHourUTC = new HashMap<>();
    private Map<String, Integer> requestsByIP = new HashMap<>();

    public void addEntry(LogEntry entry) {
        ZonedDateTime entryTime = entry.getTime();

        totalRequests++;

        if (minTime == null || entryTime.isBefore(minTime)) minTime = entryTime;
        if (maxTime == null || entryTime.isAfter(maxTime)) maxTime = entryTime;


        if (entry.getAgent() != null && entry.getAgent().isBot()) {
            botCount++;
        }

        HttpMethod method = entry.getMethod();
        requestsByMethod.put(method, requestsByMethod.getOrDefault(method, 0) + 1);

        int code = entry.getResponseCode();
        requestsByCode.put(code, requestsByCode.getOrDefault(code, 0) + 1);

        // Суммарный размер
        totalResponseSize += entry.getResponseSize();

        int hourUTC = entryTime.withZoneSameInstant(ZoneOffset.UTC).getHour();
        requestsByHourUTC.put(hourUTC, requestsByHourUTC.getOrDefault(hourUTC, 0) + 1);

        String ip = entry.getIpAddr();
        requestsByIP.put(ip, requestsByIP.getOrDefault(ip, 0) + 1);
    }

    public int getEntriesCount() {
        return totalRequests;
    }

    public int getBotCount() {
        return botCount;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null) return 0.0;
        long seconds = java.time.Duration.between(minTime, maxTime).getSeconds();
        double hours = seconds / 3600.0;
        if (hours <= 0) return totalResponseSize;
        return totalResponseSize / hours;
    }
}
