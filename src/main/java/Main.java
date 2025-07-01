import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите текст: ");
        String text = new Scanner(System.in).nextLine();
        System.out.println("Длина текста: " + text.length());
    }
}
