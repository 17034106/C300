package sg.edu.rp.c346.c300.model;

public class PrePayment {

    private String stallUID;
    private String customerUID;
    private double amount;
    private String paymentDateTime;
//    private String tcOrderID;
    private String school;
//    private String toOrderID;
    private String tID;


    public PrePayment(String stallUID, String customerUID, double amount, String paymentDateTime,  String school,  String tID) {
        this.stallUID = stallUID;
        this.customerUID = customerUID;
        this.amount = amount;
        this.paymentDateTime = paymentDateTime;
//        this.tcOrderID = tcOrderID;
        this.school = school;
//        this.toOrderID = toOrderID;
        this.tID = tID;
    }

    public PrePayment(){

    }


    public String getStallUID() {
        return stallUID;
    }

    public void setStallUID(String stallUID) {
        this.stallUID = stallUID;
    }

    public String getCustomerUID() {
        return customerUID;
    }

    public void setCustomerUID(String customerUID) {
        this.customerUID = customerUID;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentDateTime() {
        return paymentDateTime;
    }

    public void setPaymentDateTime(String paymentDateTime) {
        this.paymentDateTime = paymentDateTime;
    }

//    public String getTcOrderID() {
//        return tcOrderID;
//    }
//
//    public void setTcOrderID(String tcOrderID) {
//        this.tcOrderID = tcOrderID;
//    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

//    public String getToOrderID() {
//        return toOrderID;
//    }
//
//    public void setToOrderID(String toOrderID) {
//        this.toOrderID = toOrderID;
//    }

    public String gettID() {
        return tID;
    }

    public void settID(String tID) {
        this.tID = tID;
    }
}
