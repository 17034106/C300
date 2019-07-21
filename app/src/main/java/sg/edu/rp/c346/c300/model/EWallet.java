package sg.edu.rp.c346.c300.model;

import java.io.Serializable;

public class EWallet implements Serializable {
    private String category;
    private double amount;
    private String comment;
    private String status;
    private String requestTime;
    private String answerTime;

    public EWallet(String category, double amount, String comment, String status, String requestTime, String answerTime) {
        this.category = category;
        this.amount = amount;
        this.comment = comment;
        this.status = status;
        this.requestTime = requestTime;
        this.answerTime = answerTime;
    }

    public EWallet(){

    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }
}
