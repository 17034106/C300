package sg.edu.rp.c346.c300.model;

import java.util.Date;

public class Customer {


    private String customername;
    private String nric;
    private String dob;
    private int mobile;
    private String customerschool;
    private String email;
    private String password;



    public Customer(String customername, String nric, String dob, int mobile, String customerschool, String email, String password) {
        this.customername = customername;
        this.nric = nric;
        this.dob = dob;
        this.mobile = mobile;
        this.customerschool = customerschool;
        this.email = email;
        this.password = password;
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
}
