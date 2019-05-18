package sg.edu.rp.c346.c300.model;

import com.google.gson.annotations.SerializedName;

public class Food1 {

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;


    public Food1(String name, double price) {
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
