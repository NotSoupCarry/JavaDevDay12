public class ProvaGetSet {
    public static void main(String[] args) {
        Person persona1 = new Person();

        persona1.setName("gigi"); // setto il nome

        System.out.println(persona1.getName()); // prendo il nome
    }
}

class Person{
    private String name; // private

    public String getName(){ // GETTER
        return name;
    }

    public void setName(String newName){ // SETTER
        this.name = newName;
    }
}
