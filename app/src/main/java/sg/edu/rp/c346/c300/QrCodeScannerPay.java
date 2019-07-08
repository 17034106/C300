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
import sg.edu.rp.c346.c300.model.Budget;
import sg.edu.rp.c346.c300.model.Category;
import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.model.DirectOrderReceive;
import sg.edu.rp.c346.c300.model.DirectOrderSend;

import static android.Manifest.permission.CAMERA;


public class QrCodeScannerPay extends AppCompatActivity  implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView mScannerView;


//    String foodName;
//    double foodPrice;
//    int quantity;
//    boolean schoolFood;
//    String school;
//    String stallName;
//    String stallId;
//    String foodId;
//    double totalPrice;
//    String currentDateTimeString;
//    String addOnListString; //get the value from the qrcode (combined)
//    String[] addOnListIndividual; // split out all the value (individual)
//    final ArrayList<AddOn> addOnList = new ArrayList<>(); // get the value from the firebase and store them here
//    String tId;
//    String foodImage;
//    String customerUID;
//    String stallUID;
//
//    String customerSchool;
//
    String foodOrder;

    boolean roleCoffeeShop;

    String school;
    int stallId;
    String ownerUID;

    ArrayList<DirectOrderReceive> directOrderReceiveList; // retrieve all the information that the customer has chosen to buy (For role = coffee shop)

    String returnString; // the return String value to display the menu when the QR code scanner scans the QR code

    double overallTotalPrice; // find out the total price

    boolean enoughBudget; //Check whether there is enough budget or not

    Budget budget;
    double customerBalance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("onCreate", "onCreate");

        directOrderReceiveList = new ArrayList<>();

        getBalanceAndCategory();

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

                if (enoughBudget) { //if enough budget

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
                            directOrderReceiveList.clear();
                        }
                    });
                    builder.setMessage(foodOrder);
                    builder.setCancelable(false);
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
                else{ // not enough budget

                    AlertDialog.Builder builder = new AlertDialog.Builder(QrCodeScannerPay.this);
                    builder.setTitle("Error");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mScannerView.resumeCameraPreview(QrCodeScannerPay.this);
                            directOrderReceiveList.clear();
                        }
                    });

                    builder.setMessage("Insufficient Budget");
                    builder.setCancelable(false);
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }
            }
        }, 320);



    }





    //region display information about the order when the user scan the QR code
    //----------------------------------------------------------------------------------------------------------------------------------
    private String displayOrder(String element){

        overallTotalPrice=0;

        returnString =""; // the return String value

        final String[] elementList = element.split("\\|");

        school = elementList[1];
        stallId = Integer.parseInt(elementList[2]);

        if (elementList[0].equals("T")){ // the role is coffee shop
            roleCoffeeShop = true;
            Toast.makeText(this, "Coffee Shop", Toast.LENGTH_SHORT).show();

            //region retrieve all the information that the customer has chosen
            String allFood = elementList[3];
            final String[] splitFood = allFood.split(",");

            DatabaseReference drMenu = FirebaseDatabase.getInstance().getReference().child("menu").child("school").child(school).child("stall").child(stallId+"");
            drMenu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (int i =0; i<splitFood.length;i++){

                        String[] individualFood = splitFood[i].split("\\*");
                        int foodId = Integer.parseInt(individualFood[0]);
                        int quantity = Integer.parseInt(individualFood[1]);
                        String allAddOn = individualFood[2];
                        String[] individualAddOn = allAddOn.split(":");

                        String name = dataSnapshot.child("food").child(foodId+"").child("name").getValue().toString();
                        double price = Double.parseDouble(dataSnapshot.child("food").child(foodId+"").child("price").getValue().toString());
                        String stallName = dataSnapshot.child("StallName").getValue().toString();
                        String image = dataSnapshot.child("food").child(foodId+"").child("imageurl").getValue().toString();
                        String stallUID = dataSnapshot.child("stallUID").getValue().toString();
                        ownerUID = stallUID;

                        ArrayList<AddOn> addOnList = new ArrayList<>();

                        double totalPrice=price;

                        if (!individualAddOn[0].equals("-1")){
                            for (int h =0; h<individualAddOn.length;h++){
                                String addOnName = dataSnapshot.child("food").child(foodId+"").child("AddOn").child(individualAddOn[h]).child("name").getValue().toString();
                                double addOnPrice = Double.parseDouble(dataSnapshot.child("food").child(foodId+"").child("AddOn").child(individualAddOn[h]).child("price").getValue().toString());
                                addOnList.add(new AddOn(addOnName, addOnPrice));
                                totalPrice+=addOnPrice;
                            }
                        }

                        totalPrice = totalPrice * quantity;


                        directOrderReceiveList.add(new DirectOrderReceive(name, price, stallName, stallId, school, foodId, quantity, addOnList, totalPrice, image, stallUID));

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            overallTotalPrice=0;

            int count =0;
            returnString="";
            for (DirectOrderReceive i : directOrderReceiveList) {

                Log.d("Size", "what is the size of theis 1: "+directOrderReceiveList.size());

                String addOnString ="";

                for (int h =0; h<i.getAddOn().size();h++){
                    addOnString += String.format("%-15s: +$%.2f\n", i.getAddOn().get(h).getName(), i.getAddOn().get(h).getPrice());
                }

                if (count !=0) {
                    returnString += "\n";
                    returnString += "\n";
                }
                returnString += "Name: "+i.getName()+"\n";
                returnString+="--------------------------------------------------\n";
                returnString +=String.format("%-15s: $%.2f\n", "Price", i.getPrice());
                returnString+="--------------------------------------------------\n";
                returnString+=addOnString;
                if(i.getAddOn().size()!=0) {
                    returnString += "--------------------------------------------------\n";
                }
                returnString+=String.format("%-15s: %dSet(s)\n", "Quantity", i.getQuantity());
                returnString+="--------------------------------------------------\n";
                returnString+=String.format("%-15s: $%.2f\n", "Total Price", i.getTotalPrice());

                overallTotalPrice += i.getTotalPrice();
                count++;
            }
            returnString+="==================================\n";
            returnString+= String.format("Overall Total Price: %.2f\n", overallTotalPrice);
            returnString+="==================================\n";
            returnString+="\nAre you sure?";

            if(budget.getCategory().getFood().getLeft()>= overallTotalPrice){
                enoughBudget = true;
            }
            else{
                enoughBudget = false;
            }



        }
        else{   // the role is non-coffee shop
            roleCoffeeShop = false;

            Toast.makeText(this, "Non-Coffee Shop", Toast.LENGTH_SHORT).show();

        }




        return returnString;
    }
    //----------------------------------------------------------------------------------------------------------------------------------
    //endregion




    //region make payment when the user okay with the order
    //----------------------------------------------------------------------------------------------------------------------------------
    public void makePayment(){

        if (roleCoffeeShop){

            final String dateTimeOrder = MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a");


            //---------------------------------------------------------------------------------------------
            //region send data to HC
            final DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("do");
            drHC.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfDO").getValue().toString());

                    for (int i =0; i<directOrderReceiveList.size()/2;i++){
                        Log.d("Size", "what is the size of theis 2: "+directOrderReceiveList.size());

                        DirectOrderSend directOrderSend = new DirectOrderSend(directOrderReceiveList.get(i).getName(),
                                directOrderReceiveList.get(i).getPrice(),
                                directOrderReceiveList.get(i).getStallName(),
                                directOrderReceiveList.get(i).getStallID(),
                                directOrderReceiveList.get(i).getSchool(),
                                directOrderReceiveList.get(i).getFoodID(),
                                directOrderReceiveList.get(i).getQuantity(),
                                directOrderReceiveList.get(i).getAddOn(),
                                directOrderReceiveList.get(i).getTotalPrice(),
                                directOrderReceiveList.get(i).getImage(),
                                directOrderReceiveList.get(i).getStallUID(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                dateTimeOrder,
                                ""+CartDisplay.randomStringValue(18)+stallId+directOrderReceiveList.get(i).getFoodID(),
                                true);
                        drHC.child((numOfDO+i)+"").setValue(directOrderSend);
                        drHC.child((numOfDO+i)+"").child("addOn").child("numOfAddOn").setValue(directOrderReceiveList.get(i).getAddOn().size());
                    }

                    drHC.child("numOfDO").setValue(numOfDO+(directOrderReceiveList.size()/2));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //endregion


            //---------------------------------------------------------------------------------------------
            //region send data to HO


            final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school").child(school).child("stall").child(stallId+"").child("do");
            drHO.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfDO").getValue().toString());

                    for (int i =0; i<directOrderReceiveList.size()/2;i++){

                        DirectOrderSend directOrderSend = new DirectOrderSend(directOrderReceiveList.get(i).getName(),
                                directOrderReceiveList.get(i).getPrice(),
                                directOrderReceiveList.get(i).getStallName(),
                                directOrderReceiveList.get(i).getStallID(),
                                directOrderReceiveList.get(i).getSchool(),
                                directOrderReceiveList.get(i).getFoodID(),
                                directOrderReceiveList.get(i).getQuantity(),
                                directOrderReceiveList.get(i).getAddOn(),
                                directOrderReceiveList.get(i).getTotalPrice(),
                                directOrderReceiveList.get(i).getImage(),
                                directOrderReceiveList.get(i).getStallUID(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                dateTimeOrder,
                                ""+CartDisplay.randomStringValue(18)+stallId+directOrderReceiveList.get(i).getFoodID(),
                                true);
                        drHO.child((numOfDO+i)+"").setValue(directOrderSend);
                        drHO.child((numOfDO+i)+"").child("addOn").child("numOfAddOn").setValue(directOrderReceiveList.get(i).getAddOn().size());
                    }

                    drHO.child("numOfDO").setValue(numOfDO+(directOrderReceiveList.size()/2));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //endregion


            //---------------------------------------------------------------------------------------------
            //region send data to notification

            final DatabaseReference drNotification = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("walkIn");
            drNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfWalkIn").getValue().toString());

                    for (int i =0; i<directOrderReceiveList.size()/2;i++){

                        DirectOrderSend directOrderSend = new DirectOrderSend(directOrderReceiveList.get(i).getName(),
                                directOrderReceiveList.get(i).getPrice(),
                                directOrderReceiveList.get(i).getStallName(),
                                directOrderReceiveList.get(i).getStallID(),
                                directOrderReceiveList.get(i).getSchool(),
                                directOrderReceiveList.get(i).getFoodID(),
                                directOrderReceiveList.get(i).getQuantity(),
                                directOrderReceiveList.get(i).getAddOn(),
                                directOrderReceiveList.get(i).getTotalPrice(),
                                directOrderReceiveList.get(i).getImage(),
                                directOrderReceiveList.get(i).getStallUID(),
                                FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                dateTimeOrder,
                                ""+CartDisplay.randomStringValue(18)+stallId+directOrderReceiveList.get(i).getFoodID(),
                                true);
                        drNotification.child((numOfDO+i)+"").setValue(directOrderSend);
                        drNotification.child((numOfDO+i)+"").child("addOn").child("numOfAddOn").setValue(directOrderReceiveList.get(i).getAddOn().size());
                    }

                    drNotification.child("numOfWalkIn").setValue(numOfDO+(directOrderReceiveList.size()/2));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //endregion








        }
        else{





        }



        //---------------------------------------------------------------------------------------------
        //region deduct money from customer balance and Budget
        DatabaseReference drCustomerBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
        drCustomerBalance.setValue(customerBalance-overallTotalPrice);

        Calendar calendar = Calendar.getInstance();
        int day1 = calendar.get(Calendar.DAY_OF_WEEK)-2;

        if (day1==-1){
            day1 = 6;
        }

        DatabaseReference drBudgetDeduct = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day").child(day1+"");
        budget.getCategory().getFood().setLeft(budget.getCategory().getFood().getLeft()-overallTotalPrice);
        drBudgetDeduct.setValue(budget);

        //endregion


        //---------------------------------------------------------------------------------------------
        //region add money to owner

        final DatabaseReference drOwnerBalance = FirebaseDatabase.getInstance().getReference().child("Owner").child(ownerUID).child("balance");
        drOwnerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double ownerBalance = Double.parseDouble(dataSnapshot.getValue().toString().trim());
                drOwnerBalance.setValue(ownerBalance+overallTotalPrice);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //endregion









        finish();
    }
    //----------------------------------------------------------------------------------------------------------------------------------
    //endregion


    private void getBalanceAndCategory(){
        DatabaseReference drBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
        drBalance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerBalance = Double.parseDouble(dataSnapshot.getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DatabaseReference drBudget = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day");
        drBudget.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                int day1 = calendar.get(Calendar.DAY_OF_WEEK)-2;

                if (day1==-1){
                    day1 = 6;
                }

                budget = dataSnapshot.child(day1+"").getValue(Budget.class);
                Log.d("Category","Catgory))))))))): "+budget.getCategory().getFood().getLeft());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }



}
