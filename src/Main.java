import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String path = "C:\\Users\\aragulin\\IdeaProjects\\AccessLogParser\\src\\resources\\access.log";
        File file = new File(path);

        if (!file.exists()) {
            System.out.println("Файл не найден по пути: " + path);
            return;
        }

        int totalLines = 0;
        int googleBotCount = 0;
        int yandexBotCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Проверка длины строки
                if (line.length() > 1024) {
                    throw new LineTooLongException(
                            "Найдена строка длиннее 1024 символов: длина = " + line.length() + ". Обработка прекращена."
                    );
                }

                totalLines++;

                // Парсим строку, выделяем User-Agent
                String[] quoteParts = line.split("\"");
                if (quoteParts.length < 6) continue;

                String userAgent = quoteParts[5];

                int openBracketIndex = userAgent.indexOf('(');
                int closeBracketIndex = userAgent.indexOf(')');
                if (openBracketIndex == -1 || closeBracketIndex == -1 || closeBracketIndex <= openBracketIndex) continue;

                String firstBrackets = userAgent.substring(openBracketIndex + 1, closeBracketIndex);
                String[] parts = firstBrackets.split(";");
                if (parts.length < 2) continue;

                String secondPart = parts[1].trim();
                int slashIndex = secondPart.indexOf('/');
                String botName = (slashIndex != -1) ? secondPart.substring(0, slashIndex) : secondPart;

                if (botName.equalsIgnoreCase("Googlebot")) googleBotCount++;
                else if (botName.equalsIgnoreCase("YandexBot")) yandexBotCount++;
            }
        } catch (LineTooLongException e) {
            System.err.println("Ошибка: " + e.getMessage());
            return;
        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return;
        }

        if (totalLines == 0) {
            System.out.println("Файл пуст или не содержит валидных данных.");
            return;
        }

        double googlePercent = (googleBotCount * 100.0) / totalLines;
        double yandexPercent = (yandexBotCount * 100.0) / totalLines;

        System.out.printf("Доля запросов от Googlebot: %.2f%% (%d из %d)%n", googlePercent, googleBotCount, totalLines);
        System.out.printf("Доля запросов от YandexBot: %.2f%% (%d из %d)%n", yandexPercent, yandexBotCount, totalLines);
    }
}
