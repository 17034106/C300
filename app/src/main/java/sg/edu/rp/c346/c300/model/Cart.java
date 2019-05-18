package sg.edu.rp.c346.c300.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cart {


    private String name;
    private double price;
    private String dateTimeOrder;
    private int quantity;
    private String stallName;
    private double totalPrice;
    private ArrayList<AddOn> addOnList;
    private String additionalNote;


    public Cart(String name, double price, String dateTimeOrder, int quantity, String stallName, double totalPrice, ArrayList<AddOn> addOnList, String additionalNote) {
        this.name = name;
        this.price = price;
        this.dateTimeOrder = dateTimeOrder;
        this.quantity = quantity;
        this.stallName = stallName;
        this.totalPrice = totalPrice;
        this.addOnList = addOnList;
        this.additionalNote = additionalNote;
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

    public void setQuantity(int foodQuantity) {
        this.quantity = foodQuantity;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public ArrayList<AddOn> getAddOnList() {
        return addOnList;
    }

    public void setAddOnList(ArrayList<AddOn> addOnList) {
        this.addOnList = addOnList;
    }

    public String getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }
}
