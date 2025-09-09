public class UserAgent {
    private final String browser;
    private final String os;
    private final String userAgentString;

    public UserAgent(String userAgentString) {
        this.userAgentString = userAgentString;
        this.browser = parseBrowser(userAgentString);
        this.os = parseOs(userAgentString);
    }

    private String parseBrowser(String ua) {
        if (ua.contains("Chrome")) return "Chrome";
        if (ua.contains("Firefox")) return "Firefox";
        if (ua.contains("Safari")) return "Safari";
        if (ua.contains("Edge")) return "Edge";
        return "Unknown";
    }

    private String parseOs(String ua) {
        if (ua.contains("Windows")) return "Windows";
        if (ua.contains("Linux")) return "Linux";
        if (ua.contains("Mac")) return "MacOS";
        if (ua.contains("Android")) return "Android";
        if (ua.contains("iOS")) return "iOS";
        return "Unknown";
    }

    // Добавленный метод
    public boolean isBot() {
        if (this.userAgentString == null || this.userAgentString.isEmpty()) {
            return false;
        }
        String ua = this.userAgentString.toLowerCase();  // Приводим к нижнему регистру
        return ua.contains("bot") ||
                ua.contains("crawler") ||
                ua.contains("spider") ||
                ua.contains("scraper") ||
                ua.contains("googlebot") ||
                ua.contains("bingbot");
    }

    // Геттеры
    public String getBrowser() { return browser; }
    public String getOs() { return os; }
    public String getUserAgentString() { return userAgentString; }

    public String toString() { return browser + " on " + os; }
}
