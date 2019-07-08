package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;
import com.google.gson.annotations.SerializedName;

public class Category {



    @PropertyName("food")
    private FoodBudget food;

    @PropertyName("drink")
    private Drink drink;

    @PropertyName("stationery")
    private Stationery stationery;

    @PropertyName("charity")
    private Charity charity;

    @PropertyName("others")
    private Others others;

    public Category( FoodBudget food, Drink drink, Stationery stationery, Charity charity, Others others) {
        this.food = food;
        this.drink = drink;
        this.stationery = stationery;
        this.charity = charity;
        this.others = others;
    }

    public Category() {
    }

    @PropertyName("food")
    public FoodBudget getFood() {
        return food;
    }

    @PropertyName("food")
    public void setFood(FoodBudget food) {
        this.food = food;
    }

    @PropertyName("drink")
    public Drink getDrink() {
        return drink;
    }

    @PropertyName("drink")
    public void setDrink(Drink drink) {
        this.drink = drink;
    }

    @PropertyName("stationery")
    public Stationery getStationery() {
        return stationery;
    }

    @PropertyName("stationery")
    public void setStationery(Stationery stationery) {
        this.stationery = stationery;
    }

    @PropertyName("charity")
    public Charity getCharity() {
        return charity;
    }

    @PropertyName("charity")
    public void setCharity(Charity charity) {
        this.charity = charity;
    }

    @PropertyName("others")
    public Others getOthers() {
        return others;
    }

    @PropertyName("others")
    public void setOthers(Others others) {
        this.others = others;
    }
}
