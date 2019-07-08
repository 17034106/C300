package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Cart {


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
    private ArrayList<AddOn> addOnList;
    @PropertyName("additionalNote")
    private String additionalNote;
    @PropertyName("startTime")
    private String startTime;
    @PropertyName("endTime")
    private String endTime;
    @PropertyName("lastChanges")
    private String lastChanges;
    @PropertyName("lastChangesInMin")
    private int lastChangesInMin;
    @PropertyName("imageurl")
    private String image;
    @PropertyName("stallUID")
    private String stallUID;
    @PropertyName("school")
    private String school;


    public Cart(String name, double price, String dateTimeOrder, int quantity, String stallName, int stallId, int foodId, double totalPrice, ArrayList<AddOn> addOnList, String additionalNote, String startTime, String endTime, String lastChanges, int lastChangesInMin, String image, String stallUID, String school) {
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
        this.stallUID = stallUID;
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
    public ArrayList<AddOn> getAddOnList() {
        return addOnList;
    }

    @PropertyName("addOn")
    public void setAddOnList(ArrayList<AddOn> addOnList) {
        this.addOnList = addOnList;
    }

    @PropertyName("additionalNote")
    public String getAdditionalNote() {
        return additionalNote;
    }

    @PropertyName("additionalNote")
    public void setAdditionalNote(String additionalNote) {
        this.additionalNote = additionalNote;
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

    @PropertyName("imageurl")
    public String getImage() {
        return image;
    }

    @PropertyName("imageurl")
    public void setImage(String image) {
        this.image = image;
    }

    @PropertyName("stallUID")
    public String getStallUID() {
        return stallUID;
    }

    @PropertyName("stallUID")
    public void setStallUID(String stallUID) {
        this.stallUID = stallUID;
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
