package sg.edu.rp.c346.c300.model;

public class Food {

    private String name;
    private double price;
    private int lastChanges;
    private String stallName;
    private int stallId;
    private int foodId;
    private String school;
    private String startTime;
    private String endTime;
    private String image;



//    public Food(int imageId, String name, double price) {
//        this.imageId = imageId;
//        this.name = name;
//        this.price = price;
//    }


    public Food(String name, double price, int lastChanges, String stallName,String school, int stallId, int foodId, String startTime, String endTime, String image) {
        this.name = name;
        this.price = price;
        this.lastChanges = lastChanges;
        this.stallName = stallName;
        this.school = school;
        this.stallId = stallId;
        this.foodId = foodId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
    }

//    public int getImageId() {
//        return imageId;
//    }
//
//    public void setImageId(int imageId) {
//        this.imageId = imageId;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStallName() {
        return stallName;
    }

    public void setStallName(String stallName) {
        this.stallName = stallName;
    }

    public int getLastChanges() {
        return lastChanges;
    }

    public void setLastChanges(int lastChanges) {
        this.lastChanges = lastChanges;
    }


    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getStallId() {
        return stallId;
    }

    public void setStallId(int stallId) {
        this.stallId = stallId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
