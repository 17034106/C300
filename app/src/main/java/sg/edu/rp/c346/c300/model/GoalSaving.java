package sg.edu.rp.c346.c300.model;

import java.io.Serializable;

@SuppressWarnings("serial") //With this annotation we are going to hide compiler warnings
public class GoalSaving implements Serializable {

    private String name;
    private double price;
    private String status;


    public GoalSaving(String name, double price, String status) {
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public GoalSaving(){

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
