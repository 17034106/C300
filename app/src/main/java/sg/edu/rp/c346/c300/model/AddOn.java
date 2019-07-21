package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class AddOn implements Serializable {


    @PropertyName("name")
    private String name;
    @PropertyName("price")
    private double price;

    public AddOn( String name, double price) {
        this.name = name;
        this.price = price;
    }

    public AddOn(){

    }

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("price")
    public double getPrice() {
        return price;
    }

    @PropertyName("price")
    public void setPrice(double price) {
        this.price = price;
    }
}
