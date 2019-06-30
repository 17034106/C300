package sg.edu.rp.c346.c300;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Customer;

import static android.Manifest.permission.CAMERA;


public class QrCodeScannerPay extends AppCompatActivity  implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;


    String foodName;
    double foodPrice;
    int quantity;
    boolean schoolFood;
    String school;
    String stallName;
    String stallId;
    String foodId;
    double totalPrice;
    String currentDateTimeString;
    String addOnListString; //get the value from the qrcode (combined)
    String[] addOnListIndividual; // split out all the value (individual)
    final ArrayList<AddOn> addOnList = new ArrayList<>(); // get the value from the firebase and store them here
    String tId;
    String foodImage;
    String customerUID;

    String customerSchool;

    String foodOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "onCreate");


        getCustomerSchool();
        customerUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
//                Toast.makeText(getApplicationContext(), "Permission already granted", Toast.LENGTH_LONG).show();

            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QrCodeScannerPay.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(mScannerView == null) {
                    mScannerView = new ZXingScannerView(this);
                    setContentView(mScannerView);
                }
                mScannerView.setResultHandler(this);
                mScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result rawResult) {

        final String result = rawResult.getText();

        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);

        foodOrder = displayOrder(result);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms

                foodOrder = displayOrder(result);


                AlertDialog.Builder builder = new AlertDialog.Builder(QrCodeScannerPay.this);
                builder.setTitle("Order Details");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        makePayment();
                    }
                });
                builder.setNeutralButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mScannerView.resumeCameraPreview(QrCodeScannerPay.this);

                    }
                });
                builder.setMessage(foodOrder);
                builder.setCancelable(false);
                AlertDialog alert1 = builder.create();
                alert1.show();

            }
        }, 320);



    }


    private void getCustomerSchool(){

        final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                customerSchool = customer.getCustomerschool();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //region display information about the order when the user scan the QR code
    //----------------------------------------------------------------------------------------------------------------------------------
    private String displayOrder(String element){

        final String[] elementList = element.split("\\|");



        schoolFood = elementList[0].substring(0,1).equals("T") ? true : false; // if is T, means it is true which is school's food




            school = elementList[0].substring(1);
            stallId = elementList[1];
            foodId = elementList[2];
            quantity = Integer.parseInt(elementList[3]);

        if (schoolFood){

            if(elementList.length==5){
                addOnListString = elementList[4];
            }
            else{
                addOnListString="";
            }
            addOnListIndividual = addOnListString.split("\\*");



            DatabaseReference drMenu = FirebaseDatabase.getInstance().getReference("menu").child("school").child(school).child("stall").child(stallId);
            drMenu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                    foodName = dataSnapshot.child("food").child(foodId).child("name").getValue().toString();
                    foodPrice = Double.parseDouble(dataSnapshot.child("food").child(foodId).child("price").getValue().toString());
                    stallName = dataSnapshot.child("StallName").getValue().toString();
                    foodImage = dataSnapshot.child("food").child(foodId).child("imageurl").getValue().toString().trim();

                    totalPrice = foodPrice;

                    addOnList.clear();
                    if (elementList.length ==5) {
                        for (int i = 0; i < addOnListIndividual.length; i++) {
                            String addOnName = dataSnapshot.child("food").child(foodId).child("AddOn").child(addOnListIndividual[i]).child("name").getValue().toString();
                            double addOnPrice = Double.parseDouble(dataSnapshot.child("food").child(foodId).child("AddOn").child(addOnListIndividual[i]).child("price").getValue().toString());
                            addOnList.add(new AddOn(addOnName, addOnPrice));
                            totalPrice += addOnList.get(i).getPrice();
                        }
                    }
                    tId = ""+CartDisplay.randomStringValue(18)+stallId+foodId;

                    totalPrice = totalPrice*quantity;



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            String addOnString ="";

            for (int i =0; i<addOnList.size();i++){
                addOnString += String.format("%-15s: +$%.2f\n", addOnList.get(i).getName(), addOnList.get(i).getPrice());
            }

            String orderDetail = "Name: "+foodName+"\n";
            orderDetail+="--------------------------------------------------\n";
            orderDetail +=String.format("%-15s: $%.2f\n", "Price", foodPrice );
            orderDetail+="--------------------------------------------------\n";
            orderDetail+=addOnString;
            if(addOnList.size()!=0) {
                orderDetail += "--------------------------------------------------\n";
            }
            orderDetail+=String.format("%-15s: %dSet(s)\n", "Quantity", quantity);
            orderDetail+="--------------------------------------------------\n";
            orderDetail+=String.format("%-15s: $%.2f\n", "Total Price", totalPrice);
            orderDetail+="--------------------------------------------------\n";
            orderDetail+="\nAre you sure?";

            return orderDetail;
        }
        else{

            DatabaseReference drMenu = FirebaseDatabase.getInstance().getReference("menu").child("school").child(school).child("stall").child(stallId);
            drMenu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                    foodName = dataSnapshot.child("item").child(foodId).child("name").getValue().toString();
                    foodPrice = Double.parseDouble(dataSnapshot.child("item").child(foodId).child("price").getValue().toString());
                    stallName = dataSnapshot.child("StallName").getValue().toString();

                    totalPrice = foodPrice;


                    tId = ""+CartDisplay.randomStringValue(18)+stallId+foodId;

                    totalPrice = totalPrice*quantity;



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            String orderDetail = "Name: "+foodName+"\n";
            orderDetail+="--------------------------------------------------\n";
            orderDetail +=String.format("%-15s: $%.2f\n", "Price", foodPrice );
            orderDetail+="--------------------------------------------------\n";
            orderDetail+=String.format("%-15s: %d\n", "Quantity", quantity);
            orderDetail+="--------------------------------------------------\n";
            orderDetail+=String.format("%-15s: $%.2f\n", "Total Price", totalPrice);
            orderDetail+="--------------------------------------------------\n";
            orderDetail+="\nAre you sure?";

            return orderDetail;
        }


    }
    //----------------------------------------------------------------------------------------------------------------------------------
    //endregion




    //region make payment when the user okay with the order
    //----------------------------------------------------------------------------------------------------------------------------------
    public void makePayment(){

        //region send data to history customer (HC)
        //----------------------------------------------------------------------------------------------------------------------------------
        final DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("do");
        drHC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfDO = Integer.parseInt(dataSnapshot.child("numOfDO").getValue().toString());

                Date currentDateTime = Calendar.getInstance().getTime();
                currentDateTimeString = MainpageActivity.convertDateToString(currentDateTime, "dd/MM/yyyy h:mm:ss a");

                drHC.child(numOfDO+"").child("name").setValue(foodName);
                drHC.child(numOfDO+"").child("price").setValue(foodPrice);
                drHC.child(numOfDO+"").child("quantity").setValue(quantity);
                drHC.child(numOfDO+"").child("stallName").setValue(stallName);
                drHC.child(numOfDO+"").child("stallId").setValue(stallId);
                drHC.child(numOfDO+"").child("foodId").setValue(foodId);
                drHC.child(numOfDO+"").child("dateTimeOrder").setValue(currentDateTimeString);
                drHC.child(numOfDO+"").child("ISITFOOD").setValue(schoolFood);
                drHC.child(numOfDO+"").child("imageurl").setValue(foodImage);
                drHC.child(numOfDO+"").child("customerUID").setValue(customerUID);

                if (schoolFood) {
                    for (int i = 0; i < addOnList.size(); i++) {
                        drHC.child(numOfDO + "").child("addOn").child(i + "").child("name").setValue(addOnList.get(i).getName());
                        drHC.child(numOfDO + "").child("addOn").child(i + "").child("price").setValue(addOnList.get(i).getPrice());
                    }
                    drHC.child(numOfDO + "").child("addOn").child("numOfAddOn").setValue(addOnList.size());
                }

                drHC.child(numOfDO+"").child("totalPrice").setValue(totalPrice);
                drHC.child(numOfDO+"").child("tId").setValue(tId);
                drHC.child("numOfDO").setValue(numOfDO+1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion

        //region send data to history owner (HO)
        final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school").child(school).child("stall").child(stallId).child("do");
        drHO.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfDO = Integer.parseInt(dataSnapshot.child("numOfDO").getValue().toString().trim());

                drHO.child(numOfDO+"").child("name").setValue(foodName);
                drHO.child(numOfDO+"").child("price").setValue(foodPrice);
                drHO.child(numOfDO+"").child("quantity").setValue(quantity);
                drHO.child(numOfDO+"").child("stallName").setValue(stallName);
                drHO.child(numOfDO+"").child("stallId").setValue(stallId);
                drHO.child(numOfDO+"").child("foodId").setValue(foodId);
                drHO.child(numOfDO+"").child("dateTimeOrder").setValue(currentDateTimeString);
                drHO.child(numOfDO+"").child("ISITFOOD").setValue(schoolFood);
                drHO.child(numOfDO+"").child("imageurl").setValue(foodImage);
                drHO.child(numOfDO+"").child("customerUID").setValue(customerUID);

                if (schoolFood) {
                    for (int i = 0; i < addOnList.size(); i++) {
                        drHO.child(numOfDO + "").child("addOn").child(i + "").child("name").setValue(addOnList.get(i).getName());
                        drHO.child(numOfDO + "").child("addOn").child(i + "").child("price").setValue(addOnList.get(i).getPrice());
                    }
                    drHO.child(numOfDO + "").child("addOn").child("numOfAddOn").setValue(addOnList.size());
                }

                drHO.child(numOfDO+"").child("totalPrice").setValue(totalPrice);
                drHO.child(numOfDO+"").child("tId").setValue(tId);
                drHO.child("numOfDO").setValue(numOfDO+1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //endregion


        //region send data to customer notification (walk-in)
        if (schoolFood) {
            final DatabaseReference drNotificationCustomerWI = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("walkIn");
            drNotificationCustomerWI.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfWalkIn = Integer.parseInt(dataSnapshot.child("numOfWalkIn").getValue().toString().trim());

                    drNotificationCustomerWI.child(numOfWalkIn + "").child("name").setValue(foodName);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("price").setValue(foodPrice);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("quantity").setValue(quantity);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("stallName").setValue(stallName);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("stallId").setValue(stallId);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("foodId").setValue(foodId);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("dateTimeOrder").setValue(currentDateTimeString);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("ISITFOOD").setValue(schoolFood);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("imageurl").setValue(foodImage);
                    drNotificationCustomerWI.child(numOfWalkIn+"").child("customerUID").setValue(customerUID);



                    for (int i = 0; i < addOnList.size(); i++) {
                        drNotificationCustomerWI.child(numOfWalkIn + "").child("addOn").child(i + "").child("name").setValue(addOnList.get(i).getName());
                        drNotificationCustomerWI.child(numOfWalkIn + "").child("addOn").child(i + "").child("price").setValue(addOnList.get(i).getPrice());
                    }
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("addOn").child("numOfAddOn").setValue(addOnList.size());


                    drNotificationCustomerWI.child(numOfWalkIn + "").child("totalPrice").setValue(totalPrice);
                    drNotificationCustomerWI.child(numOfWalkIn + "").child("tId").setValue(tId);
                    drNotificationCustomerWI.child("numOfWalkIn").setValue(numOfWalkIn + 1);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        //endregion



        finish();

    }
    //----------------------------------------------------------------------------------------------------------------------------------
    //endregion

}
