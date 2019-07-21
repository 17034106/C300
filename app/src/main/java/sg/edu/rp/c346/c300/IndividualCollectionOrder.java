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
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sg.edu.rp.c346.c300.adapter.Collection2Adapter;
import sg.edu.rp.c346.c300.adapter.CollectionAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.Customer;

public class IndividualCollectionOrder extends AppCompatActivity {


    TextView tvTID, tvFoodName, tvFoodPrice, tvStallName, tvFoodStallOperation, tvLastChange, tvQuantity, tvAddOn, tvAdditionalNotes, tvSchool;
    ArrayList<AddOn> addOnListIndividual;
    ImageView ivimage;

    static IndividualCollectionOrder thisAcitivy;


    int positionInCustomer; //finding the position of the confirmed order in customer firebase (TC)
    int positionInOwner; //finding the position of the confirmed order in Owner firebase (TO)

    DatabaseReference drTO;
    String customerSchool;

    Collection collection;

    String name;
    double price;
    String dateTimeOrder;
    int quantity;
    String stallName;
    int stallId;
    int foodId;
    double totalPrice;
    String additionalNote;
    String lastChanges;
    int lastChangesInMin;
    String tId;
    String startTime;
    String endTime;
    String customerUID;
    String stallUID;
    String status;
    String image;
    String school;





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
        tvSchool = findViewById(R.id.tvIndividualSchool);



        final Intent intentReceive = getIntent();

        name = intentReceive.getStringExtra("foodName");
        price = intentReceive.getDoubleExtra("foodPrice", -0.0);
        dateTimeOrder = intentReceive.getStringExtra("dateTimeOrder");
        quantity = intentReceive.getIntExtra("quantity",-1);
        stallName = intentReceive.getStringExtra("stallName");
        stallId = intentReceive.getIntExtra("stallId",-1);
        foodId = intentReceive.getIntExtra("foodId",-1);
        totalPrice = intentReceive.getDoubleExtra("totalPrice",-1);
        additionalNote = intentReceive.getStringExtra("additionalNote");
        lastChanges = intentReceive.getStringExtra("lastChanges");
        lastChangesInMin = intentReceive.getIntExtra("lastChangesInMin",-1);
        tId = intentReceive.getStringExtra("tId");
        startTime = intentReceive.getStringExtra("startTime");
        endTime = intentReceive.getStringExtra("endTime");
        status = intentReceive.getStringExtra("status");
        image = intentReceive.getStringExtra("image");
        customerUID = intentReceive.getStringExtra("customerUID");
        stallUID = intentReceive.getStringExtra("stallUID");
        school = intentReceive.getStringExtra("school");
        addOnListIndividual = Collection2Adapter.addOnListIndividual;

        collection = new Collection(name, price, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnListIndividual, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status, image, school);




        tvTID.setText(tId);
        tvFoodName.setText(name);
        tvFoodPrice.setText(String.format("$%.2f", price));
        tvStallName.setText(String.format("Stall: %s",stallName));
        Glide.with(IndividualCollectionOrder.this).load(image).centerCrop().into(ivimage);
        tvSchool.setText(school);

        Date stallStartOperationDate = MainpageActivity.convertStringToDate(startTime, "HHmm");
        Date stallEndOperationDate = MainpageActivity.convertStringToDate(endTime, "HHmm");

        tvFoodStallOperation.setText(String.format("Working from %s to %s", MainpageActivity.convertDateToString(stallStartOperationDate, "hh:mm a"),MainpageActivity.convertDateToString(stallEndOperationDate, "hh:mm a") ));
        String lastChangeDisplay = "Changes can only be made before <b>"+intentReceive.getStringExtra("lastChanges")+"</b>";
        tvLastChange.setText(Html.fromHtml(lastChangeDisplay));
        tvQuantity.setText(String.format("x%d", quantity));
        if (additionalNote.isEmpty()){
            tvAdditionalNotes.setText("No Additional Notes");
        }
        else{
            tvAdditionalNotes.setText(additionalNote);
        }


        addOnListIndividual = Collection2Adapter.addOnListIndividual;
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

        //region Receive the order and send the data to HO and HC
        findViewById(R.id.IndividualConfirmReceiveOrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:



                                //---------------------------------------------------------------------------------------------------------
                                //region deduct money from the customer
                                    final DatabaseReference drCustomerBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
                                    drCustomerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            double balance = Double.parseDouble(dataSnapshot.getValue().toString().trim());
                                            drCustomerBalance.setValue(balance-collection.getTotalPrice());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                //endregion



                                //---------------------------------------------------------------------------------------------------------
                                //region add money to the owner
                                final DatabaseReference drOwnerBalance = FirebaseDatabase.getInstance().getReference().child("Owner").child(collection.getStallUID()).child("balance");
                                drOwnerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        double balance = Double.parseDouble(dataSnapshot.getValue().toString().trim());
                                        drOwnerBalance.setValue(balance+collection.getTotalPrice());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                //endregion




                                //---------------------------------------------------------------------------------------------------------
                                //region Remove prePayment and add to postPayment

                                final DatabaseReference drPrePayment = FirebaseDatabase.getInstance().getReference().child("prePayment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                drPrePayment.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int numOfPrePayment = Integer.parseInt(dataSnapshot.child("numOfPrePayment").getValue().toString());
                                        for (int i =0; i<numOfPrePayment;i++){
                                            if (dataSnapshot.child(i+"").child("tID").getValue().toString().equals(collection.gettId())){

                                                final double amount = Double.parseDouble(dataSnapshot.child(i+"").child("amount").getValue().toString());
                                                final String customerUID = dataSnapshot.child(i+"").child("customerUID").getValue().toString();
                                                final String paymentDateTime = dataSnapshot.child(i+"").child("paymentDateTime").getValue().toString();
                                                final String school = dataSnapshot.child(i+"").child("school").getValue().toString();
                                                final String stallUID = dataSnapshot.child(i+"").child("stallUID").getValue().toString();
                                                final String tId = dataSnapshot.child(i+"").child("tID").getValue().toString();


                                                //region add to postPayment
                                                final DatabaseReference drPostPayment = FirebaseDatabase.getInstance().getReference().child("postPayment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                drPostPayment.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        int numOfPostPayment = Integer.parseInt(dataSnapshot.child("numOfPostPayment").getValue().toString().trim());
                                                        drPostPayment.child(numOfPostPayment+"").child("amount").setValue(amount);
                                                        drPostPayment.child(numOfPostPayment+"").child("customerUID").setValue(customerUID);
                                                        drPostPayment.child(numOfPostPayment+"").child("paymentDateTime").setValue(paymentDateTime);
                                                        drPostPayment.child(numOfPostPayment+"").child("school").setValue(school);
                                                        drPostPayment.child(numOfPostPayment+"").child("stallUID").setValue(stallUID);
                                                        drPostPayment.child(numOfPostPayment+"").child("tID").setValue(tId);
                                                        drPostPayment.child(numOfPostPayment+"").child("paymentMadeDateTime").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));

                                                        drPostPayment.child("numOfPostPayment").setValue(numOfPostPayment+1);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });
                                                //endregion


                                                //region re-add the prePayment
                                                for (int h = i+1;h<numOfPrePayment;h++){
                                                    double amountReadded = Double.parseDouble(dataSnapshot.child(h+"").child("amount").getValue().toString());
                                                    String customerUIDReadded = dataSnapshot.child(h+"").child("customerUID").getValue().toString();
                                                    String paymentDateTimeReadded = dataSnapshot.child(h+"").child("paymentDateTime").getValue().toString();
                                                    String schoolReadded = dataSnapshot.child(h+"").child("school").getValue().toString();
                                                    String stallUIDReadded = dataSnapshot.child(h+"").child("stallUID").getValue().toString();
                                                    String tIdReadded = dataSnapshot.child(h+"").child("tID").getValue().toString();


                                                    drPrePayment.child((h-1)+"").child("amount").setValue(amountReadded);
                                                    drPrePayment.child((h-1)+"").child("customerUID").setValue(customerUIDReadded);
                                                    drPrePayment.child((h-1)+"").child("paymentDateTime").setValue(paymentDateTimeReadded);
                                                    drPrePayment.child((h-1)+"").child("school").setValue(schoolReadded);
                                                    drPrePayment.child((h-1)+"").child("stallUID").setValue(stallUIDReadded);
                                                    drPrePayment.child((h-1)+"").child("tID").setValue(tIdReadded);

                                                }

                                                drPrePayment.child((numOfPrePayment-1)+"").removeValue();
                                                drPrePayment.child("numOfPrePayment").setValue(numOfPrePayment-1);
                                                //endregion


                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                //endregion


                                //---------------------------------------------------------------------------------------
                                //region update the owner graph


                                //endregion


                                //---------------------------------------------------------------------------------------
                                //region update the customer graph

                                //endregion



                                //---------------------------------------------------------------------------------------------------------
                                //region add to HC

                                final DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
                                drHC.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString().trim());

                                        collection.setStatus("completed");


                                        drHC.child(numOfPreOrder+"").setValue(collection);
                                        drHC.child(numOfPreOrder+"").child("dateTimePurchased").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));
                                        drHC.child(numOfPreOrder+"").child("addOn").child("numOfAddOn").setValue(collection.getAddOn().size());
                                        drHC.child("numOfPreOrder").setValue(numOfPreOrder+1);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                //endregion





                                //---------------------------------------------------------------------------------------------------------
                                //region add to HO

                                final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school").child(collection.getSchool()).child("stall").child(collection.getStallId()+"").child("preOrder");
                                drHO.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString().trim());

                                        collection.setStatus("completed");

                                        drHO.child(numOfPreOrder+"").setValue(collection);
                                        drHO.child(numOfPreOrder+"").child("dateTimePurchased").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));

                                        drHO.child(numOfPreOrder+"").child("addOn").child("numOfAddOn").setValue(collection.getAddOn().size());

                                        drHO.child("numOfPreOrder").setValue(numOfPreOrder+1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                //endregion





                                //---------------------------------------------------------------------------------------------------------
                                //region add to notification

                                final DatabaseReference drNotification = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
                                drNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString().trim());

                                        collection.setStatus("completed");
                                        drNotification.child(numOfPreOrder+"").setValue(collection);
                                        drNotification.child(numOfPreOrder+"").child("dateTimePurchased").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));
                                        drNotification.child(numOfPreOrder+"").child("addOn").child("numOfAddOn").setValue(collection.getAddOn().size());
                                        drNotification.child(numOfPreOrder+"").child("notificationTiming").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));

                                        drNotification.child("numOfPreOrder").setValue(numOfPreOrder+1);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });



                                //endregion








                                deleteOrder();



                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(IndividualCollectionOrder.this);
                builder.setMessage("Did you receive your order?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });

        //endregion


        //region intent to IndividualEditFoodDisplay to change the confirmed order
        findViewById(R.id.IndividualEditOrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (intentReceive.getStringExtra("status").equals("purchased")) {

                    Intent intent = new Intent(IndividualCollectionOrder.this, IndividualEditFoodDisplay.class);
//                    intent.putExtra("tId", tId);
//                    intent.putExtra("foodName", name);
//                    intent.putExtra("foodPrice", price);
//                    intent.putExtra("stallName", stallName);
//                    intent.putExtra("stallId", intentReceive.getIntExtra("stallId", 0));
//                    intent.putExtra("startTime", intentReceive.getStringExtra("startTime"));
//                    intent.putExtra("endTime", intentReceive.getStringExtra("endTime"));
//                    intent.putExtra("quantity", intentReceive.getIntExtra("quantity", 0));
//                    intent.putExtra("additionalNote", intentReceive.getStringExtra("additionalNote"));
//                    intent.putExtra("status", intentReceive.getStringExtra("status"));
//                    intent.putExtra("image", intentReceive.getStringExtra("image"));
//                    intent.putExtra("lastChangesInMin", intentReceive.getIntExtra("lastChangesInMin", -1));
                    intent.putExtra("collection", collection);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(IndividualCollectionOrder.this, "Unable to edit the food", Toast.LENGTH_SHORT).show();
                }
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
                    String stallUID = dataSnapshot.child(i+"").child("stallUID").getValue().toString();
                    String status = dataSnapshot.child(i+"").child("status").getValue().toString();
                    String image = dataSnapshot.child(i+"").child("imageurl").getValue().toString();
                    String school = dataSnapshot.child(i+"").child("school").getValue().toString();

                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(i+"").child("addOn").child("numOfAddOn").getValue().toString());

                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    for (int h =0; h < numOfAddOn;h++){
                        String addOnName = dataSnapshot.child(i+"").child("addOn").child(h+"").child("name").getValue().toString();
                        double addOnPrice = Double.parseDouble(dataSnapshot.child(i+"").child("addOn").child(h+"").child("price").getValue().toString());
                        addOnList.add(new AddOn(addOnName, addOnPrice));
                    }

                    collectionList.add(new Collection(name, price, dataTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status,image, school));

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
                    drTC.child(Integer.toString(positionInCustomer + i)).child("stallUID").setValue(readdedCollectionList.get(i).getStallUID());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("status").setValue(readdedCollectionList.get(i).getStatus());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("imageurl").setValue(readdedCollectionList.get(i).getImage());
                    drTC.child(Integer.toString(positionInCustomer + i)).child("school").setValue(readdedCollectionList.get(i).getSchool());

                    drTC.child(Integer.toString(positionInCustomer + i)).child("addOn").child("numOfAddOn").setValue(0);
                    for (int h = 0; h < readdedCollectionList.get(i).getAddOn().size(); h++) {
                        drTC.child(Integer.toString(positionInCustomer + i)).child("addOn").child(Integer.toString(h)).child("name").setValue(readdedCollectionList.get(i).getAddOn().get(h).getName());
                        drTC.child(Integer.toString(positionInCustomer + i)).child("addOn").child(Integer.toString(h)).child("price").setValue(readdedCollectionList.get(i).getAddOn().get(h).getPrice());
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
                            String stallUID = dataSnapshot.child(i+"").child("stallUID").getValue().toString();
                            String status = dataSnapshot.child(i+"").child("status").getValue().toString();
                            String image = dataSnapshot.child(i+"").child("imageurl").getValue().toString();
                            String school = dataSnapshot.child(i+"").child("school").getValue().toString();

                            int numOfAddOn = Integer.parseInt(dataSnapshot.child(i+"").child("addOn").child("numOfAddOn").getValue().toString());

                            ArrayList<AddOn> addOnList = new ArrayList<>();
                            for (int h =0; h < numOfAddOn;h++){
                                String addOnName = dataSnapshot.child(i+"").child("addOn").child(h+"").child("name").getValue().toString();
                                double addOnPrice = Double.parseDouble(dataSnapshot.child(i+"").child("addOn").child(h+"").child("price").getValue().toString());
                                addOnList.add(new AddOn(addOnName, addOnPrice));
                            }

                            readdedOwnerCollectionList.add(new Collection(name, price, dataTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, additionalNote, lastChanges, lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status, image, school));
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
                            drTO.child(Integer.toString(positionInOwner + r)).child("stallUID").setValue(readdedOwnerCollectionList.get(r).getStallUID());
                            drTO.child(Integer.toString(positionInOwner + r)).child("status").setValue(readdedOwnerCollectionList.get(r).getStatus());
                            drTO.child(Integer.toString(positionInOwner + r)).child("imageurl").setValue(readdedOwnerCollectionList.get(r).getImage());
                            drTO.child(Integer.toString(positionInOwner + r)).child("school").setValue(readdedOwnerCollectionList.get(r).getSchool());

                            drTO.child(Integer.toString(positionInOwner + r)).child("addOn").child("numOfAddOn").setValue(0);
                            for (int h = 0; h < readdedOwnerCollectionList.get(r).getAddOn().size(); h++) {
                                drTO.child(Integer.toString(positionInOwner + r)).child("addOn").child(Integer.toString(h)).child("name").setValue(readdedOwnerCollectionList.get(r).getAddOn().get(h).getName());
                                drTO.child(Integer.toString(positionInOwner + r)).child("addOn").child(Integer.toString(h)).child("price").setValue(readdedOwnerCollectionList.get(r).getAddOn().get(h).getPrice());
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
