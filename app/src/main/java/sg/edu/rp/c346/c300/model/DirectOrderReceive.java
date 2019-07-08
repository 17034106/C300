package sg.edu.rp.c346.c300.model;

import java.util.ArrayList;

public class DirectOrderReceive {


    private String name;
    private double price;
    private String stallName;
    private int stallID;
    private String school;
    private int foodID;
    private int quantity;
    private ArrayList<AddOn> addOn;
    private double totalPrice;
    private String image;
    private String stallUID;


    public DirectOrderReceive(String name, double price, String stallName, int stallID, String school, int foodID, int quantity, ArrayList<AddOn> addOn, double totalPrice, String image, String stallUID) {
        this.name = name;
        this.price = price;
        this.stallName = stallName;
        this.stallID = stallID;
        this.school = school;
        this.foodID = foodID;
        this.quantity = quantity;
        this.addOn = addOn;
        this.totalPrice = totalPrice;
        this.image = image;
        this.stallUID = stallUID;
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

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public int getStallID() {
        return stallID;
    }

    public void setStallID(int stallID) {
        this.stallID = stallID;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<AddOn> getAddOn() {
        return addOn;
    }

    public void setAddOn(ArrayList<AddOn> addOn) {
        this.addOn = addOn;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStallUID() {
        return stallUID;
    }

    public void setStallUID(String stallUID) {
        this.stallUID = stallUID;
    }

}
