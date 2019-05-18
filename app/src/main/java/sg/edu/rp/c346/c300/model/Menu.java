package sg.edu.rp.c346.c300.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Menu {

    private String stallName;
    private int image;


    public Menu(String stallName) {
        this.stallName = stallName;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
