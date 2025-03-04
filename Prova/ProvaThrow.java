public class ProvaThrow {
    static void checkAge(int age) {
        if (age < 18) {
            throw new ArithmeticException("Access denied - You must be at least 18 years old.");
        } else {
            System.out.println("Access granted - You are old enough!");
        }
    }

    public static void main(String[] args) {
        checkAge(19);
        checkAge(15); // age < 18 va nel throw e interrompe il programma
        checkAge(122); // questa riga non parte

    }
}
