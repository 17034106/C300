package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.adapter.CollectionAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.Customer;

public class IndividualCollectionOrder extends AppCompatActivity {


    TextView tvTID, tvFoodName, tvFoodPrice, tvStallName, tvFoodStallOperation, tvLastChange, tvQuantity, tvAddOn, tvAdditionalNotes;
    ArrayList<AddOn> addOnListIndividual;
    ImageView ivimage;

    static IndividualCollectionOrder thisAcitivy;

    String tId;

    int positionInCustomer; //finding the position of the confirmed order in customer firebase (TC)
    int positionInOwner; //finding the position of the confirmed order in Owner firebase (TO)

    DatabaseReference drTO;
    String customerSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_collection_order);

        thisAcitivy = this;

        tvTID = findViewById(R.id.tvIndividualTID);
        tvFoodName = findViewById(R.id.tvIndividualFoodName);
        tvFoodPrice = findViewById(R.id.tvIndividualFoodPrice);
        tvStallName = findViewById(R.id.tvIndividualFoodStallName);
        tvFoodStallOperation = findViewById(R.id.tvIndividualFoodStallDuration);
        tvLastChange = findViewById(R.id.tvIndividualLastChanges);
        tvQuantity = findViewById(R.id.IndividualQuantityDisplay);
        tvAddOn = findViewById(R.id.tvIndividualAddOn);
        tvAdditionalNotes = findViewById(R.id.tvIndividualAddtionalNotes);
        tvAdditionalNotes = findViewById(R.id.tvIndividualAddtionalNotes);
        ivimage = findViewById(R.id.IndividualFoodImage);



        final Intent intentReceive = getIntent();

        tId = intentReceive.getStringExtra("tId");
        tvTID.setText(tId);
        tvFoodName.setText(intentReceive.getStringExtra("foodName"));
        tvFoodPrice.setText(String.format("$%.2f", intentReceive.getDoubleExtra("foodPrice",0)));
        tvStallName.setText(String.format("Stall: %s",intentReceive.getStringExtra("stallName")));
        Glide.with(IndividualCollectionOrder.this).load(intentReceive.getStringExtra("image")).centerCrop().into(ivimage);

        Date stallStartOperationDate = MainpageActivity.convertStringToDate(intentReceive.getStringExtra("startTime"), "HHmm");
        Date stallEndOperationDate = MainpageActivity.convertStringToDate(intentReceive.getStringExtra("endTime"), "HHmm");

        tvFoodStallOperation.setText(String.format("Working from %s to %s", MainpageActivity.convertDateToString(stallStartOperationDate, "hh:mm a"),MainpageActivity.convertDateToString(stallEndOperationDate, "hh:mm a") ));
        String lastChangeDisplay = "Changes can only be made before <b>"+intentReceive.getStringExtra("lastChanges")+"</b>";
        tvLastChange.setText(Html.fromHtml(lastChangeDisplay));
        tvQuantity.setText(String.format("x%d", intentReceive.getIntExtra("quantity", 0)));
        if (intentReceive.getStringExtra("additionalNote").isEmpty()){
            tvAdditionalNotes.setText("No Additional Notes");
        }
        else{
            tvAdditionalNotes.setText(intentReceive.getStringExtra("additionalNote"));
        }


        addOnListIndividual = CollectionAdapter.addOnListIndividual;
        String addOnString ="";
        for (AddOn i: addOnListIndividual){
            addOnString+=String.format("%-70s +$%.2f\n", i.getName(), i.getPrice());
        }

        if (addOnString.isEmpty()){
            tvAddOn.setText("No Add On");
        }
        else {
            tvAddOn.setText(addOnString.trim());
        }

        //region intent to IndividualEditFoodDisplay to change the confirmed order
        findViewById(R.id.IndividualEditOrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualCollectionOrder.this, IndividualEditFoodDisplay.class);
                intent.putExtra("tId",intentReceive.getStringExtra("tId") );
                intent.putExtra("foodName", intentReceive.getStringExtra("foodName"));
                intent.putExtra("foodPrice", intentReceive.getDoubleExtra("foodPrice",0));
                intent.putExtra("stallName", intentReceive.getStringExtra("stallName"));
                intent.putExtra("stallId", intentReceive.getIntExtra("stallId", 0));
                intent.putExtra("startTime", intentReceive.getStringExtra("startTime"));
                intent.putExtra("endTime", intentReceive.getStringExtra("endTime"));
                intent.putExtra("quantity",intentReceive.getIntExtra("quantity", 0));
                intent.putExtra("additionalNote",intentReceive.getStringExtra("additionalNote"));
                intent.putExtra("status",intentReceive.getStringExtra("status"));
                intent.putExtra("image",intentReceive.getStringExtra("image"));
                intent.putExtra("lastChangesInMin", intentReceive.getIntExtra("lastChangesInMin",-1));
                startActivity(intent);
            }
        });
        //endregion


        //region Delete the confirmed order
        findViewById(R.id.IndividualDeleteOrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deleteOrder();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(IndividualCollectionOrder.thisAcitivy);
                builder.setMessage("Do you want to delete?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();




            }
        });



        //endregion


    }

    public static IndividualCollectionOrder getInstance(){
        return thisAcitivy;
    }


    //region delete the confirmed order
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void deleteOrder(){


        //region delete the confirmed data in Customer firebase (TC)
        final DatabaseReference drTC = FirebaseDatabase.getInstance().getReference().child("tc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order");

        drTC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfOrderInCustomer= Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());
                ArrayList<Collection> collectionList = new ArrayList<>();

                for (int i =0;i<numOfOrderInCustomer;i++){
                    if (dataSnapshot.child(i+"").child("tId").getValue().toString().equals(tId)){
                        positionInCustomer = i;
                    }

                    //region add all the collection orders in the collectionList

                    String name = dataSnapshot.child(i+"").child("name").getValue().toString();
                    double price = Double.parseDouble(dataSnapshot.child(i+"").child("price").getValue().toString());
                    String dataTimeOrder = dataSnapshot.child(i+"").child("dateTimeOrder").getValue().toString();
                    int quantity = Integer.parseInt(dataSnapshot.child(i+"").child("quantity").getValue().toString());
                    String stallName = dataSnapshot.child(i+"").child("stallName").getValue().toString();
                    int stallId = Integer.parseInt(dataSnapshot.child(i+"").child("stallId").getValue().toString());
                    int foodId = Integer.parseInt(dataSnapshot.child(i+"").child("foodId").getValue().toString());
                    double totalPrice = Double.parseDouble(dataSnapshot.child(i+"").child("totalPrice").getValue().toString());
                    String additionalNote = dataSnapshot.child(i+"").child("additionalNote").getValue().toString();
                    String lastChanges = dataSnapshot.child(i+"").child("lastChanges").getValue().toString();
                    int lastChangesInMin = Integer.parseInt(dataSnapshot.child(i+"").child("lastChangesInMin").getValue().toString());
                    String tId = dataSnapshot.child(i+"").child("tId").getValue().toString();
                    String startTime = dataSnapshot.child(i+"").child("startTime").getValue().toString();
                    String endTime = dataSnapshot.child(i+"").child("endTime").getValue().toString();
                    String customerUID = dataSnapshot.child(i+"").child("customerUID").getValue().toString();
                    String status = dataSnapshot.child(i+"").child("status").getValue().toString();
                    String image = dataSnapshot.child(i+"").child("imageurl").getValue().toString();

                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(i+"").child("addOn").child("numOfAddOn").getValue().toString());

                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    for (int h =0; h < numOfAddOn;h++){
                        String addOnName = dataSnapshot.child(i+"").child("addOn").child(h+"").child("name").getValue().toString();
                        double addOnPrice = Double.parseDouble(dataSnapshot.child(i+"").child("addOn").child(h+"").child("price").getValue().toString());
                        addOnList.add(new AddOn(addOnName, addOnPrice));
                    }

                    collectionList.add(new Collection(name, price, dataTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, status,image));

                    //endregion
                }

                drTC.child(positionInCustomer+"").removeValue();


                ArrayList<Collection> readdedCollectionList = new ArrayList<>();

                int nextPositionInCustomer = positionInCustomer+1;

                for (int i =nextPositionInCustomer; i<collectionList.size(); i++){
                    readdedCollectionList.add(collectionList.get(i));
                    drTC.child(i+"").removeValue();
                }


                for (int i =0; i<readdedCollectionList.size();i++) {
                    drTC.child(Integer.toString(positionInCustomer + i)).child("dateTimeOrder").setValue(readdedCollectionList.get(i).getDateTimeOrder());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("name").setValue(readdedCollectionList.get(i).getName());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("price").setValue(readdedCollectionList.get(i).getPrice());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("quantity").setValue(readdedCollectionList.get(i).getQuantity());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("stallName").setValue(readdedCollectionList.get(i).getStallName());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("totalPrice").setValue(readdedCollectionList.get(i).getTotalPrice());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("additionalNote").setValue(readdedCollectionList.get(i).getAdditionalNote() + "");
                    drTC.child(Integer.toString(positionInCustomer + i)).child("lastChanges").setValue(readdedCollectionList.get(i).getLastChanges());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("stallId").setValue(readdedCollectionList.get(i).getStallId());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("foodId").setValue(readdedCollectionList.get(i).getFoodId());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("tId").setValue(readdedCollectionList.get(i).gettId());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("startTime").setValue(readdedCollectionList.get(i).getStartTime());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("endTime").setValue(readdedCollectionList.get(i).getEndTime());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("lastChangesInMin").setValue(readdedCollectionList.get(i).getLastChangesInMin());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("customerUID").setValue(readdedCollectionList.get(i).getCustomerUID());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("status").setValue(readdedCollectionList.get(i).getStatus());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("imageurl").setValue(readdedCollectionList.get(i).getImage());

                    drTC.child(Integer.toString(positionInCustomer + i)).child("addOn").child("numOfAddOn").setValue(0);
                    for (int h = 0; h < readdedCollectionList.get(i).getAddOnList().size(); h++) {
                        drTC.child(Integer.toString(positionInCustomer + i)).child("addOn").child(Integer.toString(h)).child("name").setValue(readdedCollectionList.get(i).getAddOnList().get(h).getName());
                        drTC.child(Integer.toString(positionInCustomer + i)).child("addOn").child(Integer.toString(h)).child("price").setValue(readdedCollectionList.get(i).getAddOnList().get(h).getPrice());
                        drTC.child(Integer.toString(positionInCustomer + i)).child("addOn").child("numOfAddOn").setValue(h + 1);
                    }
                }

                drTC.child("numOfOrder").setValue(positionInCustomer +readdedCollectionList.size());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion


        //region delete the confirmed data in Owner firebase (TO)

        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
        final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                customerSchool = customer.getCustomerschool(); // getting the user's school

                drTO = FirebaseDatabase.getInstance().getReference().child("to").child("school").child(customerSchool).child("stall").child(getIntent().getIntExtra("stallId",-1)+"").child("order");
                drTO.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numOfOrderInOwner = Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());

                        for (int i =0; i<numOfOrderInOwner;i++){
                            if (dataSnapshot.child(i+"").child("tId").getValue().toString().equals(getIntent().getStringExtra("tId"))){
                                positionInOwner = i;
                            }
                        }
                        drTO.child(positionInOwner+"").removeValue();

                        ArrayList<Collection> readdedOwnerCollectionList = new ArrayList<>();
                        int nextPositionInOwner = positionInOwner+1;

                        for (int i =nextPositionInOwner ; i<numOfOrderInOwner;i++){
                            String name = dataSnapshot.child(i+"").child("name").getValue().toString();
                            double price = Double.parseDouble(dataSnapshot.child(i+"").child("price").getValue().toString());
                            String dataTimeOrder = dataSnapshot.child(i+"").child("dateTimeOrder").getValue().toString();
                            int quantity = Integer.parseInt(dataSnapshot.child(i+"").child("quantity").getValue().toString());
                            String stallName = dataSnapshot.child(i+"").child("stallName").getValue().toString();
                            int stallId = Integer.parseInt(dataSnapshot.child(i+"").child("stallId").getValue().toString());
                            int foodId = Integer.parseInt(dataSnapshot.child(i+"").child("foodId").getValue().toString());
                            double totalPrice = Double.parseDouble(dataSnapshot.child(i+"").child("totalPrice").getValue().toString());
                            String additionalNote = dataSnapshot.child(i+"").child("additionalNote").getValue().toString();
                            String lastChanges = dataSnapshot.child(i+"").child("lastChanges").getValue().toString();
                            int lastChangesInMin =0; //useless one as owner do not need to care this
                            String tId = dataSnapshot.child(i+"").child("tId").getValue().toString();
                            String startTime = dataSnapshot.child(i+"").child("startTime").getValue().toString();
                            String endTime = dataSnapshot.child(i+"").child("endTime").getValue().toString();
                            String customerUID = dataSnapshot.child(i+"").child("customerUID").getValue().toString();
                            String status = dataSnapshot.child(i+"").child("status").getValue().toString();
                            String image = dataSnapshot.child(i+"").child("imageurl").getValue().toString();

                            int numOfAddOn = Integer.parseInt(dataSnapshot.child(i+"").child("addOn").child("numOfAddOn").getValue().toString());

                            ArrayList<AddOn> addOnList = new ArrayList<>();
                            for (int h =0; h < numOfAddOn;h++){
                                String addOnName = dataSnapshot.child(i+"").child("addOn").child(h+"").child("name").getValue().toString();
                                double addOnPrice = Double.parseDouble(dataSnapshot.child(i+"").child("addOn").child(h+"").child("price").getValue().toString());
                                addOnList.add(new AddOn(addOnName, addOnPrice));
                            }

                            readdedOwnerCollectionList.add(new Collection(name, price, dataTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, status, image));
                            drTO.child(i+"").removeValue();
                        }



                        for (int r =0; r<readdedOwnerCollectionList.size();r++){
                            drTO.child(Integer.toString(positionInOwner + r)).child("dateTimeOrder").setValue(readdedOwnerCollectionList.get(r).getDateTimeOrder());
                            drTO.child(Integer.toString(positionInOwner + r)).child("name").setValue(readdedOwnerCollectionList.get(r).getName());
                            drTO.child(Integer.toString(positionInOwner + r)).child("price").setValue(readdedOwnerCollectionList.get(r).getPrice());
                            drTO.child(Integer.toString(positionInOwner + r)).child("quantity").setValue(readdedOwnerCollectionList.get(r).getQuantity());
                            drTO.child(Integer.toString(positionInOwner + r)).child("stallName").setValue(readdedOwnerCollectionList.get(r).getStallName());
                            drTO.child(Integer.toString(positionInOwner + r)).child("totalPrice").setValue(readdedOwnerCollectionList.get(r).getTotalPrice());
                            drTO.child(Integer.toString(positionInOwner + r)).child("additionalNote").setValue(readdedOwnerCollectionList.get(r).getAdditionalNote() + "");
                            drTO.child(Integer.toString(positionInOwner + r)).child("lastChanges").setValue(readdedOwnerCollectionList.get(r).getLastChanges());
                            drTO.child(Integer.toString(positionInOwner + r)).child("stallId").setValue(readdedOwnerCollectionList.get(r).getStallId());
                            drTO.child(Integer.toString(positionInOwner + r)).child("foodId").setValue(readdedOwnerCollectionList.get(r).getFoodId());
                            drTO.child(Integer.toString(positionInOwner + r)).child("tId").setValue(readdedOwnerCollectionList.get(r).gettId());
                            drTO.child(Integer.toString(positionInOwner + r)).child("startTime").setValue(readdedOwnerCollectionList.get(r).getStartTime());
                            drTO.child(Integer.toString(positionInOwner + r)).child("endTime").setValue(readdedOwnerCollectionList.get(r).getEndTime());
                            drTO.child(Integer.toString(positionInOwner + r)).child("lastChangesInMin").setValue(readdedOwnerCollectionList.get(r).getLastChangesInMin());
                            drTO.child(Integer.toString(positionInOwner + r)).child("customerUID").setValue(readdedOwnerCollectionList.get(r).getCustomerUID());
                            drTO.child(Integer.toString(positionInOwner + r)).child("status").setValue(readdedOwnerCollectionList.get(r).getStatus());
                            drTO.child(Integer.toString(positionInOwner + r)).child("imageurl").setValue(readdedOwnerCollectionList.get(r).getImage());

                            drTO.child(Integer.toString(positionInOwner + r)).child("addOn").child("numOfAddOn").setValue(0);
                            for (int h = 0; h < readdedOwnerCollectionList.get(r).getAddOnList().size(); h++) {
                                drTO.child(Integer.toString(positionInOwner + r)).child("addOn").child(Integer.toString(h)).child("name").setValue(readdedOwnerCollectionList.get(r).getAddOnList().get(h).getName());
                                drTO.child(Integer.toString(positionInOwner + r)).child("addOn").child(Integer.toString(h)).child("price").setValue(readdedOwnerCollectionList.get(r).getAddOnList().get(h).getPrice());
                                drTO.child(Integer.toString(positionInOwner + r)).child("addOn").child("numOfAddOn").setValue(h + 1);
                            }
                        }

                        drTO.child("numOfOrder").setValue(positionInOwner +readdedOwnerCollectionList.size());




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
        //endregion

        CollectionOrderPage.getInstance().finish();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                Intent intent = new Intent(IndividualCollectionOrder.this, CollectionOrderPage.class);
                startActivity(intent);
                finish();
            }
        }, 500);


    }
    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //endregion


}
