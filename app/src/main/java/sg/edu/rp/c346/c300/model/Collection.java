package sg.edu.rp.c346.c300.model;

import java.util.ArrayList;

public class Collection {


    private String name;
    private double price;
    private String dateTimeOrder;
    private int quantity;
    private String stallName;
    private int stallId;
    private int foodId;
    private double totalPrice;
    private ArrayList<AddOn> AddOn;
    private String additionalNote;
    private String lastChanges;
    private int lastChangesInMin;
    private String tId;
    private String startTime;
    private String endTime;
    private String customerUID;
    private String stallUID;
    private String status;
    private String image;
    private String school;

    public Collection(String name, double price, String dateTimeOrder, int quantity, String stallName, int stallId, int foodId, double totalPrice, ArrayList<AddOn> AddOn, String additionalNote, String lastChanges, int lastChangesInMin, String tId, String startTime, String endTime, String customerUID, String stallUID, String status, String image, String school) {
        this.name = name;
        this.price = price;
        this.dateTimeOrder = dateTimeOrder;
        this.quantity = quantity;
        this.stallName = stallName;
        this.stallId = stallId;
        this.foodId = foodId;
        this.totalPrice = totalPrice;
        this.AddOn = AddOn;
        this.additionalNote = additionalNote;
        this.lastChanges = lastChanges;
        this.lastChangesInMin =lastChangesInMin;
        this.tId = tId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerUID = customerUID;
        this.stallUID = stallUID;
        this.status = status;
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

    public ArrayList<AddOn> getAddOn() {
        return AddOn;
    }

    public void setAddOn(ArrayList<AddOn> addOn) {
        this.AddOn = addOn;
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

    public int getLastChangesInMin() {
        return lastChangesInMin;
    }

    public void setLastChangesInMin(int lastChangesInMin) {
        this.lastChangesInMin = lastChangesInMin;
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

    public String getCustomerUID() {
        return customerUID;
    }

    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    public String getStallUID() {
        return stallUID;
    }

    public void setStallUID(String stallUID) {
        this.stallUID = stallUID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
