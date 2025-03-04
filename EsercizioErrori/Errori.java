import java.util.Scanner;

public class Errori {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final double PREZZOPERCIOCCOLATINO = 1.50; // Prezzo per ogni cioccolatino

        try {
            System.out.println("Inserisci la quantità di cioccolatini che desideri acquistare:");

            String input = scanner.nextLine();
            int quantita = Integer.parseInt(input);  // Potrebbe lanciare una NumberFormatException

            // Controllare che la quantità non sia negativa
            if (quantita < 0) {
                throw new Exception("La quantità non può essere negativa!");
            }

            // Calcolare il costo totale
            double costoTotale = quantita * PREZZOPERCIOCCOLATINO;
            System.out.println("Il costo totale per " + quantita + " cioccolatini è: " + costoTotale + " euro");

        } catch (NumberFormatException e) {
            // Gestire il caso in cui l'utente non inserisca un numero valido
            System.out.println("Errore: Devi inserire un numero valido!");
        } catch (Exception e) {
            // Gestire il caso in cui la quantità sia negativa
            System.out.println("Errore: " + e.getMessage());
        } finally {
            // Blocchi che vengono eseguiti sempre, indipendentemente dal successo o errore
            System.out.println("\nfinalmente è finito");
            scanner.close();
        }
    }
}
