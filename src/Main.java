import java.io.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        String path = "C:\\Users\\aragulin\\IdeaProjects\\AccessLogParser\\src\\resources\\access.log";

        File file = new File(path);

        // Проверяем, существует ли файл
        if (!file.exists()) {
            System.out.println("Файл не найден по пути: " + path);
            return;
        }

        // Переменные для статистики
        int totalLines = 0;
        int minLength = Integer.MAX_VALUE;  // Инициализируем большим значением
        int maxLength = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                int len = line.length();

                // Проверка длины строки: если > 1024, выбрасываем исключение
                if (len > 1024) {
                    throw new LineTooLongException(
                            "Найдена строка длиннее 1024 символов: " + len + " символов. Обработка прекращена."
                    );
                }

                // Обновляем статистику
                totalLines++;
                minLength = Math.min(minLength, len);
                maxLength = Math.max(maxLength, len);
            }
        } catch (LineTooLongException e) {
            // Особая обработка для нашего исключения: выводим сообщение и прекращаем
            System.err.println("Ошибка: " + e.getMessage());
            return;  // Завершение программы
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            return;
        }

        // Вывод результатов (только если весь файл обработан успешно)
        System.out.println("Общее количество строк в файле: " + totalLines);
        System.out.println("Длина самой короткой строки: " + minLength + " символов");
        System.out.println("Длина самой длинной строки: " + maxLength + " символов");
    }
}