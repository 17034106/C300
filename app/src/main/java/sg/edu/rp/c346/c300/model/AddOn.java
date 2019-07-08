package sg.edu.rp.c346.c300.model;

import java.io.Serializable;

public class AddOn implements Serializable {


    private String name;
    private double price;

    public AddOn( String name, double price) {
        this.name = name;
        this.price = price;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
