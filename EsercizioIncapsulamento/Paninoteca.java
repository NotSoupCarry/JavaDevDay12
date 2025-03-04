import java.util.Scanner;
import java.util.Arrays;

public class Paninoteca {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Menu menu = new Menu(scanner);

        while (true) {
            menu.ordinaPiatto();

            System.out.println("\nVuoi ordinare un altro piatto? (sì/no)");
            String risposta = scanner.nextLine().toLowerCase();
            if (risposta.equals("no")) {
                System.out.println("Grazie per aver ordinato!");
                break;
            }
        }

        scanner.close();
    }
}

// Classe Menu
class Menu {
    private Scanner scanner;
    public String[] ingredientiPubblici = { "pomodoro", "cipolla", "maionese", "ketchup" };

    public Menu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void ordinaPiatto() {
        System.out.println("\n--- MENU ---");
        stampaLista("Ingredienti pubblici disponibili:", ingredientiPubblici);

        PiattoSpeciale piatto = new PiattoSpeciale();

        // Selezione ingredienti pubblici
        while (true) {
            System.out.println("\nInserisci un ingrediente (digita 'fine' per terminare): ");
            String ingr = scanner.nextLine().toLowerCase();

            if (ingr.equals("fine")) {
                break;
            }

            piatto.aggiungiIngrediente(ingr);
        }

        piatto.stampaDettagli();
    }

    private void stampaLista(String titolo, String[] lista) {
        System.out.println("\n" + titolo);
        for (String ingr : lista) {
            System.out.println("- " + ingr);
        }
    }
}

// Classe PiattoSpeciale
class PiattoSpeciale {
    private String[] ingredientiPrivati = { "pomodoro", "cipolla", "maionese", "ketchup" };
    private Double[] prezziPrivati = { 1.50, 35.25, 0.05, 10.00 };
    private int[] quantità = { 5, 3, 1, 2 };  
    private double prezzoTotale = 0;

    // Metodo per controllare se l'ingrediente è valido e se la quantità è sufficiente
    private int checkIngrediente(String ingrediente) {
        int indice = Arrays.asList(ingredientiPrivati).indexOf(ingrediente);
        if (indice > -1) {
            if (quantità[indice] > 0) {
                return indice; // Ingredienti disponibile
            } else {
                System.out.println("Errore: " + ingrediente + " è esaurito!");
                return -1; // Ingrediente esaurito
            }
        }
        return -1; // Ingrediente non trovato
    }

    // Metodo per aggiungere un ingrediente e aggiornare il prezzo
    public void aggiungiIngrediente(String ingrediente) {
        int indice = checkIngrediente(ingrediente);
        if (indice > -1) {
            // Se l'ingrediente è valido e disponibile, decrementa la quantità e aggiungi al prezzo
            quantità[indice]--;
            prezzoTotale += prezziPrivati[indice];
            System.out.println("Ingrediente aggiunto: " + ingrediente + " (Prezzo: " + prezziPrivati[indice] + " euro, Quantità rimasta: " + quantità[indice] + ")");
        } else {
            System.out.println("Non puoi aggiungere l'ingrediente: " + ingrediente + " OUT OF STOCKS");
        }
    }

    // Metodo per stampare i dettagli del piatto e il prezzo totale
    public void stampaDettagli() {
        System.out.println("\n--- Dettagli del Piatto Speciale ---");
        System.out.println("Prezzo totale: " + prezzoTotale + " euro");
    }
}
