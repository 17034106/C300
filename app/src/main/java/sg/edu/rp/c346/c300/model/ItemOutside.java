package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;

public class ItemOutside implements Serializable {
    private String name;
    private double price;
    private int quantity;
    private int foodID;
    private String barcode;
    private String category;
    private String imageurl;


    public ItemOutside(String name, double price, int quantity, int foodID, String barcode, String category, String imageurl) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.foodID = foodID;
        this.barcode = barcode;
        this.category = category;
        this.imageurl = imageurl;
    }

    public  ItemOutside(){

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

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
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


    public String getImageurl() {
        return imageurl;
    }


    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
