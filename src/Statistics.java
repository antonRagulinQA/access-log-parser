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

    // Новое поле: множество адресов страниц с кодом 200
    private Set<String> existingPages = new HashSet<>();

    // Новое поле: счетчик операционных систем
    private Map<String, Integer> osCount = new HashMap<>();

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

        // Добавляем адрес страницы, если код ответа 200
        if (code == 200) {
            existingPages.add(entry.getUrl());
        }

        // Подсчет операционной системы
        String os = entry.getAgent() != null ? entry.getAgent().getOs() : null;
        if (os != null) {
            osCount.put(os, osCount.getOrDefault(os, 0) + 1);
        }

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

    /**
     * Возвращает множество всех существующих страниц сайта (адреса с кодом 200)
     */
    public Set<String> getExistingPages() {
        return Collections.unmodifiableSet(existingPages);
    }

    /**
     * Возвращает статистику операционных систем в виде Map,
     * где ключ — название ОС, значение — доля от 0 до 1.
     */
    public Map<String, Double> getOsStatistics() {
        Map<String, Double> osStats = new HashMap<>();
        int totalOsCount = osCount.values().stream().mapToInt(Integer::intValue).sum();

        if (totalOsCount == 0) {
            return osStats; // пустой результат, если нет данных
        }

        for (Map.Entry<String, Integer> entry : osCount.entrySet()) {
            double ratio = (double) entry.getValue() / totalOsCount;
            osStats.put(entry.getKey(), ratio);
        }

        return osStats;
    }
}
