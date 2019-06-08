package sg.edu.rp.c346.c300.model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cart {


    private String name;
    private double price;
    private String dateTimeOrder;
    private int quantity;
    private String stallName;
    private int stallId;
    private int foodId;
    private double totalPrice;
    private ArrayList<AddOn> addOnList;
    private String additionalNote;
    private String startTime;
    private String endTime;
    private String lastChanges;
    private int lastChangesInMin;
    private String image;


    public Cart(String name, double price, String dateTimeOrder, int quantity, String stallName, int stallId, int foodId, double totalPrice, ArrayList<AddOn> addOnList, String additionalNote, String startTime, String endTime, String lastChanges, int lastChangesInMin, String image) {
        this.name = name;
        this.price = price;
        this.dateTimeOrder = dateTimeOrder;
        this.quantity = quantity;
        this.stallName = stallName;
        this.stallId = stallId;
        this.foodId = foodId;
        this.totalPrice = totalPrice;
        this.addOnList = addOnList;
        this.additionalNote = additionalNote;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastChanges = lastChanges;
        this.lastChangesInMin = lastChangesInMin;
        this.image = image;
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

    public String getLastChanges() {
        return lastChanges;
    }

    public void setLastChanges(String lastChanges) {
        this.lastChanges = lastChanges;
    }

    public int getLastChangesInMin() {
        return lastChangesInMin;
    }

    public void setLastChangesInMin(int lastChangesInMin) {
        this.lastChangesInMin = lastChangesInMin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
