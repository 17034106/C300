package sg.edu.rp.c346.c300.model;

import java.util.ArrayList;

public class Collection {


    private String name;
    private double price;
    private String dateTimeOrder;
    private int quantity;
    private String stallName;
    private int stallId;
    private double totalPrice;
    private ArrayList<AddOn> addOnList;
    private String additionalNote;
    private String lastChanges;
    private String tId;
    private String startTime;
    private String endTime;


    public Collection(String name, double price, String dateTimeOrder, int quantity, String stallName, int stallId, double totalPrice, ArrayList<AddOn> addOnList, String additionalNote, String lastChanges, String tId, String startTime, String endTime) {
        this.name = name;
        this.price = price;
        this.dateTimeOrder = dateTimeOrder;
        this.quantity = quantity;
        this.stallName = stallName;
        this.stallId = stallId;
        this.totalPrice = totalPrice;
        this.addOnList = addOnList;
        this.additionalNote = additionalNote;
        this.lastChanges = lastChanges;
        this.tId = tId;
        this.startTime = startTime;
        this.endTime = endTime;
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

    public int getStallId() {
        return stallId;
    }

    public void setStallId(int stallId) {
        this.stallId = stallId;
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


    public String getLastChanges() {
        return lastChanges;
    }

    public void setLastChanges(String lastChanges) {
        this.lastChanges = lastChanges;
    }

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
