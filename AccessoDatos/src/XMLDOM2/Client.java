package XMLDOM2;

import java.io.Serializable;
public class Client implements Serializable {
    private String name;
    private int age;
    public Client(String nombre, int edad) {
        this.name =nombre;
        this.age =edad;
    }
    public void setName(String nom) {
        this.name =nom;
    }
    public void setAge(int ed) {
        this.age =ed;
    }

    public String getName() {
        return this.name;
    }
    public int getAge() {
        return this.age;
    }

}
