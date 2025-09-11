import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogEntry {
    private String ipAddr;
    private ZonedDateTime time;  // Исправлено на ZonedDateTime для учёта часового пояса
    private HttpMethod method;
    private String path;
    private int responseCode;
    private int responseSize;
    private String referer;
    private UserAgent agent;
    private String url;
    private int code;

    // Шаблон для парсинга даты: dd/MMM/yyyy:HH:mm:ss Z (с учетом +0300)
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    // Regex строго для combined log format
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^([^\\s]+) [^\\s]+ [^\\s]+ \\[([^\\]]+)] \"([A-Z]+) ([^\\s]+) [^\"]+\" (\\d{3}) (\\d+|-) \"([^\"]*)\" \"([^\"]*)\"$"
    );

    public LogEntry(String logLine) {
        if (logLine == null || logLine.trim().isEmpty()) {
            throw new IllegalArgumentException("Строка лога пустая или null");
        }

        Matcher matcher = LOG_PATTERN.matcher(logLine.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Неверный формат строки лога: " + logLine);
        }

        ipAddr = matcher.group(1);

        time = ZonedDateTime.parse(matcher.group(2), LOG_DATE_FORMAT);

        method = HttpMethod.valueOf(matcher.group(3));
        path = matcher.group(4);

        responseCode = Integer.parseInt(matcher.group(5));

        String sizeStr = matcher.group(6);
        responseSize = sizeStr.equals("-") ? 0 : Integer.parseInt(sizeStr);

        referer = matcher.group(7).isEmpty() ? "-" : matcher.group(7);
        agent = new UserAgent(matcher.group(8));
    }

    // Геттеры
    public String getIpAddr() { return ipAddr; }
    public ZonedDateTime getTime() { return time; }  // Возвращает ZonedDateTime
    public HttpMethod getMethod() { return method; }
    public String getPath() { return path; }
    public int getResponseCode() { return responseCode; }
    public int getResponseSize() { return responseSize; }
    public String getReferer() { return referer; }
    public UserAgent getAgent() { return agent; }
    public String getUrl() { return url; }
    public int getCode() {return code;
    }

}
