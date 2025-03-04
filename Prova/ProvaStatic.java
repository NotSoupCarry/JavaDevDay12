public class ProvaStatic{
    static void myStaticMethod(){ //metodo statico
        System.out.println("metodo statico, deve essere chiamato senza creare l oggetto");
    }
    public void myPublicMethod(){ //metodo pubblico
        System.out.println("metodo pubblico, deve essere chiamato creando l oggetto");

    }

    public static void main(String[] args) {
        myStaticMethod(); //Chiamo lo static method

        //myPublicMethod(); genererebbe un errore
        ProvaStatic myObj = new ProvaStatic(); //creao l oggetto ProvaStatic
        myObj.myPublicMethod(); // chiamo il public method 
    }
}