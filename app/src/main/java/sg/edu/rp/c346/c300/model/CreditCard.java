package sg.edu.rp.c346.c300.model;

public class CreditCard {

    private String creditCardNumber;
    private double amount;
    private String dateTime;


    public CreditCard(String creditCardNumber, double amount, String dateTime) {
        this.creditCardNumber = creditCardNumber;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public CreditCard(){

    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
