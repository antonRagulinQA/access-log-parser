import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
                System.out.println("Введите число 1 :");
                int firstNumber = new Scanner(System.in).nextInt();
        System.out.println("Введите число 2 :");
                int secondNumber = new Scanner(System.in).nextInt();
                int multiply = (firstNumber * secondNumber);
                System.out.println("Произведение чисел: " + multiply);
                int sum = (firstNumber + secondNumber);
                System.out.println("Сумма чисел: " + sum);
                int minus  = (firstNumber - secondNumber);
                System.out.println("Разность чисел: " + minus);
                double divide = (double) (firstNumber / secondNumber);
                System.out.println("Частное двух чисел: "+ divide);
            }
        }
