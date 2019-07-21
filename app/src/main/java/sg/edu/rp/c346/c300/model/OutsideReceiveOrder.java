package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

public class OutsideReceiveOrder {

    private String name;
    private double price;
    private int quantity;
    private String barcode;
    private String category;
    @PropertyName("imageurl")
    private String image;
    private String stallName;
    private int stallID;
    private int foodID;
    private String stallUID;
    private String customerUID;
    private String school;


    public OutsideReceiveOrder(String name, double price, int quantity, String barcode, String category, String image, String stallName, int stallID, int foodID, String stallUID, String customerUID, String school) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.barcode = barcode;
        this.category = category;
        this.image = image;
        this.stallName = stallName;
        this.stallID = stallID;
        this.foodID = foodID;
        this.stallUID = stallUID;
        this.customerUID = customerUID;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    @PropertyName("imageurl")
    public String getImage() {
        return image;
    }


    @PropertyName("imageurl")
    public void setImage(String image) {
        this.image = image;
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

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public String getStallUID() {
        return stallUID;
    }

    public void setStallUID(String stallUID) {
        this.stallUID = stallUID;
    }

    public String getCustomerUID() {
        return customerUID;
    }

    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
