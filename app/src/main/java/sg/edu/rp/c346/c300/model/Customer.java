package sg.edu.rp.c346.c300.model;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.Date;

public class Customer implements Serializable {


    private String customername;
    private String nric;
    private String dob;
    private int mobile;
    private String customerschool;
    private String email;
    private String password;
    private double balance;
    private double saving;
    private double charity;
    private double remind;
    @PropertyName("Parent")
    private Parent parent;




    public Customer(String customername, String nric, String dob, int mobile, String customerschool, String email, String password, double balance, double saving, double charity, double remind, Parent parent) {
        this.customername = customername;
        this.nric = nric;
        this.dob = dob;
        this.mobile = mobile;
        this.customerschool = customerschool;
        this.email = email;
        this.password = password;
        this.balance = balance;
        this.saving = saving;
        this.charity = charity;
        this.remind = remind;
        this.parent = parent;
    }


    public Customer(){

    }


    public String getCustomername() {
        return customername;
    }

    public void setCustomername(String customername) {
        this.customername = customername;
    }

    public String getNric() {
        return nric;
    }

    public void setNric(String nric) {
        this.nric = nric;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public String getCustomerschool() {
        return customerschool;
    }

    public void setCustomerschool(String customerschool) {
        this.customerschool = customerschool;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getSaving() {
        return saving;
    }

    public void setSaving(double saving) {
        this.saving = saving;
    }

    public double getCharity() {
        return charity;
    }

    public void setCharity(double charity) {
        this.charity = charity;
    }

    public double getRemind() {
        return remind;
    }

    public void setRemind(double remind) {
        this.remind = remind;
    }

    @PropertyName("Parent")
    public Parent getParent() {
        return parent;
    }

    @PropertyName("Parent")
    public void setParent(Parent parent) {
        this.parent = parent;
    }
}
