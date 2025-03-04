public class ProvaErrors {
    public static void main(String[] args) {
        int[] myNums = { 1, 2, 3, 4 };
        // System.out.println(myNums[12]); //Generà un errore

        try {
            System.out.println(myNums[12]); 
        } catch (Exception e) {
            System.out.println("Errore, qualcosa è andato storto");
        }
        finally{
            System.out.println("il \"try-catch\" è finito");

        }
    }
}
