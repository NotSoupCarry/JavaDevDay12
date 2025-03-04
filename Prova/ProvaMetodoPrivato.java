public class ProvaMetodoPrivato {
    public static void main(String[] args) {
        Persona p1 = new Persona("GIGI", 41);

        //p1.verificaMaggiorenne(); GENERA ERRORE
        p1.stampaStatus();
    }
}

class Persona {
    private String nome;
    private int eta;

    // Costruttore pubblico
    public Persona(String nome, int eta) {
        this.nome = nome;
        this.eta = eta;
    }

    // Metodo privato usato SOLO dalla classe Persona
    private boolean verificaMaggiorenne() {
        return this.eta >= 18;
    }

    // Metodo pubblico che uso il metodo privato
    public void stampaStatus() {
        if (verificaMaggiorenne()) {
            System.out.println("Maggiorenne");
        } else {
            System.out.println("Minorenne");
        }
    }

}
