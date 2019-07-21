package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.ArrayList;

public class OutsideSendOrder implements Serializable {

    ArrayList<ItemOutside> item;
    private String stallName;
    @PropertyName("stallId")
    private int stallID;
    private String stallUID;
    private String customerUID;
    private String school;
    private String tId;
    private String dateTimeOrder;
    private double totalPrice;
    @PropertyName("ISITFOOD")
    private boolean ISITFOOD;


    public OutsideSendOrder(ArrayList<ItemOutside> item, String stallName, int stallID, String stallUID, String customerUID, String school, String tId, String dateTimeOrder, double totalPrice, boolean ISITFOOD) {
        this.item = (ArrayList<ItemOutside>) item;
        this.stallName = stallName;
        this.stallID = stallID;
        this.stallUID = stallUID;
        this.customerUID = customerUID;
        this.school = school;
        this.tId = tId;
        this.dateTimeOrder = dateTimeOrder;
        this.totalPrice = totalPrice;
        this.ISITFOOD = ISITFOOD;
    }

    public OutsideSendOrder(){

    }

    public ArrayList<ItemOutside> getItem() {
        return item;
    }

    public void setItem(ArrayList<ItemOutside> item) {
        this.item = item;
    }

    public String getStallName() {
        return stallName;
    }

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

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getDateTimeOrder() {
        return dateTimeOrder;
    }

    public void setDateTimeOrder(String dateTimeOrder) {
        this.dateTimeOrder = dateTimeOrder;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @PropertyName("ISITFOOD")
    public boolean isISITFOOD() {
        return ISITFOOD;
    }

    @PropertyName("ISITFOOD")
    public void setISITFOOD(boolean ISITFOOD) {
        this.ISITFOOD = ISITFOOD;
    }
}
