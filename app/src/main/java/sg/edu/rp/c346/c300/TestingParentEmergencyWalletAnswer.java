package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Random;

import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.Budget;
import sg.edu.rp.c346.c300.model.EWallet;

public class TestingParentEmergencyWalletAnswer extends AppCompatActivity {

    EWallet eWalletRequest; // get the request information from TestingParentEmergencyWallet
    int position; // get the position of the request in the fireabase from TestingParentEmergencyWallet
    int numOfRequestPending; // get the size of all requests in the fireabase from TestingParentEmergencyWallet

    TextView category, amount, comment;


    //Showing the loading
    ProgressDialog dialog;

    Budget budget; // getting the category
    int day1;
    DatabaseReference drBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_emergency_wallet_answer);

        dialog = new ProgressDialog(this);

        getCategory();


        //region  pop up window for select date and time
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.85), (int)(height*.70));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 70;
//        params.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(params);
        //endregion

        category = findViewById(R.id.etCategory);
        amount = findViewById(R.id.etAmount);
        comment = findViewById(R.id.etComment);


        eWalletRequest = (EWallet) getIntent().getSerializableExtra("request");
        position = getIntent().getIntExtra("position",-1);
        numOfRequestPending = getIntent().getIntExtra("numOfRequestPending",-1);

        category.setText(eWalletRequest.getCategory());
        amount.setText(String.format("$%.2f",eWalletRequest.getAmount()));
        comment.setText(eWalletRequest.getComment());


        //region accepting the request
        findViewById(R.id.btnRequestAccept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                answerRequest(true);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(TestingParentEmergencyWalletAnswer.this);
                builder.setMessage("Are you sure to accept it?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });
        //endregion



        //region rejecting the request
        findViewById(R.id.btnRequestReject).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                answerRequest(false);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(TestingParentEmergencyWalletAnswer.this);
                builder.setMessage("Are you sure to reject it?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();



            }
        });







    }

    public void answerRequest(boolean ans){

        dialog.setMessage("Logging. Please wait..."); //show dialog
        dialog.show();

        final DatabaseReference drEmergencyWallet = FirebaseDatabase.getInstance().getReference().child("eWalletRequestCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pending");
        drEmergencyWallet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (int i=position; i<numOfRequestPending-1;i++){
                    drEmergencyWallet.child(i+"").setValue(dataSnapshot.child((i+1)+"").getValue(EWallet.class));
                }
                drEmergencyWallet.child((numOfRequestPending-1)+"").removeValue();

                drEmergencyWallet.child("numOfRequest").setValue((numOfRequestPending-1));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //region send data to pushNotificationCustomer to notify the customer about the result of the request
        String status;
        if (ans){
            status ="accepted";
        }
        else{
            status = "rejected";
        }
        DatabaseReference drPushNotificationCustoemr = FirebaseDatabase.getInstance().getReference().child("pushNotificationCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drPushNotificationCustoemr.child("title").setValue("Emergency Wallet");
        drPushNotificationCustoemr.child("body").setValue(status);
        drPushNotificationCustoemr.child("situation").setValue("emergency");
        drPushNotificationCustoemr.child("numOfRequest").setValue(new Random().nextInt(100));
        //endregion



        if(ans){ //if the parent accept the request

            //region send data to firebase (eWalletRequestCustomer -- accepted)
            final DatabaseReference drRequestAccepted = FirebaseDatabase.getInstance().getReference().child("eWalletRequestCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("accepted");
            drRequestAccepted.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfRequest = Integer.parseInt(dataSnapshot.child("numOfRequest").getValue().toString().trim());
                    eWalletRequest.setStatus("accepted");
                    eWalletRequest.setAnswerTime(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));
                    drRequestAccepted.child((numOfRequest)+"").setValue(eWalletRequest);

                    drRequestAccepted.child("numOfRequest").setValue(numOfRequest+1);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            if (eWalletRequest.getCategory().equalsIgnoreCase("food")){
                if (budget.getCategory().getFood().getChangedValueMax()==-1) {
                    budget.getCategory().getFood().setChangedValueMax(budget.getCategory().getFood().getDefaultValueMax() + eWalletRequest.getAmount());
                    budget.getCategory().getFood().setChangedValueMin(budget.getCategory().getFood().getDefaultValueMin());
                }
                else{
                    budget.getCategory().getFood().setChangedValueMax(budget.getCategory().getFood().getChangedValueMax() + eWalletRequest.getAmount());

                }

                if (budget.getCategory().getFood().getChangedValueMin()==-1){
                    budget.getCategory().getFood().setChangedValueMin(budget.getCategory().getFood().getDefaultValueMin());
                }else{
                    budget.getCategory().getFood().setChangedValueMin(budget.getCategory().getFood().getChangedValueMin());

                }


                budget.getCategory().getFood().setAmount(budget.getCategory().getFood().getAmount() + eWalletRequest.getAmount());
                budget.getCategory().getFood().setLeft(budget.getCategory().getFood().getLeft()+eWalletRequest.getAmount());
            }
            else if (eWalletRequest.getCategory().equalsIgnoreCase("drink")){
                if (budget.getCategory().getDrink().getChangedValueMax()==-1) {
                    budget.getCategory().getDrink().setChangedValueMax(budget.getCategory().getDrink().getDefaultValueMax() + eWalletRequest.getAmount());
                    budget.getCategory().getDrink().setChangedValueMin(budget.getCategory().getDrink().getDefaultValueMin());

                }else{
                    budget.getCategory().getDrink().setChangedValueMax(budget.getCategory().getDrink().getChangedValueMax() + eWalletRequest.getAmount());

                }

                if (budget.getCategory().getDrink().getChangedValueMin()==-1){
                    budget.getCategory().getDrink().setChangedValueMin(budget.getCategory().getDrink().getDefaultValueMin());
                }else{
                    budget.getCategory().getDrink().setChangedValueMin(budget.getCategory().getDrink().getChangedValueMin());

                }

                budget.getCategory().getDrink().setAmount(budget.getCategory().getDrink().getAmount() + eWalletRequest.getAmount());
                budget.getCategory().getDrink().setLeft(budget.getCategory().getDrink().getLeft()+eWalletRequest.getAmount());
            }
            else if (eWalletRequest.getCategory().equalsIgnoreCase("stationery")){
                if (budget.getCategory().getStationery().getChangedValueMax()==-1) {
                    budget.getCategory().getStationery().setChangedValueMax(budget.getCategory().getStationery().getDefaultValueMax() + eWalletRequest.getAmount());
                    budget.getCategory().getStationery().setChangedValueMin(budget.getCategory().getStationery().getDefaultValueMin());

                }else{
                    budget.getCategory().getStationery().setChangedValueMax(budget.getCategory().getStationery().getChangedValueMax() + eWalletRequest.getAmount());

                }

                if (budget.getCategory().getStationery().getChangedValueMin()==-1){
                    budget.getCategory().getStationery().setChangedValueMin(budget.getCategory().getStationery().getDefaultValueMin());
                }else{
                    budget.getCategory().getStationery().setChangedValueMin(budget.getCategory().getStationery().getChangedValueMin());

                }

                budget.getCategory().getStationery().setAmount(budget.getCategory().getStationery().getAmount() + eWalletRequest.getAmount());
                budget.getCategory().getStationery().setLeft(budget.getCategory().getStationery().getLeft()+eWalletRequest.getAmount());
            }
            else if (eWalletRequest.getCategory().equalsIgnoreCase("others")){
                if (budget.getCategory().getOthers().getChangedValueMax()==-1) {
                    budget.getCategory().getOthers().setChangedValueMax(budget.getCategory().getOthers().getDefaultValueMax() + eWalletRequest.getAmount());
                    budget.getCategory().getOthers().setChangedValueMin(budget.getCategory().getOthers().getDefaultValueMin());

                }else{
                    budget.getCategory().getOthers().setChangedValueMax(budget.getCategory().getOthers().getChangedValueMax() + eWalletRequest.getAmount());

                }

                if (budget.getCategory().getOthers().getChangedValueMin()==-1){
                    budget.getCategory().getOthers().setChangedValueMin(budget.getCategory().getOthers().getDefaultValueMin());
                }else{
                    budget.getCategory().getOthers().setChangedValueMin(budget.getCategory().getOthers().getChangedValueMin());

                }

                budget.getCategory().getOthers().setAmount(budget.getCategory().getOthers().getAmount() + eWalletRequest.getAmount());
                budget.getCategory().getOthers().setLeft(budget.getCategory().getOthers().getLeft()+eWalletRequest.getAmount());
            }


            if (budget.getChangedAllowance()==-1) {
                budget.setChangedAllowance(budget.getAllowance() + eWalletRequest.getAmount()); // add the eWalletRequest money to the changedAllowance
            }
            else{
                budget.setChangedAllowance(budget.getChangedAllowance() + eWalletRequest.getAmount());
            }

            drBudget.child(day1+"").setValue(budget);

        }
        else{ //if the parent reject the request



            //region send data to firebase (eWalletRequestCustomer -- rejected)
            final DatabaseReference drRequestRejected = FirebaseDatabase.getInstance().getReference().child("eWalletRequestCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("rejected");
            drRequestRejected.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfRequest = Integer.parseInt(dataSnapshot.child("numOfRequest").getValue().toString().trim());
                    eWalletRequest.setStatus("rejected");
                    eWalletRequest.setAnswerTime(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));
                    drRequestRejected.child((numOfRequest)+"").setValue(eWalletRequest);

                    drRequestRejected.child("numOfRequest").setValue(numOfRequest+1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms

                //region must do at the last update to firebase
                dialog.dismiss();
                Toast.makeText(TestingParentEmergencyWalletAnswer.this, "Sent Successfully", Toast.LENGTH_SHORT).show();

                TestingParentEmergencyWallet.getInstance().finish();
                Intent intent = new Intent(TestingParentEmergencyWalletAnswer.this, TestingParentEmergencyWallet.class);
                startActivity(intent);
                finish();
                //endregion

            }
        }, 500);

    }

    private void getCategory(){


        drBudget = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day");
        drBudget.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                day1 = calendar.get(Calendar.DAY_OF_WEEK)-2;

                if (day1==-1){
                    day1 = 6;
                }

                budget = dataSnapshot.child(day1+"").getValue(Budget.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
