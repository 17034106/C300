package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;

public class Collection implements Serializable {


    @PropertyName("name")
    private String name;
    @PropertyName("price")
    private double price;
    @PropertyName("dateTimeOrder")
    private String dateTimeOrder;
    @PropertyName("quantity")
    private int quantity;
    @PropertyName("stallName")
    private String stallName;
    @PropertyName("stallId")
    private int stallId;
    @PropertyName("foodId")
    private int foodId;
    @PropertyName("totalPrice")
    private double totalPrice;
    @PropertyName("addOn")
    private ArrayList<AddOn> addOn;
    @PropertyName("additionalNote")
    private String additionalNote;
    @PropertyName("lastChanges")
    private String lastChanges;
    @PropertyName("lastChangesInMin")
    private int lastChangesInMin;
    @PropertyName("tId")
    private String tId;
    @PropertyName("startTime")
    private String startTime;
    @PropertyName("endTime")
    private String endTime;
    @PropertyName("customerUID")
    private String customerUID;
    @PropertyName("stallUID")
    private String stallUID;
    @PropertyName("status")
    private String status;
    @PropertyName("imageurl")
    private String image;
    @PropertyName("school")
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
        this.addOn = AddOn;
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

    @PropertyName("name")
    public String getName() {
        return name;
    }

    @PropertyName("name")
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName("price")
    public double getPrice() {
        return price;
    }

    @PropertyName("price")
    public void setPrice(double price) {
        this.price = price;
    }

    @PropertyName("dateTimeOrder")
    public String getDateTimeOrder() {
        return dateTimeOrder;
    }

    @PropertyName("dateTimeOrder")
    public void setDateTimeOrder(String dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }

    @PropertyName("quantity")
    public int getQuantity() {
        return quantity;
    }

    @PropertyName("quantity")
    public void setQuantity(int foodQuantity) {
        this.quantity = foodQuantity;
    }

    @PropertyName("stallName")
    public String getStallName() {
        return stallName;
    }

    @PropertyName("stallName")
    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    @PropertyName("stallId")
    public int getStallId() {
        return stallId;
    }

    @PropertyName("stallId")
    public void setStallId(int stallId) {
        this.stallId = stallId;
    }

    @PropertyName("foodId")
    public int getFoodId() {
        return foodId;
    }

    @PropertyName("foodId")
    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    @PropertyName("totalPrice")
    public double getTotalPrice() {
        return totalPrice;
    }

    @PropertyName("totalPrice")
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @PropertyName("addOn")
    public ArrayList<AddOn> getAddOn() {
        return addOn;
    }

    @PropertyName("addOn")
    public void setAddOn(ArrayList<AddOn> addOn) {
        this.addOn = addOn;
    }

    @PropertyName("additionalNote")
    public String getAdditionalNote() {
        return additionalNote;
    }

    @PropertyName("additionalNote")
    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
    }

    @PropertyName("lastChanges")
    public String getLastChanges() {
        return lastChanges;
    }

    @PropertyName("lastChanges")
    public void setLastChanges(String lastChanges) {
        this.lastChanges = lastChanges;
    }

    @PropertyName("lastChangesInMin")
    public int getLastChangesInMin() {
        return lastChangesInMin;
    }

    @PropertyName("lastChangesInMin")
    public void setLastChangesInMin(int lastChangesInMin) {
        this.lastChangesInMin = lastChangesInMin;
    }

    @PropertyName("tId")
    public String gettId() {
        return tId;
    }

    @PropertyName("tId")
    public void settId(String tId) {
        this.tId = tId;
    }

    @PropertyName("startTime")
    public String getStartTime() {
        return startTime;
    }

    @PropertyName("startTime")
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    @PropertyName("endTime")
    public String getEndTime() {
        return endTime;
    }

    @PropertyName("endTime")
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @PropertyName("customerUID")
    public String getCustomerUID() {
        return customerUID;
    }

    @PropertyName("customerUID")
    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    @PropertyName("stallUID")
    public String getStallUID() {
        return stallUID;
    }

    @PropertyName("stallUID")
    public void setStallUID(String stallUID) {
        this.stallUID = stallUID;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @PropertyName("imageurl")
    public String getImage() {
        return image;
    }

    @PropertyName("imageurl")
    public void setImage(String image) {
        this.image = image;
    }

    @PropertyName("school")
    public String getSchool() {
        return school;
    }

    @PropertyName("school")
    public void setSchool(String school) {
        this.school = school;
    }
}

