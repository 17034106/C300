package sg.edu.rp.c346.c300;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.rp.c346.c300.model.Collection;

public class PreOrderCompleted extends AppCompatActivity {


    TextView etTID, etStallName, etDate, etLocation, etTotalPrice, etOrderInformation;

    String dateTimePurchased; // for retrieving data from firebase

    Collection collection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_order_completed);


        etTID = findViewById(R.id.tID);
        etStallName = findViewById(R.id.stall);
        etDate = findViewById(R.id.dateTime);
        etLocation = findViewById(R.id.location);
        etTotalPrice =  findViewById(R.id.totalPrice);
        etOrderInformation = findViewById(R.id.orderDetails);


        if (getIntent().getBooleanExtra("justReceived", false)) {  // if this activity is intent from IndividualCollectionOrder (which the customer just pressed receive order)

            collection = (Collection) getIntent().getSerializableExtra("collection");
            dateTimePurchased = getIntent().getStringExtra("dateTimePurchased");

            etTID.setText("TID: " + collection.gettId());
            etStallName.setText("Stall: " + collection.getStallName());
            etDate.setText("Date: " + dateTimePurchased);
            etLocation.setText("Location: " + collection.getSchool());
            etTotalPrice.setText(String.format("Total Price: $%.2f", collection.getTotalPrice()));


            String orderDetails = "";
            orderDetails += "Food: " + collection.getName() + "\n";
            if (collection.getAddOn().size() != 0) {
                orderDetails += "Add On Item:\n";
                for (int i = 0; i < collection.getAddOn().size(); i++) {
                    orderDetails += "     " + collection.getAddOn().get(i).getName() + "\n";
                }
            }

            if (!collection.getAdditionalNote().equals("")) {
                orderDetails += "Additional Notes:\n";
                orderDetails += "     " + collection.getAdditionalNote() + "\n";
            }

            orderDetails+="Quantity: "+collection.getQuantity();


            etOrderInformation.setText(orderDetails);



        }
        else{ // if this activity is intent from HistoryTransactionPreOrder

            DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
            drHC.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    dateTimePurchased = dataSnapshot.child(Integer.toString((getIntent().getIntExtra("collectionListSize", -1)-getIntent().getIntExtra("position", -1) -1 ))).child("dateTimePurchased").getValue().toString();
                    collection = (Collection) getIntent().getSerializableExtra("collection");
                    etTID.setText("TID: " + collection.gettId());
                    etStallName.setText("Stall: " + collection.getStallName());
                    etDate.setText("Date: " + dateTimePurchased);
                    etLocation.setText("Location: " + collection.getSchool());
                    etTotalPrice.setText(String.format("Total Price: $%.2f", collection.getTotalPrice()));


                    String orderDetails = "";
                    orderDetails += "Food: " + collection.getName() + "\n";
                    if (collection.getAddOn().size() != 0) {
                        orderDetails += "Add On Item:\n";
                        for (int i = 0; i < collection.getAddOn().size(); i++) {
                            orderDetails += "     " + collection.getAddOn().get(i).getName() + "\n";
                        }
                    }

                    if (!collection.getAdditionalNote().equals("")) {
                        orderDetails += "Additional Notes:\n";
                        orderDetails += "     " + collection.getAdditionalNote() + "\n";
                    }

                    orderDetails+="Quantity: "+collection.getQuantity();


                    etOrderInformation.setText(orderDetails);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
