import java.io.*;

public class Main {
    public static void main(String[] args) {
        Statistics statistics = new Statistics();

        String filePath = "C:\\Users\\aragulin\\IdeaProjects\\AccessLogParser\\src\\resources\\access.log";

        // Проверка существования файла
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Файл не найден: " + filePath + ". Проверьте путь и убедитесь, что файл существует.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;  // Пропускаем пустые строки
                LogEntry entry = new LogEntry(line);
                statistics.addEntry(entry);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + filePath + " (" + e.getMessage() + ")");
            return;
        }

        // Вывод результатов
        System.out.println("Всего записей: " + statistics.getEntriesCount());
        System.out.println("Количество ботов: " + statistics.getBotCount());
        System.out.println("Средний трафик (байты/час): " + statistics.getTrafficRate());
    }
}
