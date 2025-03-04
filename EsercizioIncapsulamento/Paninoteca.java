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

// Classe Menu: gestisce l'input dell'utente e la creazione del piatto speciale
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
        System.out.println("\nInserisci un ingrediente: ");
        String ingr = scanner.nextLine().toLowerCase();

        piatto.aggiungiIngrediente(ingr);

        piatto.stampaDettagli();
    }

    private void stampaLista(String titolo, String[] lista) {
        System.out.println("\n" + titolo);
        for (String ingr : lista) {
            System.out.println("- " + ingr);
        }
    }
}

// Classe PiattoSpeciale: incapsula ingredienti e prezzo
class PiattoSpeciale {
    private String[] ingredientiPrivati = { "pomodoro", "cipolla", "maionese", "ketchup" };
    private Double[] prezziPrivati = { 1.50, 35.25, 0.05, 10.00 };
    private String ingredienteSelezionato;

    // Metodo per controllare se l'ingrediente è valido
    private int checkIngrediente(String ingrediente) {
        int indice = Arrays.asList(ingredientiPrivati).indexOf(ingrediente);
        return indice; 
    }

    // Metodo per aggiungere un ingrediente e aggiornare il prezzo
    public void aggiungiIngrediente(String ingrediente) {
        this.ingredienteSelezionato = ingrediente;
    }

    // Metodo per stampare i dettagli del piatto
    public void stampaDettagli() {
        int indice = checkIngrediente(this.ingredienteSelezionato);
        if (indice > -1) {
            System.out.println("\n--- Dettagli del Piatto Speciale ---");
            System.out.print("Ingrediente selezionato: " + ingredienteSelezionato);
            System.out.println("Prezzo: " + prezziPrivati[indice]);
        } else {
            System.out.println("Ingrediente non validop");
        }

    }
}
