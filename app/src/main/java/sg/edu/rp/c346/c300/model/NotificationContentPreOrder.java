package sg.edu.rp.c346.c300.model;

import java.util.ArrayList;

public class NotificationContentPreOrder {

    private String tid;
    private int quantity;
    private ArrayList<AddOn> addOn;
    private String addiitionalNotes;
    private double totalPrice;
    private String dateTimeOrder;


    public NotificationContentPreOrder(String tid, int quantity, ArrayList<AddOn> addOn, String addiitionalNotes, double totalPrice, String dateTimeOrder) {
        this.tid = tid;
        this.quantity = quantity;
        this.addOn = addOn;
        this.addiitionalNotes = addiitionalNotes;
        this.totalPrice = totalPrice;
        this.dateTimeOrder = dateTimeOrder;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
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

    public String getAddiitionalNotes() {
        return addiitionalNotes;
    }

    public void setAddiitionalNotes(String addiitionalNotes) {
        this.addiitionalNotes = addiitionalNotes;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDateTimeOrder() {
        return dateTimeOrder;
    }

    public void setDateTimeOrder(String dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }
}
