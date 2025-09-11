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

    private Set<String> existingPages = new HashSet<>();
    private Map<String, Integer> osCount = new HashMap<>();

    private Set<String> nonExistingPages = new HashSet<>();
    private Map<String, Integer> browserCount = new HashMap<>();

    // Новые поля для задания
    private int nonBotRequests = 0;
    private int errorRequests = 0;
    private Set<String> uniqueNonBotIPs = new HashSet<>();

    public void addEntry(LogEntry entry) {
        ZonedDateTime entryTime = entry.getTime();

        totalRequests++;

        if (minTime == null || entryTime.isBefore(minTime)) minTime = entryTime;
        if (maxTime == null || entryTime.isAfter(maxTime)) maxTime = entryTime;

        boolean isBot = entry.getAgent() != null && entry.getAgent().isBot();

        if (isBot) {
            botCount++;
        } else {
            nonBotRequests++;
            uniqueNonBotIPs.add(entry.getIpAddr());
        }

        HttpMethod method = entry.getMethod();
        requestsByMethod.put(method, requestsByMethod.getOrDefault(method, 0) + 1);

        int code = entry.getResponseCode();
        requestsByCode.put(code, requestsByCode.getOrDefault(code, 0) + 1);

        if (code >= 400 && code < 600) {
            errorRequests++;
        }

        if (code == 200) {
            existingPages.add(entry.getUrl());
        }

        if (code == 404) {
            nonExistingPages.add(entry.getUrl());
        }

        String os = entry.getAgent() != null ? entry.getAgent().getOs() : null;
        if (os != null) {
            osCount.put(os, osCount.getOrDefault(os, 0) + 1);
        }

        String browser = entry.getAgent() != null ? entry.getAgent().getBrowser() : null;
        if (browser != null) {
            browserCount.put(browser, browserCount.getOrDefault(browser, 0) + 1);
        }

        totalResponseSize += entry.getResponseSize();

        int hourUTC = entryTime.withZoneSameInstant(ZoneOffset.UTC).getHour();
        requestsByHourUTC.put(hourUTC, requestsByHourUTC.getOrDefault(hourUTC, 0) + 1);

        String ip = entry.getIpAddr();
        requestsByIP.put(ip, requestsByIP.getOrDefault(ip, 0) + 1);
    }

    private double getHoursInLog() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0.0;
        }
        long seconds = java.time.Duration.between(minTime, maxTime).getSeconds();
        return seconds / 3600.0;
    }

    public double getAverageVisitsPerHour() {
        double hours = getHoursInLog();
        if (hours == 0) {
            return 0.0;
        }
        return nonBotRequests / hours;
    }

    public double getAverageErrorRequestsPerHour() {
        double hours = getHoursInLog();
        if (hours == 0) {
            return 0.0;
        }
        return (double) errorRequests / hours;
    }

    public double getAverageVisitsPerUser() {
        if (uniqueNonBotIPs.isEmpty()) {
            return 0.0;
        }
        return (double) nonBotRequests / uniqueNonBotIPs.size();
    }

    // Ваши уже реализованные методы

    public Set<String> getExistingPages() {
        return Collections.unmodifiableSet(existingPages);
    }

    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStats = new HashMap<>();
        int totalOsCount = osCount.values().stream().mapToInt(Integer::intValue).sum();

        if (totalOsCount == 0) {
            return osStats;
        }

        for (Map.Entry<String, Integer> entry : osCount.entrySet()) {
            osStats.put(entry.getKey(), (double) entry.getValue() / totalOsCount);
        }

        return osStats;
    }

    public Set<String> getNonExistingPages() {
        return Collections.unmodifiableSet(nonExistingPages);
    }

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> browserStats = new HashMap<>();
        int totalBrowserCount = browserCount.values().stream().mapToInt(Integer::intValue).sum();

        if (totalBrowserCount == 0) {
            return browserStats;
        }

        for (Map.Entry<String, Integer> entry : browserCount.entrySet()) {
            browserStats.put(entry.getKey(), (double) entry.getValue() / totalBrowserCount);
        }

        return browserStats;
    }

    public int getEntriesCount() {
        return totalRequests;
    }

    public int getBotCount() {
        return botCount;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null || minTime.equals(maxTime)) {
            return 0.0;
        }
        long seconds = java.time.Duration.between(minTime, maxTime).getSeconds();
        double hours = seconds / 3600.0;
        if (hours == 0) {
            return 0.0;
        }
        return (double) totalResponseSize / hours;
    }
}
