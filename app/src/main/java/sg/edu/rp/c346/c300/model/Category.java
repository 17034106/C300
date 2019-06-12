package sg.edu.rp.c346.c300.model;

import com.google.gson.annotations.SerializedName;

public class Category {

    @SerializedName("food")
    private FoodBudget food;

    @SerializedName("drink")
    private Drink drink;

    @SerializedName("stationery")
    private Stationery stationery;

    @SerializedName("charity")
    private Charity charity;

    @SerializedName("others")
    private Others others;

    public Category(FoodBudget food, Drink drink, Stationery stationery, Charity charity, Others others) {
        this.food = food;
        this.drink = drink;
        this.stationery = stationery;
        this.charity = charity;
        this.others = others;
    }

    public Category() {
    }

    public FoodBudget getFood() {
        return food;
    }

    public void setFood(FoodBudget food) {
        this.food = food;
    }

    public Drink getDrink() {
        return drink;
    }

    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    public Stationery getStationery() {
        return stationery;
    }

    public void setStationery(Stationery stationery) {
        this.stationery = stationery;
    }

    public Charity getCharity() {
        return charity;
    }

    public void setCharity(Charity charity) {
        this.charity = charity;
    }

    public Others getOthers() {
        return others;
    }

    public void setOthers(Others others) {
        this.others = others;
    }
}
