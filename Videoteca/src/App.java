
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Menu.menuPrincipale(scanner);

        scanner.close();
    }
}

// Classe Controlli
class Controlli {
    // Metodo per controllare che l'input stringa non sia vuoto
    public static String controlloInputStringhe(Scanner scanner) {
        String valore;
        do {
            valore = scanner.nextLine().trim();
            if (valore.isEmpty()) {
                System.out.print("Input non valido. Inserisci un testo: ");
            }
        } while (valore.isEmpty());
        return valore;
    }

    // Metodo per controllare l'input intero
    public static Integer controlloInputInteri(Scanner scanner) {
        Integer valore;
        do {
            while (!scanner.hasNextInt()) {
                System.out.print("Devi inserire un numero intero. Riprova ");
                scanner.next();
            }
            valore = scanner.nextInt();
            if (valore < 0) {
                System.out.print("Il numero non può essere negativo. Riprova: ");
            }
        } while (valore < 0);
        return valore;
    }
}

// Classe per la connessione al database
class DBcontext {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/VideotecaDB";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";

    public static Connection connessioneDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

// Classe Film
class Film {
    private int id;
    private String titolo;
    private int anno;
    private boolean disponibile;

    // Costruttore
    public Film(int id, String titolo, int anno) {
        this.id = id;
        this.titolo = titolo;
        this.anno = anno;
        this.disponibile = true; 
    }

    public Film(String titolo, int anno) {
        this(0, titolo, anno);
    }

    public int getId() {
        return id;
    }

    public String getTitolo() {
        return titolo;
    }

    public int getAnno() {
        return anno;
    }

    public boolean isDisponibile() {
        return disponibile;
    }

    public void setDisponibile(boolean disponibile) {
        this.disponibile = disponibile;
    }

    @Override
    public String toString() {
        return titolo + " (" + anno + ")";
    }
}

// Classe Utente
class Utente {
    private int id;
    private String nome;
    private List<Film> filmNoleggiati;

    // costruttore
    public Utente(int id, String nome) {
        this.id = id;
        this.nome = nome;
        this.filmNoleggiati = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void aggiungiNoleggio(Film film) {
        filmNoleggiati.add(film);
    }

    public void elencoNoleggi() {
        System.out.println("Film noleggiati da " + nome + ":");
        if (filmNoleggiati.isEmpty()) {
            System.out.println("Nessun film noleggiato.");
        } else {
            for (Film film : filmNoleggiati) {
                System.out.println("- " + film);
            }
        }
    }
}

// Classe Videoteca
class Videoteca {
    private List<Film> catalogo;
    private List<Utente> utenti;

    // costruttore
    public Videoteca() {
        this.catalogo = new ArrayList<>();
        this.utenti = new ArrayList<>();
    }

    // #region caico dei dati dal db
    // carico dei dati dei film
    public void caricaFilmDaDB() {
        try (Connection conn = DBcontext.connessioneDatabase();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM film")) {

            while (rs.next()) {
                // Carico i dati dal database nel catalogo
                catalogo.add(new Film(rs.getInt("ID"), rs.getString("titolo"), rs.getInt("anno")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // carico dei dati di utenti
    public void caricaUtentiDaDB() {
        try (Connection conn = DBcontext.connessioneDatabase();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM utente")) {

            while (rs.next()) {
                utenti.add(new Utente(rs.getInt("ID"), rs.getString("nome")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // carico dei dati di noleggio
    public void caricaNoleggiDaDB() {
        try (Connection conn = DBcontext.connessioneDatabase();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT u.ID, u.nome, f.ID, f.titolo, f.anno FROM noleggio n " +
                                "JOIN utente u ON n.id_utente = u.ID " +
                                "JOIN film f ON n.id_film = f.ID")) {

            while (rs.next()) {
                int userId = rs.getInt(1);
                Film film = new Film(rs.getInt(3), rs.getString(4), rs.getInt(5));
                for (Utente utente : utenti) {
                    if (utente.getId() == userId) {
                        utente.aggiungiNoleggio(film);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // #endregion

    // #region METODI PER LA GESTIONE DEGLI UTENTE
    public Integer getIdUtenteByName(String nome) {
        try (Connection conn = DBcontext.connessioneDatabase();
                PreparedStatement pstmt = conn.prepareStatement("SELECT ID FROM utente WHERE nome = ?")) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void aggiungiUtente(Utente utente) {
        // Controlla se l'utente esiste già
        if (utenteExists(utente.getNome())) {
            System.out.println("Errore: L'utente \"" + utente.getNome() + "\" è già registrato.");
            return;
        }

        // Se non esiste, lo registra
        try (Connection conn = DBcontext.connessioneDatabase();
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO utente (nome) VALUES (?)")) {
            pstmt.setString(1, utente.getNome());
            pstmt.executeUpdate();
            System.out.println("Utente \"" + utente.getNome() + "\" registrato con successo.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminaUtente(int idUtente) {
        // Rimuovi l'utente dalla lista
        Utente utenteDaEliminare = null;
        for (Utente utente : utenti) {
            if (utente.getId() == idUtente) {
                utenteDaEliminare = utente;
                break;
            }
        }

        if (utenteDaEliminare != null) {
            // Rimuovi anche dal database
            try (Connection conn = DBcontext.connessioneDatabase();
                    PreparedStatement pstmt = conn.prepareStatement("DELETE FROM utente WHERE ID = ?")) {
                pstmt.setInt(1, idUtente);
                pstmt.executeUpdate();
                utenti.remove(utenteDaEliminare); // Rimuovi dall'elenco in memoria
                System.out.println("Utente con ID " + idUtente + " eliminato con successo.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Utente con ID " + idUtente + " non trovato.");
        }
    }

    public void mostraUtenti() {
        System.out.println("\n=== Lista Utenti ===");
        for (Utente utente : utenti) {
            System.out.println("- " + utente);
        }
    }

    private boolean utenteExists(String nome) {
        String query = "SELECT COUNT(*) FROM utente WHERE nome = ?";
        try (Connection conn = DBcontext.connessioneDatabase();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, nome);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Se il conteggio è maggiore di 0, l'utente esiste già
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // #endregion

    // #region METODI PER LA GESTIONE DEI FILM
    public Integer getIdFilmByTitle(String titolo) {
        try (Connection conn = DBcontext.connessioneDatabase();
                PreparedStatement pstmt = conn.prepareStatement("SELECT ID FROM film WHERE titolo = ?")) {
            pstmt.setString(1, titolo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void aggiungiFilm(Film film) {
        if (filmExists(film.getTitolo(), film.getAnno())) {
            System.out.println(
                    "Il film \"" + film.getTitolo() + "\" (" + film.getAnno() + ") è già presente nel catalogo.");
            return;
        }

        try (Connection conn = DBcontext.connessioneDatabase();
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO film (titolo, anno) VALUES (?, ?)")) {
            pstmt.setString(1, film.getTitolo());
            pstmt.setInt(2, film.getAnno());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminaFilm(int idFilm) {
        // Trova il film da eliminare
        Film filmDaEliminare = null;
        for (Film film : catalogo) {
            if (film.getId() == idFilm) {
                filmDaEliminare = film;
                break;
            }
        }

        if (filmDaEliminare != null) {
            // Rimuovi il film dalla lista del catalogo
            catalogo.remove(filmDaEliminare);

            // Rimuovi anche dal database
            try (Connection conn = DBcontext.connessioneDatabase();
                    PreparedStatement pstmt = conn.prepareStatement("DELETE FROM film WHERE ID = ?")) {
                pstmt.setInt(1, idFilm);
                pstmt.executeUpdate();
                System.out.println("Film con ID " + idFilm + " eliminato con successo.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Film con ID " + idFilm + " non trovato.");
        }
    }

    public void mostraFilm() {
        System.out.println("\n=== Catalogo Film ===");
        for (Film film : catalogo) {
            String stato = film.isDisponibile() ? "Disponibile" : "Noleggiato";
            System.out.println("- " + film + " (" + stato + ")");
        }
    }

    private boolean filmExists(String titolo, int anno) {
        String query = "SELECT COUNT(*) FROM film WHERE titolo = ? AND anno = ?";
        try (Connection conn = DBcontext.connessioneDatabase();
                PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, titolo);
            pstmt.setInt(2, anno);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // Se il conteggio è maggiore di 0, il film esiste già
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    // #endregion

    // #region METODI PER LA GESTIONE DEI NOLEGGI
    public void noleggiaFilm(int idUtente, int idFilm) {
        try (Connection conn = DBcontext.connessioneDatabase();
                PreparedStatement pstmt = conn.prepareStatement("SELECT disponibile FROM film WHERE ID = ?")) {

            // Controlla se il film è disponibile
            pstmt.setInt(1, idFilm);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean disponibile = rs.getBoolean("disponibile");
                if (!disponibile) {
                    System.out.println("Il film non è disponibile per il noleggio.");
                    return;
                }
            }

            // Se il film è disponibile, procedi con il noleggio
            try (PreparedStatement insertNoleggio = conn
                    .prepareStatement("INSERT INTO noleggio (id_utente, id_film) VALUES (?, ?)")) {
                insertNoleggio.setInt(1, idUtente);
                insertNoleggio.setInt(2, idFilm);
                insertNoleggio.executeUpdate();
            }

            // Rendi il film non disponibile
            try (PreparedStatement updateDisponibile = conn
                    .prepareStatement("UPDATE film SET disponibile = FALSE WHERE ID = ?")) {
                updateDisponibile.setInt(1, idFilm);
                updateDisponibile.executeUpdate();
            }

            System.out.println("Film noleggiato con successo!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void mostraNoleggiUtente(int idUtente) {
        for (Utente utente : utenti) {
            if (utente.getId() == idUtente) {
                utente.elencoNoleggi();
                return;
            }
        }
        System.out.println("Utente non trovato.");
    }

    public void mostraTuttiINoleggi() {
        System.out.println("\n=== Noleggi di tutti gli utenti ===");
        // Verifica se ci sono utenti
        if (utenti.isEmpty()) {
            System.out.println("Non ci sono utenti registrati.");
            return;
        }
        // Ciclo attraverso tutti gli utenti
        for (Utente utente : utenti) {
            utente.elencoNoleggi(); // Mostra i noleggi per ogni utente
        }
    }
    // #endregion
}

// Classe Menu
class Menu {
    // Menu principale
    public static void menuPrincipale(Scanner scanner) {
        int scelta;
        boolean exitMainMenu = false;
        Videoteca videoteca = new Videoteca();

        // Caricamento dati dal database
        videoteca.caricaFilmDaDB();
        videoteca.caricaUtentiDaDB();
        videoteca.caricaNoleggiDaDB();

        System.out.println("\n==== Benvenuto nella nostra super pazza VideoTeca :v ====");

        while (!exitMainMenu) {
            System.out.println("\n==== Menu Principale ====");
            System.out.println("1. Menu Gestione Utenti");
            System.out.println("2. Menu Gestione Film");
            System.out.println("3. Menu Gestione Noleggi");
            System.out.println("4. Esci");

            System.out.print("Scegli un'opzione (1-4): ");
            scelta = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (scelta) {
                case 1:
                    menuUtente(scanner, videoteca);
                    break;
                case 2:
                    menuFilm(scanner, videoteca);
                    break;
                case 3:
                    menuNoleggi(scanner, videoteca);
                    break;
                case 4:
                    System.out.println("Uscita dal programma.");
                    exitMainMenu = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }

    // Menu per la gestione utente
    public static void menuUtente(Scanner scanner, Videoteca videoteca) {
        int sceltaMenuUtente;
        boolean exitMenuUtente = false;

        while (!exitMenuUtente) {
            System.out.println("\n==== Menu Principale ====");
            System.out.println("1. Aggiungi Utente");
            System.out.println("2. Elimina un utente");
            System.out.println("3. Mostra tutti gli utenti");
            System.out.println("4. Torna al menu principale");

            System.out.print("Scegli un'opzione (1-4): ");
            sceltaMenuUtente = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (sceltaMenuUtente) {
                case 1:
                    System.out.print("Nome utente: ");
                    String nome = Controlli.controlloInputStringhe(scanner);
                    videoteca.aggiungiUtente(new Utente(0, nome));
                    break;
                case 2:
                    System.out.print("Inserisci l'ID dell'utente da eliminare: ");
                    int idElimina = Controlli.controlloInputInteri(scanner);
                    scanner.nextLine();
                    videoteca.eliminaUtente(idElimina);
                    break;
                case 3:
                    videoteca.mostraUtenti();
                    break;
                case 4:
                    System.out.println("Torno al menu principale.");
                    exitMenuUtente = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }

    // Menu per la gestione Film
    public static void menuFilm(Scanner scanner, Videoteca videoteca) {
        int sceltaMenuFilm;
        boolean exitMenuFilm = false;

        while (!exitMenuFilm) {
            System.out.println("\n==== Menu Principale ====");
            System.out.println("1. Aggiungi Film");
            System.out.println("2. Elimina un Film");
            System.out.println("3. Mostra tutti i Film");
            System.out.println("4. Torna al menu principale");

            System.out.print("Scegli un'opzione (1-4): ");
            sceltaMenuFilm = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (sceltaMenuFilm) {
                case 1:
                    System.out.print("Titolo del film: ");
                    String titolo = Controlli.controlloInputStringhe(scanner);
                    System.out.print("Anno di uscita: ");
                    int anno = Controlli.controlloInputInteri(scanner);
                    scanner.nextLine();
                    videoteca.aggiungiFilm(new Film(titolo, anno));
                    break;
                case 2:
                    System.out.print("Inserisci l'ID del film da eliminare: ");
                    int idFilm = Controlli.controlloInputInteri(scanner);
                    scanner.nextLine();
                    videoteca.eliminaFilm(idFilm);
                    break;
                case 3:
                    videoteca.mostraFilm();
                    break;
                case 4:
                    System.out.println("Torno al menu principale.");
                    exitMenuFilm = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }

    // Menu per la gestione Noleggi
    public static void menuNoleggi(Scanner scanner, Videoteca videoteca) {
        int sceltaMenuNoleggi;
        boolean exitMenuNoleggi = false;

        while (!exitMenuNoleggi) {
            System.out.println("\n==== Menu Principale ====");
            System.out.println("1. Noleggia un Film per Utente");
            System.out.println("2. Mostra Noleggi per Utente");
            System.out.println("3. Mostra tutti i Noleggi di tutti gli Utenti");
            System.out.println("4. Torna al menu principale");

            System.out.print("Scegli un'opzione (1-4): ");
            sceltaMenuNoleggi = Controlli.controlloInputInteri(scanner);
            scanner.nextLine();

            switch (sceltaMenuNoleggi) {
                case 1:
                    System.out.print("Nome utente: ");
                    String nomeUtente = Controlli.controlloInputStringhe(scanner);
                    System.out.print("Titolo film: ");
                    String titoloFilm = Controlli.controlloInputStringhe(scanner);

                    // converto in id
                    Integer idUtenteNoleggio = videoteca.getIdUtenteByName(nomeUtente);
                    Integer idFilmNoleggio = videoteca.getIdFilmByTitle(titoloFilm);

                    if (idUtenteNoleggio == null) {
                        System.out.println("Errore: utente non trovato.");
                        break;
                    }
                    if (idFilmNoleggio == null) {
                        System.out.println("Errore: film non trovato.");
                        break;
                    }

                    videoteca.noleggiaFilm(idUtenteNoleggio, idFilmNoleggio);
                    break;
                case 2:
                    System.out.print("ID Utente: ");
                    int idUt = Controlli.controlloInputInteri(scanner);
                    scanner.nextLine();
                    videoteca.mostraNoleggiUtente(idUt);
                    break;
                case 3:
                    videoteca.mostraTuttiINoleggi();
                    break;
                case 4:
                    System.out.println("Torno al menu principale.");
                    exitMenuNoleggi = true;
                    break;
                default:
                    System.out.println("Opzione non valida! Riprova.");
            }
        }
    }
}