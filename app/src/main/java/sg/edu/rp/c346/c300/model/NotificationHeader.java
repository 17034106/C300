package sg.edu.rp.c346.c300.model;

public class NotificationHeader {

    private String title;
    private String status;
    private String currentTime;
    private String image;

    public NotificationHeader(String title, String status, String currentTime, String image) {
        this.title = title;
        this.status = status;
        this.currentTime = currentTime;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
