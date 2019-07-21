package sg.edu.rp.c346.c300;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import sg.edu.rp.c346.c300.adapter.EWalletAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.EWallet;

public class EmergencyWalletAdd extends AppCompatActivity {

    EditText etAmount, etComment;
    MaterialBetterSpinner spinnerCategory;

    ArrayList<String> allCategoryList = new ArrayList<>();
    ArrayAdapter<String> categoryAdapter;

    String category;
    double amount;
    String comment;

    //Showing the loading
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_wallet_add);

        dialog = new ProgressDialog(this);


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


        etAmount = findViewById(R.id.etAmount);
        etComment = findViewById(R.id.etComment);
        spinnerCategory = findViewById(R.id.spinnerCategory);



        allCategoryList = getIntent().getStringArrayListExtra("allCategoryList");


        categoryAdapter = new ArrayAdapter<String>(EmergencyWalletAdd.this, android.R.layout.simple_list_item_1,allCategoryList);
        spinnerCategory.setAdapter(categoryAdapter);




        findViewById(R.id.btnRequestConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!spinnerCategory.getText().toString().trim().isEmpty() && !etAmount.getText().toString().isEmpty() && !etComment.getText().toString().isEmpty()) {

                    dialog.setMessage("Logging. Please wait..."); //show dialog
                    dialog.show();

                    category = spinnerCategory.getText().toString().trim();
                    amount = Double.parseDouble(etAmount.getText().toString());
                    comment = etComment.getText().toString();


                    //region send to pushNotificationParent to tell the parent through notification
                    final DatabaseReference drPushNotificationParent = FirebaseDatabase.getInstance().getReference().child("pushNotificationParent").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    drPushNotificationParent.child("title").setValue("Emergency Wallet");
                    drPushNotificationParent.child("body").setValue(comment+".");
                    drPushNotificationParent.child("situation").setValue("emergency");
                    drPushNotificationParent.child("numOfRequest").setValue(new Random().nextInt(100));


                    //endregion


                    //region send to eWalletRequestCustomer to record all the pending record
                    final DatabaseReference drEmergencyWallet = FirebaseDatabase.getInstance().getReference().child("eWalletRequestCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pending");
                    drEmergencyWallet.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int numOfRequest = Integer.parseInt(dataSnapshot.child("numOfRequest").getValue().toString().trim());

                            EWallet eWallet = new EWallet(category, amount, comment,"pending", MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"), "nil");
                            drEmergencyWallet.child(numOfRequest+"").setValue(eWallet);


                            drEmergencyWallet.child("numOfRequest").setValue(numOfRequest+1);


                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms

                                    //region must do at the last update to firebase
                                    dialog.dismiss();
                                    Toast.makeText(EmergencyWalletAdd.this, "Sent Successfully", Toast.LENGTH_SHORT).show();

                                    EmergencyWallet.getInstance().finish();
                                    Intent intent = new Intent(EmergencyWalletAdd.this, EmergencyWallet.class);
                                    startActivity(intent);
                                    finish();
                                    //endregion

                                }
                            }, 500);



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    //endregion






                }
                else{
                    if (spinnerCategory.getText().toString().isEmpty()){
                        spinnerCategory.setError("Please fill this");
                    }

                    if (etAmount.getText().toString().isEmpty()){
                        etAmount.setError("Please fill this");
                    }

                    if (etComment.getText().toString().isEmpty()){
                        etComment.setError("Please fill this");
                    }


                }

            }
        });





    }
}
