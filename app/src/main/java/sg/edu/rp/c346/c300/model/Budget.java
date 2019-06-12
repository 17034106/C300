package sg.edu.rp.c346.c300.model;

import com.google.gson.annotations.SerializedName;

public class Budget {

    @SerializedName("allowance")
    private double allowance;

    @SerializedName("category")
    private Category category;


    public Budget(double allowance, Category category) {
        this.allowance = allowance;
        this.category = category;
    }

    public Budget() {
    }

    public double getAllowance() {
        return allowance;
    }

    public void setAllowance(double allowance) {
        this.allowance = allowance;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
