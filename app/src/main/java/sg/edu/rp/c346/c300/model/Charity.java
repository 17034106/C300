package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;

public class Charity {

    @PropertyName("change_value_max")
    private double changedValueMax;

    @PropertyName("changedValueMin")
    private double changedValueMin;

    @PropertyName("default_value_max")
    private double defaultValueMax;

    @PropertyName("defaultValueMin")
    private double defaultValueMin;

    @SerializedName("amount")
    private double amount;


    public Charity(double changedValueMax, double changedValueMin, double defaultValueMax, double defaultValueMin, double amount) {
        this.changedValueMax = changedValueMax;
        this.changedValueMin = changedValueMin;
        this.defaultValueMax = defaultValueMax;
        this.defaultValueMin = defaultValueMin;
        this.amount = amount;
    }

    public Charity() {
    }

    public double getChangedValueMax() {
        return changedValueMax;
    }

    public void setChangedValueMax(double changedValueMax) {
        this.changedValueMax = changedValueMax;
    }

    public double getChangedValueMin() {
        return changedValueMin;
    }

    public void setChangedValueMin(double changedValueMin) {
        this.changedValueMin = changedValueMin;
    }

    public double getDefaultValueMax() {
        return defaultValueMax;
    }

    public void setDefaultValueMax(double defaultValueMax) {
        this.defaultValueMax = defaultValueMax;
    }

    public double getDefaultValueMin() {
        return defaultValueMin;
    }

    public void setDefaultValueMin(double defaultValueMin) {
        this.defaultValueMin = defaultValueMin;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
