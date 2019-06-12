package sg.edu.rp.c346.c300.model;

import com.google.gson.annotations.SerializedName;

public class Others {



    @SerializedName("changedValueMax")
    private double changedValueMax;

    @SerializedName("changedValueMin")
    private double changedValueMin;

    @SerializedName("defaultValueMax")
    private double defaultValueMax;

    @SerializedName("defaultValueMin")
    private double defaultValueMin;

    @SerializedName("amount")
    private double amount;

    public Others(double changedValueMax, double changedValueMin, double defaultValueMax, double defaultValueMin, double amount) {
        this.changedValueMax = changedValueMax;
        this.changedValueMin = changedValueMin;
        this.defaultValueMax = defaultValueMax;
        this.defaultValueMin = defaultValueMin;
        this.amount = amount;
    }


    public Others() {
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
