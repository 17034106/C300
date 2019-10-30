package sg.edu.rp.c346.c300.model;

import java.io.Serializable;

public class Parent implements Serializable {


    private String parentname;
    private int mobile;
    private String password;

    public Parent(String parentname, int mobile, String password) {
        this.parentname = parentname;
        this.mobile = mobile;
        this.password = password;
    }

    public Parent(){

    }


    public String getParentname() {
        return parentname;
    }

    public void setParentname(String parentname) {
        this.parentname = parentname;
    }

    public int getMobile() {
        return mobile;
    }

    public void setMobile(int mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
