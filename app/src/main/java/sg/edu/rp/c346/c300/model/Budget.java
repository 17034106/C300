package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;

public class Budget {

    @PropertyName("allowance")
    private double allowance;

    @PropertyName("changedAllowance")
    private double changedAllowance;

    @PropertyName("category")
    private Category category;

    @PropertyName("currentDate")
    private String currentDate;


    public Budget(double allowance, double changedAllowance, Category category, String currentDate) {
        this.allowance = allowance;
        this.changedAllowance = changedAllowance;
        this.category = category;
        this.currentDate = currentDate;
    }

    public Budget() {
    }

    @PropertyName("allowance")
    public double getAllowance() {
        return allowance;
    }

    @PropertyName("allowance")
    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }

    @PropertyName("changedAllowance")
    public double getChangedAllowance() {
        return changedAllowance;
    }

    @PropertyName("changedAllowance")
    public void setChangedAllowance(double changedAllowance) {
        this.changedAllowance = changedAllowance;
    }

    @PropertyName("category")
    public Category getCategory() {
        return category;
    }

    @PropertyName("category")
    public void setCategory(Category category) {
        this.category = category;
    }

    @PropertyName("currentDate")
    public String getCurrentDate() {
        return currentDate;
    }

    @PropertyName("currentDate")
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }
}
