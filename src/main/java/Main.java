import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Введите число:");
        int firstNumber = new Scanner(System.in).nextInt();
        int secondNumber = new Scanner(System.in).nextInt();
        int multiply = (firstNumber * secondNumber);
        System.out.println("Произведение чисел: " + multiply);
        int sum = (firstNumber + secondNumber);
        System.out.println("Сумма чисел: " + sum);
        int minus  = (firstNumber - secondNumber);
        System.out.println("Сумма чисел: " + sum);
        double divide = (double) (firstNumber + secondNumber);
        System.out.println("Частное двух чисел: "+ divide);


    }
}
