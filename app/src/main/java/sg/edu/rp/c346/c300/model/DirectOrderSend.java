package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

import java.util.ArrayList;

public class DirectOrderSend {

    @PropertyName("name")
    private String name;
    @PropertyName("price")
    private double price;
    @PropertyName("stallName")
    private String stallName;
    @PropertyName("stallId")
    private int stallID;
    @PropertyName("school")
    private String school;
    @PropertyName("foodId")
    private int foodID;
    @PropertyName("quantity")
    private int quantity;
    @PropertyName("addOn")
    private ArrayList<AddOn> addOn;
    @PropertyName("totalPrice")
    private double totalPrice;
    @PropertyName("imageurl")
    private String imageurl;
    @PropertyName("stallUID")
    private String stallUID;
    @PropertyName("customerUID")
    private String customerUID;
    @PropertyName("dateTimeOrder")
    private String dateTimeOrder;
    @PropertyName("tId")
    private String tId;
    @PropertyName("ISITFOOD")
    private boolean isItFood;


    public DirectOrderSend(String name, double price, String stallName, int stallID, String school, int foodID, int quantity, ArrayList<AddOn> addOn, double totalPrice, String imageurl, String stallUID, String customerUID, String dateTimeOrder, String tId, boolean isItFood) {
        this.name = name;
        this.price = price;
        this.stallName = stallName;
        this.stallID = stallID;
        this.school = school;
        this.foodID = foodID;
        this.quantity = quantity;
        this.addOn = addOn;
        this.totalPrice = totalPrice;
        this.imageurl = imageurl;
        this.stallUID = stallUID;
        this.customerUID = customerUID;
        this.dateTimeOrder = dateTimeOrder;
        this.tId = tId;
        this.isItFood = isItFood;
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

    @PropertyName("stallName")
    public String getStallName() {
        return stallName;
    }

    @PropertyName("stallName")
    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    @PropertyName("stallId")
    public int getStallID() {
        return stallID;
    }

    @PropertyName("stallId")
    public void setStallID(int stallID) {
        this.stallID = stallID;
    }

    @PropertyName("school")
    public String getSchool() {
        return school;
    }

    @PropertyName("school")
    public void setSchool(String school) {
        this.school = school;
    }

    @PropertyName("foodId")
    public int getFoodID() {
        return foodID;
    }

    @PropertyName("foodId")
    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    @PropertyName("quantity")
    public int getQuantity() {
        return quantity;
    }

    @PropertyName("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @PropertyName("addOn")
    public ArrayList<AddOn> getAddOn() {
        return addOn;
    }

    @PropertyName("addOn")
    public void setAddOn(ArrayList<AddOn> addOn) {
        this.addOn = addOn;
    }

    @PropertyName("totalPrice")
    public double getTotalPrice() {
        return totalPrice;
    }

    @PropertyName("totalPrice")
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @PropertyName("imageurl")
    public String getImageurl() {
        return imageurl;
    }

    @PropertyName("imageurl")
    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    @PropertyName("stallUID")
    public String getStallUID() {
        return stallUID;
    }

    @PropertyName("stallUID")
    public void setStallUID(String stallUID) {
        this.stallUID = stallUID;
    }

    @PropertyName("customerUID")
    public String getCustomerUID() {
        return customerUID;
    }

    @PropertyName("customerUID")
    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    @PropertyName("dateTimeOrder")
    public String getDateTimeOrder() {
        return dateTimeOrder;
    }

    @PropertyName("dateTimeOrder")
    public void setDateTimeOrder(String dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }

    @PropertyName("tId")
    public String gettId() {
        return tId;
    }

    @PropertyName("tId")
    public void settId(String tId) {
        this.tId = tId;
    }

    @PropertyName("ISITFOOD")
    public boolean isItFood() {
        return isItFood;
    }

    @PropertyName("ISITFOOD")
    public void setItFood(boolean itFood) {
        isItFood = itFood;
    }
}
