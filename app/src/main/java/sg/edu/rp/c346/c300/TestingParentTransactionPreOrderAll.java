package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.adapter.Collection2Adapter;
import sg.edu.rp.c346.c300.adapter.CollectionAdapter;
import sg.edu.rp.c346.c300.adapter.CollectionWalkInAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.NotificationHeader;
import sg.edu.rp.c346.c300.model.WalkIn;

public class TestingParentTransactionPreOrderAll extends AppCompatActivity {

    ListView preOrderListView;

    DatabaseReference drHC;

    ArrayList<Collection> preOrderList = new ArrayList<>();
    CollectionAdapter collectionPreOrderAdapter;

    ArrayList<WalkIn> walkInList = new ArrayList<>();
    CollectionWalkInAdapter collectionWalkInAdapter;

    TextView transactionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_transaction_pre_order_all);


        transactionTitle = findViewById(R.id.transactionTitle);

        preOrderListView = findViewById(R.id.preOrderListView);

        String page = getIntent().getStringExtra("page");

        if (page.equals("PreOrder")) {

            transactionTitle.setText("PreOrder Transaction");


            intiDataPreOrder();



        }else{

            transactionTitle.setText("Walk-In Transaction");


            initDataWalkIn();
        }
        //endregion


    }



    private void intiDataPreOrder(){
        drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
        drHC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString());
                preOrderList.clear();
                for (int i =0; i<numOfOrder;i++){
                    String foodName = dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString();
                    double foodPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("price").getValue().toString());
                    String dateTimeOrder = dataSnapshot.child(Integer.toString(i)).child("dateTimeOrder").getValue().toString();
                    String dateTimePurchased = dataSnapshot.child(Integer.toString(i)).child("dateTimePurchased").getValue().toString();
                    int quantity = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("quantity").getValue().toString());
                    String stallName = dataSnapshot.child(Integer.toString(i)).child("stallName").getValue().toString();
                    int  stallId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("stallId").getValue().toString());
                    int  foodId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("foodId").getValue().toString());
                    double  totalPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("totalPrice").getValue().toString());
                    String  additionalNote = dataSnapshot.child(Integer.toString(i)).child("additionalNote").getValue().toString();
                    String  lastChanges = dataSnapshot.child(Integer.toString(i)).child("lastChanges").getValue().toString();
                    int  lastChangesInMin = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("lastChangesInMin").getValue().toString());
                    String tId = dataSnapshot.child(Integer.toString(i)).child("tId").getValue().toString();
                    String startTime = dataSnapshot.child(Integer.toString(i)).child("startTime").getValue().toString();
                    String endTime = dataSnapshot.child(Integer.toString(i)).child("endTime").getValue().toString();
                    String customerUID= dataSnapshot.child(Integer.toString(i)).child("customerUID").getValue().toString();
                    String stallUID = dataSnapshot.child(Integer.toString(i)).child("stallUID").getValue().toString();
                    String status= dataSnapshot.child(Integer.toString(i)).child("status").getValue().toString();
                    String image= dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();
                    String school= dataSnapshot.child(Integer.toString(i)).child("school").getValue().toString();

                    String combinedDateTimeOrderAndPurchased = dateTimeOrder+"\n\nPurchased Made: "+dateTimePurchased;


                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    addOnList.clear();
                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                    for (int h =0; h<numOfAddOn;h++){
//                        String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
//                        double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
//                        AddOn addOn = new AddOn(addOnName, addOnPrice);
//                        addOnList.add(addOn);
                        addOnList.add(dataSnapshot.child(i+"").child("addOn").child(h+"").getValue(AddOn.class));
                    }

                    Collection collection = new Collection(foodName, foodPrice, combinedDateTimeOrderAndPurchased, quantity, stallName, stallId, foodId, totalPrice, addOnList,additionalNote,lastChanges,lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status, image,school);
                    preOrderList.add(collection);
                }

                collectionPreOrderAdapter = new CollectionAdapter(TestingParentTransactionPreOrderAll.this, preOrderList, true);
                preOrderListView.setAdapter(collectionPreOrderAdapter);
                collectionPreOrderAdapter.notifyDataSetChanged();



                preOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        setDeny(preOrderList.get(position));
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(TestingParentTransactionPreOrderAll.this);
                        builder.setMessage("Are you sure to deny kid to purchase any item from this stall?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }



    public void initDataWalkIn(){
        DatabaseReference drWalkInNotification = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("walkIn");
        drWalkInNotification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfWalkIn").getValue().toString());

                for (int i = numOfPreOrder-1; i >=0; i--) {
                    String foodName = dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString();
                    double foodPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("price").getValue().toString());
                    String dateTimeOrder = dataSnapshot.child(Integer.toString(i)).child("dateTimeOrder").getValue().toString();
                    int quantity = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("quantity").getValue().toString());
                    String stallName = dataSnapshot.child(Integer.toString(i)).child("stallName").getValue().toString();
                    int stallId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("stallId").getValue().toString());
                    int foodId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("foodId").getValue().toString());
                    double totalPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("totalPrice").getValue().toString());
                    String tId = dataSnapshot.child(Integer.toString(i)).child("tId").getValue().toString();
                    String customerUID = dataSnapshot.child(Integer.toString(i)).child("customerUID").getValue().toString();
                    String stallUID = dataSnapshot.child(Integer.toString(i)).child("stallUID").getValue().toString();
                    String status = "Completed";
                    String image = dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();
                    String school = dataSnapshot.child(Integer.toString(i)).child("school").getValue().toString();


                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    addOnList.clear();
                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                    for (int h = 0; h < numOfAddOn; h++) {
                        String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                        double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                        AddOn addOn = new AddOn(addOnName, addOnPrice);
                        addOnList.add(addOn);
                    }



                    WalkIn walkIn = new WalkIn(foodName, foodPrice, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, tId, customerUID, stallUID, image, school);
                    walkInList.add(walkIn);


                }


                collectionWalkInAdapter = new CollectionWalkInAdapter(TestingParentTransactionPreOrderAll.this, walkInList);
                preOrderListView.setAdapter(collectionWalkInAdapter);
                collectionWalkInAdapter.notifyDataSetChanged();



                preOrderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        setDeny(walkInList.get(position));
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(TestingParentTransactionPreOrderAll.this);
                        builder.setMessage("Are you sure to deny kid to purchase any item from this stall?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void setDeny(final WalkIn item){
        final DatabaseReference drDenyList = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school").child(item.getSchool());
        drDenyList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfDeny = Integer.parseInt(dataSnapshot.child("numOfDeny").getValue().toString());

                for (int i =0; i<numOfDeny;i++){
                    if (Integer.parseInt(dataSnapshot.child(i+"").getValue().toString()) == (item.getStallId())){
                        Toast.makeText(TestingParentTransactionPreOrderAll.this, "Already Denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                drDenyList.child(numOfDeny+"").setValue(item.getStallId());
                drDenyList.child("numOfDeny").setValue(numOfDeny+1);
                Toast.makeText(TestingParentTransactionPreOrderAll.this, "Denied Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    private void setDeny(final Collection item){
        final DatabaseReference drDenyList = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school").child(item.getSchool());
        drDenyList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfDeny = Integer.parseInt(dataSnapshot.child("numOfDeny").getValue().toString());

                for (int i =0; i<numOfDeny;i++){
                    if (Integer.parseInt(dataSnapshot.child(i+"").getValue().toString()) == (item.getStallId())){
                        Toast.makeText(TestingParentTransactionPreOrderAll.this, "Already Denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                drDenyList.child(numOfDeny+"").setValue(item.getStallId());
                drDenyList.child("numOfDeny").setValue(numOfDeny+1);
                Toast.makeText(TestingParentTransactionPreOrderAll.this, "Denied Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
