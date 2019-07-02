package sg.edu.rp.c346.c300.model;

import java.util.ArrayList;

public class WalkIn {


    private String name;
    private double price;
    private String dateTimeOrder;
    private int quantity;
    private String stallName;
    private int stallId;
    private int foodId;
    private double totalPrice;
    private ArrayList<AddOn> addOn;
    private String tId;
    private String customerUID;
    private String stallUID;
    private String image;
    private String school;


    public WalkIn(String name, double price, String dateTimeOrder, int quantity, String stallName, int stallId, int foodId, double totalPrice, ArrayList<sg.edu.rp.c346.c300.model.AddOn> addOn, String tId, String customerUID, String stallUID, String image, String school) {
        this.name = name;
        this.price = price;
        this.dateTimeOrder = dateTimeOrder;
        this.quantity = quantity;
        this.stallName = stallName;
        this.stallId = stallId;
        this.foodId = foodId;
        this.totalPrice = totalPrice;
        this.addOn = addOn;
        this.tId = tId;
        this.customerUID = customerUID;
        this.stallUID = stallUID;
        this.image = image;
        this.school = school;
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

    public String getDateTimeOrder() {
        return dateTimeOrder;
    }

    public void setDateTimeOrder(String dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public int getStallId() {
        return stallId;
    }

    public void setStallId(int stallId) {
        this.stallId = stallId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<sg.edu.rp.c346.c300.model.AddOn> getAddOn() {
        return addOn;
    }

    public void setAddOn(ArrayList<sg.edu.rp.c346.c300.model.AddOn> addOn) {
        this.addOn = addOn;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getCustomerUID() {
        return customerUID;
    }

    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
