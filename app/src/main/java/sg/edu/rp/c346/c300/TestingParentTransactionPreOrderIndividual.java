package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;

public class TestingParentTransactionPreOrderIndividual extends AppCompatActivity {

    TextView tvTID, tvFoodName, tvFoodPrice, tvStallName, tvFoodStallOperation, tvQuantity, tvAddOn, tvAdditionalNotes, tvSchool;
    ImageView ivimage;

    Collection item; // get the food item from CollectionAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_transaction_pre_order_individual);



        tvTID = findViewById(R.id.tvIndividualTID);
        tvFoodName = findViewById(R.id.tvIndividualFoodName);
        tvFoodPrice = findViewById(R.id.tvIndividualFoodPrice);
        tvStallName = findViewById(R.id.tvIndividualFoodStallName);
        tvFoodStallOperation = findViewById(R.id.tvIndividualFoodStallDuration);
        tvQuantity = findViewById(R.id.IndividualQuantityDisplay);
        tvAddOn = findViewById(R.id.tvIndividualAddOn);
        tvAdditionalNotes = findViewById(R.id.tvIndividualAddtionalNotes);
        ivimage = findViewById(R.id.IndividualFoodImage);
        tvSchool = findViewById(R.id.tvIndividualSchool);

//        item = (Collection) getIntent().getSerializableExtra("item");

        tvTID.setText(item.gettId());
        tvFoodName.setText(item.getName());
        tvFoodPrice.setText(String.format("$%.2f",item.getPrice()));
        tvStallName.setText("Stall: "+item.getStallName());
        tvFoodStallOperation.setText(String.format("Working from %s to %s", MainpageActivity.convertDateToString(MainpageActivity.convertStringToDate(item.getStartTime(),"HHmm"), "hh:mm a"),MainpageActivity.convertDateToString(MainpageActivity.convertStringToDate(item.getEndTime(),"HHmm"), "hh:mm a") ));
        tvQuantity.setText("x"+item.getQuantity());
        String allAddOn ="";
        if (item.getAddOn().size()==0){
            tvAddOn.setText("No Add On");
        }
        else{
            for (AddOn i : item.getAddOn()){
                allAddOn+= String.format("%-60s  +$%.2f\n", i.getName(), i.getPrice());
            }
            tvAddOn.setText(allAddOn);
        }

        if (item.getAdditionalNote().isEmpty()){
            tvAdditionalNotes.setText("No Additional Notes");
        }
        else{
            tvAdditionalNotes.setText(item.getAdditionalNote());
        }
        Glide.with(TestingParentTransactionPreOrderIndividual.this).load(item.getImage()).centerCrop().into(ivimage);
        tvSchool.setText(item.getSchool());




//        findViewById(R.id.IndividualDenyStallBtn).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case DialogInterface.BUTTON_POSITIVE:
//                                setDeny();
//                                break;
//
//                            case DialogInterface.BUTTON_NEGATIVE:
//                                //No button clicked
//                                break;
//                        }
//                    }
//                };
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(TestingParentTransactionPreOrderIndividual.this);
//                builder.setMessage("Are you sure to deny kid to purchase any item from this stall?").setPositiveButton("Yes", dialogClickListener)
//                        .setNegativeButton("No", dialogClickListener).show();
//
//
//
//
//
//            }
//        });





    }


    private void setDeny(){
        final DatabaseReference drDenyList = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school").child(item.getSchool());
        drDenyList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfDeny = Integer.parseInt(dataSnapshot.child("numOfDeny").getValue().toString());

                for (int i =0; i<numOfDeny;i++){
                    if (Integer.parseInt(dataSnapshot.child(i+"").getValue().toString()) == (item.getStallId())){
                        Toast.makeText(TestingParentTransactionPreOrderIndividual.this, "Already Denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                drDenyList.child(numOfDeny+"").setValue(item.getStallId());
                drDenyList.child("numOfDeny").setValue(numOfDeny+1);
                Toast.makeText(TestingParentTransactionPreOrderIndividual.this, "Denied Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



}
