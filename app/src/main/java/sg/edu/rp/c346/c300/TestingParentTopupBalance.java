package sg.edu.rp.c346.c300;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.regex.Pattern;

import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.CreditCard;

public class TestingParentTopupBalance extends AppCompatActivity {


//    EditText etCardNumber, etCardName, etCardValid, etCardCSV, etAmount;

    CardForm cardForm;

    EditText etAmount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_topup_balance);


//        etCardNumber = findViewById(R.id.etCardNumber);
//        etCardName = findViewById(R.id.etCardName);
//        etCardValid = findViewById(R.id.etCardValid);
//        etCardCSV = findViewById(R.id.etCardCSV);
//        etAmount = findViewById(R.id.etAmount);


        etAmount = findViewById(R.id.etAmount);
        cardForm = (CardForm) findViewById(R.id.cardFormInfo);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(false)
                .mobileNumberRequired(false)
                .actionLabel("Top Up")
                .setup(TestingParentTopupBalance.this);


        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);


        findViewById(R.id.topUpBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cardForm.isValid()) {
                    if (!etAmount.getText().toString().isEmpty() ) {

                        String patternTopUp = "[0-9]+.?[0-9]{0,2}";
                        if (Pattern.matches(patternTopUp, etAmount.getText().toString())) { // checking whether it is valid amount of not

                            if (Double.parseDouble(etAmount.getText().toString().trim()) > 0) {

                                final double amount = Double.parseDouble(etAmount.getText().toString().trim());


                                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(TestingParentTopupBalance.this); // for the if condition to display the dialog
                                android.support.v7.app.AlertDialog alert1 = builder.create(); // for the if condition to display the dialog


                                builder.setTitle("Categorization");
                                builder.setPositiveButton("Top Up", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        topUpBalance(amount);
                                    }
                                });
                                builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                builder.setMessage(String.format("Are you sure to top up $%.2f", amount));
                                builder.setCancelable(false);
                                alert1 = builder.create();
                                alert1.show();

                            } else {
                                etAmount.setError("Please be above $0");
                            }

                        }else{
                            etAmount.setError("Invalid Input");
                        }

                    } else {
                        etAmount.setError("Please fill in the amount");
                    }

                }
                else{
                    Toast.makeText(TestingParentTopupBalance.this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();
                }


            }
        });


        findViewById(R.id.historyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TestingParentTopupBalance.this, TopUpHistory.class));
            }
        });



    }

    private void topUpBalance(final double amount){
        final DatabaseReference drBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
        drBalance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double previousAmount = Double.parseDouble(dataSnapshot.getValue().toString());
                drBalance.setValue(Double.parseDouble(String.format("%.2f", (previousAmount+amount))));

                final DatabaseReference drCreditCardCustomer = FirebaseDatabase.getInstance().getReference().child("creditCardCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                drCreditCardCustomer.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numOfTopUp = Integer.parseInt(dataSnapshot.child("numOfTopUp").getValue().toString());

                        CreditCard creditCard = new CreditCard(cardForm.getCardNumber(), amount, MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));
                        drCreditCardCustomer.child(numOfTopUp+"").setValue(creditCard);
                        drCreditCardCustomer.child("numOfTopUp").setValue(numOfTopUp+1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                Toast.makeText(TestingParentTopupBalance.this, "Successfully Top Up", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
