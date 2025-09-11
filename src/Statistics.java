import java.net.URI;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.util.*;

public class Statistics {

    private int totalRequests = 0;
    private ZonedDateTime minTime = null;
    private ZonedDateTime maxTime = null;

    private int botCount = 0;
    private int nonBotRequests = 0;
    private Set<String> uniqueNonBotIPs = new HashSet<>();

    private Map<String, Integer> requestsByMethod = new HashMap<>();
    private Map<Integer, Integer> requestsByCode = new HashMap<>();

    private int errorRequests = 0;

    private Set<String> existingPages = new HashSet<>();
    private Set<String> nonExistingPages = new HashSet<>();

    private Map<String, Integer> osCount = new HashMap<>();
    private Map<String, Integer> browserCount = new HashMap<>();

    private long totalResponseSize = 0;

    private Map<Integer, Integer> requestsByHourUTC = new HashMap<>();
    private Map<String, Integer> requestsByIP = new HashMap<>();

    private Map<Long, Integer> requestsBySecond = new HashMap<>();
    private Set<String> referrerDomains = new HashSet<>();

    private Map<String, Integer> nonBotRequestsByIP = new HashMap<>();

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

            nonBotRequestsByIP.put(entry.getIpAddr(),
                    nonBotRequestsByIP.getOrDefault(entry.getIpAddr(), 0) + 1);
        }

        String method = entry.getMethod().toString();
        requestsByMethod.put(method, requestsByMethod.getOrDefault(method, 0) + 1);

        int code = entry.getCode();
        requestsByCode.put(code, requestsByCode.getOrDefault(code, 0) + 1);

        if (code >= 400 && code < 600) {
            errorRequests++;
        }

        if (code == 200) {
            existingPages.add(entry.getUrl());
        } else if (code == 404) {
            nonExistingPages.add(entry.getUrl());
        }

        UserAgent agent = entry.getAgent();
        if (agent != null) {
            String os = agent.getOs();
            if (os != null) {
                osCount.put(os, osCount.getOrDefault(os, 0) + 1);
            }
            String browser = agent.getBrowser();
            if (browser != null) {
                browserCount.put(browser, browserCount.getOrDefault(browser, 0) + 1);
            }
        }

        totalResponseSize += entry.getResponseSize();

        int hour = entryTime.withZoneSameInstant(ZoneOffset.UTC).getHour();
        requestsByHourUTC.put(hour, requestsByHourUTC.getOrDefault(hour, 0) + 1);

        requestsByIP.put(entry.getIpAddr(), requestsByIP.getOrDefault(entry.getIpAddr(), 0) + 1);

        long epochSecond = entryTime.toEpochSecond();
        requestsBySecond.put(epochSecond, requestsBySecond.getOrDefault(epochSecond, 0) + 1);

        String referer = entry.getReferer();
        if (referer != null) {
            try {
                URI uri = new URI(referer);
                String domain = uri.getHost();
                if (domain != null) {
                    referrerDomains.add(domain);
                }
            } catch (Exception ignored) {}
        }
    }

    public double getAverageVisitsPerHour() {
        if (minTime == null || maxTime == null) return 0;
        double hours = (double)(maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600.0;
        if (hours <= 0) return 0;
        return (double) nonBotRequests / hours;
    }

    public double getAverageErrorRequestsPerHour() {
        if (minTime == null || maxTime == null) return 0;
        double hours = (double)(maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600.0;
        if (hours <= 0) return 0;
        return (double) errorRequests / hours;
    }

    public double getAverageVisitsPerUser() {
        if (uniqueNonBotIPs.isEmpty()) return 0;
        return (double) nonBotRequests / uniqueNonBotIPs.size();
    }
    public int getEntriesCount() {
        return totalRequests;
    }

    public int getBotCount() {
        return botCount;
    }

    public double getTrafficRate() {
        if (minTime == null || maxTime == null) return 0;
        double hours = (maxTime.toEpochSecond() - minTime.toEpochSecond()) / 3600.0;
        if (hours <= 0) return 0;
        return (double) totalResponseSize / hours;
    }

}
