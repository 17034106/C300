package sg.edu.rp.c346.c300;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.google.zxing.qrcode.encoder.QRCode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import sg.edu.rp.c346.c300.app.FeedMenu;
import sg.edu.rp.c346.c300.app.FoodMenu;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Budget;
import sg.edu.rp.c346.c300.model.DirectOrderReceive;
import sg.edu.rp.c346.c300.model.DirectOrderSend;
import sg.edu.rp.c346.c300.model.ItemOutside;
import sg.edu.rp.c346.c300.model.OutsideReceiveOrder;
import sg.edu.rp.c346.c300.model.OutsideSendOrder;

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


    String stallNameIntent;
    String dateTimeIntent;
    String schoolIntent;
    double totalPriceIntent;


    Map<String, Integer> outsideReceiveMap; // split out based on the FoodID
    Map<String, Double> categoryCost;

    ArrayList<OutsideReceiveOrder> categoryFoodList;
    ArrayList<OutsideReceiveOrder> categoryDrinkList;
    ArrayList<OutsideReceiveOrder> categoryStationeryList;
    ArrayList<OutsideReceiveOrder> categoryOthersList;
    ArrayList<OutsideReceiveOrder> allCategoryList;
    double totalPriceForOutside = 0;



    public static ArrayList<Integer> jurongPointDenyList = new ArrayList<>();
    public static ArrayList<Integer> republicPolytechnicDenyList = new ArrayList<>();
    public static ArrayList<Integer> singaporePolytechnicDenyList = new ArrayList<>();
    boolean allow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        directOrderReceiveList = new ArrayList<>();

        outsideReceiveMap = new Hashtable<String, Integer>();
        categoryCost = new Hashtable<String, Double>();
        categoryFoodList = new ArrayList<>();
        categoryDrinkList = new ArrayList<>();
        categoryStationeryList = new ArrayList<>();
        categoryOthersList = new ArrayList<>();
        allCategoryList = new ArrayList<>();

        getBalanceAndCategory();
        getAllDenyList();

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
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted) {
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    } else {
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
                if (mScannerView == null) {
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

                if (allow) {
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

                                categoryFoodList.clear();
                                categoryDrinkList.clear();
                                categoryStationeryList.clear();
                                categoryOthersList.clear();
                                allCategoryList.clear();
                            }
                        });
                        builder.setMessage(foodOrder);
                        builder.setCancelable(false);
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    } else { // not enough budget

                        AlertDialog.Builder builder = new AlertDialog.Builder(QrCodeScannerPay.this);
                        builder.setTitle("Error");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mScannerView.resumeCameraPreview(QrCodeScannerPay.this);
                                directOrderReceiveList.clear();

                                categoryFoodList.clear();
                                categoryDrinkList.clear();
                                categoryStationeryList.clear();
                                categoryOthersList.clear();
                                allCategoryList.clear();
                            }
                        });

                        builder.setMessage("Insufficient Budget");
                        builder.setCancelable(false);
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }

                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(QrCodeScannerPay.this);
                    builder.setTitle("Error");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mScannerView.resumeCameraPreview(QrCodeScannerPay.this);
                            directOrderReceiveList.clear();

                            categoryFoodList.clear();
                            categoryDrinkList.clear();
                            categoryStationeryList.clear();
                            categoryOthersList.clear();
                            allCategoryList.clear();
                        }
                    });

                    builder.setMessage("Deny purchase from this stall\nPlease contact your parent");
                    builder.setCancelable(false);
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                }

            }
        }, 600);


    }


    //region display information about the order when the user scan the QR code
    //----------------------------------------------------------------------------------------------------------------------------------
    private String displayOrder(String element) {

        overallTotalPrice = 0;

        returnString = ""; // the return String value

        final String[] elementList = element.split("\\|");

        school = elementList[1];
        stallId = Integer.parseInt(elementList[2]);

        allow = true; // allow to buy

        if (school.equalsIgnoreCase("Republic Polytechnic")) {
            if (republicPolytechnicDenyList.contains(stallId)) {
                allow = false;
            }
        } else if (school.equalsIgnoreCase("Singapore Polytechnic")) {
            if (singaporePolytechnicDenyList.contains(stallId)) {
                allow = false;
            }
        } else if (school.equalsIgnoreCase("Jurong Point")) {
            if (jurongPointDenyList.contains(stallId)) {
                allow = false;
            }
        }


        if(allow) { // if the kid is not deny from purchasing
            //region the role is coffee shop
            if (elementList[0].equals("T")) {
                roleCoffeeShop = true;

                //region retrieve all the information that the customer has chosen
                String allFood = elementList[3];
                final String[] splitFood = allFood.split(",");

                DatabaseReference drMenu = FirebaseDatabase.getInstance().getReference().child("menu").child("school").child(school).child("stall").child(stallId + "");
                drMenu.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (int i = 0; i < splitFood.length; i++) {

                            String[] individualFood = splitFood[i].split("\\*");
                            int foodId = Integer.parseInt(individualFood[0]);
                            int quantity = Integer.parseInt(individualFood[1]);
                            String allAddOn = individualFood[2];
                            String[] individualAddOn = allAddOn.split(":");

                            String name = dataSnapshot.child("food").child(foodId + "").child("name").getValue().toString();
                            double price = Double.parseDouble(dataSnapshot.child("food").child(foodId + "").child("price").getValue().toString());
                            String stallName = dataSnapshot.child("stallName").getValue().toString();
                            String image = dataSnapshot.child("food").child(foodId + "").child("imageurl").getValue().toString();
                            String stallUID = dataSnapshot.child("stallUID").getValue().toString();
                            ownerUID = stallUID;

                            ArrayList<AddOn> addOnList = new ArrayList<>();

                            double totalPrice = price;

                            if (!individualAddOn[0].equals("-1")) {
                                for (int h = 0; h < individualAddOn.length; h++) {
                                    String addOnName = dataSnapshot.child("food").child(foodId + "").child("AddOn").child(individualAddOn[h]).child("name").getValue().toString();
                                    double addOnPrice = Double.parseDouble(dataSnapshot.child("food").child(foodId + "").child("AddOn").child(individualAddOn[h]).child("price").getValue().toString());
                                    addOnList.add(new AddOn(addOnName, addOnPrice));
                                    totalPrice += addOnPrice;
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


                int count = 0;
                returnString = "";
                for (DirectOrderReceive i : directOrderReceiveList) {


                    String addOnString = "";

                    for (int h = 0; h < i.getAddOn().size(); h++) {
                        addOnString += String.format("%-15s: +$%.2f\n", i.getAddOn().get(h).getName(), i.getAddOn().get(h).getPrice());
                    }

                    if (count != 0) {
                        returnString += "\n";
                        returnString += "\n";
                    }
                    returnString += "Name: " + i.getName() + "\n";
                    returnString += "--------------------------------------------------\n";
                    returnString += String.format("%-15s: $%.2f\n", "Price", i.getPrice());
                    returnString += "--------------------------------------------------\n";
                    returnString += addOnString;
                    if (i.getAddOn().size() != 0) {
                        returnString += "--------------------------------------------------\n";
                    }
                    returnString += String.format("%-15s: %dSet(s)\n", "Quantity", i.getQuantity());
                    returnString += "--------------------------------------------------\n";
                    returnString += String.format("%-15s: $%.2f\n", "Total Price", i.getTotalPrice());

                    overallTotalPrice += i.getTotalPrice();
                    count++;
                }
                returnString += "==================================\n";
                returnString += String.format("Overall Total Price: %.2f\n", overallTotalPrice);
                returnString += "==================================\n";
                returnString += "\nAre you sure?";

                if (budget.getCategory().getFood().getLeft() >= overallTotalPrice) {
                    enoughBudget = true;
                } else {
                    enoughBudget = false;
                }


            }
            //endregion the role is non-coffee shop
            else {
                roleCoffeeShop = false;
                //T|school|StallID|foodID*FoodID2*FoodID

                String[] allFood = elementList[3].split("\\*");

                outsideReceiveMap.clear();
                for (String i : allFood) {
                    if (outsideReceiveMap.containsKey(i)) {
                        outsideReceiveMap.put(i, outsideReceiveMap.get(i) + 1);
                    } else {
                        outsideReceiveMap.put(i, 1);
                    }
                }


                DatabaseReference drItem = FirebaseDatabase.getInstance().getReference().child("menu").child("school").child(school).child("stall").child(stallId + "");
                drItem.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (Map.Entry<String, Integer> i : outsideReceiveMap.entrySet()) {

                            String name = dataSnapshot.child("item").child(i.getKey()).child("name").getValue().toString();
                            double price = Double.parseDouble(dataSnapshot.child("item").child(i.getKey()).child("price").getValue().toString().trim());
                            int quantity = i.getValue();
                            String barcode = dataSnapshot.child("item").child(i.getKey()).child("barcode").getValue().toString();
                            String category = dataSnapshot.child("item").child(i.getKey()).child("category").getValue().toString();
                            String image = dataSnapshot.child("item").child(i.getKey()).child("imageurl").getValue().toString();
                            String stallName = dataSnapshot.child("stallName").getValue().toString();
                            int foodID = Integer.parseInt(i.getKey());
                            String stallUID = dataSnapshot.child("stallUID").getValue().toString();
                            String customerUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            ownerUID = stallUID;

                            Log.d("What is the cat", "What is the category for the item: " + category);

                            if (category.equals("food")) {
                                categoryFoodList.add(new OutsideReceiveOrder(name, price, quantity, barcode, category, image, stallName, stallId, foodID, stallUID, customerUID, school));
                            } else if (category.equals("drink")) {
                                categoryDrinkList.add(new OutsideReceiveOrder(name, price, quantity, barcode, category, image, stallName, stallId, foodID, stallUID, customerUID, school));
                            } else if (category.equals("stationery")) {
                                categoryStationeryList.add(new OutsideReceiveOrder(name, price, quantity, barcode, category, image, stallName, stallId, foodID, stallUID, customerUID, school));
                            } else if (category.equals("others")) {
                                categoryOthersList.add(new OutsideReceiveOrder(name, price, quantity, barcode, category, image, stallName, stallId, foodID, stallUID, customerUID, school));
                            }


                            allCategoryList.add(new OutsideReceiveOrder(name, price, quantity, barcode, category, image, stallName, stallId, foodID, stallUID, customerUID, school));

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                totalPriceForOutside = 0;

                categoryCost.put("food", 0.0);
                categoryCost.put("drink", 0.0);
                categoryCost.put("stationery", 0.0);
                categoryCost.put("others", 0.0);

                returnString = "";
                if (categoryFoodList.size() != 0) {
                    returnString += "==================================\n";
                    returnString += "Food Category\n";
                    returnString += "==================================\n";
                    for (OutsideReceiveOrder i : categoryFoodList) {
                        returnString += "--------------------------------------------------\n";
                        returnString += String.format("Name: %-15s\n", i.getName());
                        returnString += String.format("Price: $%.2f\n", i.getPrice());
                        returnString += String.format("Quantity: %d\n", i.getQuantity());
                        totalPriceForOutside += i.getPrice() * i.getQuantity();
                        categoryCost.put("food", categoryCost.get("food") + (i.getPrice() * i.getQuantity()));

                    }
                }

                if (categoryDrinkList.size() != 0) {
                    returnString += "==================================\n";
                    returnString += "Drink Category\n";
                    returnString += "==================================\n";
                    for (OutsideReceiveOrder i : categoryDrinkList) {
                        returnString += "--------------------------------------------------\n";
                        returnString += String.format("Name: %-15s\n", i.getName());
                        returnString += String.format("Price: $%.2f\n", i.getPrice());
                        returnString += String.format("Quantity: %d\n", i.getQuantity());
                        totalPriceForOutside += i.getPrice() * i.getQuantity();
                        categoryCost.put("drink", categoryCost.get("drink") + (i.getPrice() * i.getQuantity()));

                    }
                }


                if (categoryStationeryList.size() != 0) {
                    returnString += "==================================\n";
                    returnString += "Stationery Category\n";
                    returnString += "==================================\n";
                    for (OutsideReceiveOrder i : categoryStationeryList) {
                        returnString += "--------------------------------------------------\n";
                        returnString += String.format("Name: %-15s\n", i.getName());
                        returnString += String.format("Price: $%.2f\n", i.getPrice());
                        returnString += String.format("Quantity: %d\n", i.getQuantity());
                        totalPriceForOutside += i.getPrice() * i.getQuantity();
                        categoryCost.put("stationery", categoryCost.get("stationery") + (i.getPrice() * i.getQuantity()));

                    }
                }


                if (categoryOthersList.size() != 0) {
                    returnString += "==================================\n";
                    returnString += "Others Category\n";
                    returnString += "==================================\n";
                    for (OutsideReceiveOrder i : categoryOthersList) {
                        returnString += "--------------------------------------------------\n";
                        returnString += String.format("Name: %-15s\n", i.getName());
                        returnString += String.format("Price: $%.2f\n", i.getPrice());
                        returnString += String.format("Quantity: %d\n", i.getQuantity());
                        totalPriceForOutside += i.getPrice() * i.getQuantity();
                        categoryCost.put("others", categoryCost.get("others") + (i.getPrice() * i.getQuantity()));

                    }
                }


                returnString += "==================================\n";
                returnString += String.format("Overall Total Price: $%.2f\n", totalPriceForOutside);
                returnString += "==================================\n";
                returnString += "\nAre you sure?";


                if (budget.getCategory().getFood().getLeft() >= categoryCost.get("food") && budget.getCategory().getDrink().getLeft() >= categoryCost.get("drink") && budget.getCategory().getStationery().getLeft() >= categoryCost.get("stationery") && budget.getCategory().getOthers().getLeft() >= categoryCost.get("others")) {
                    enoughBudget = true;
                } else {
                    enoughBudget = false;
                }


            }

        }


        return returnString;
    }
    //----------------------------------------------------------------------------------------------------------------------------------
    //endregion


    //region make payment when the user okay with the order
    //----------------------------------------------------------------------------------------------------------------------------------
    public void makePayment() {

        final String dateTimeOrder = MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a");


        if (roleCoffeeShop) {

            final ArrayList<String> allTID = new ArrayList<>();
            for (int i = 0; i < directOrderReceiveList.size(); i++) {
                allTID.add("" + CartDisplay.randomStringValue(18) + stallId + directOrderReceiveList.get(i).getFoodID());
            }


            //---------------------------------------------------------------------------------------------
            //region send data to HC
            final DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("walkIn");
            drHC.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfWalkIn").getValue().toString());

                    for (int i = 0; i < directOrderReceiveList.size() / 2; i++) {

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
                                allTID.get(i),
                                true);
                        drHC.child((numOfDO + i) + "").setValue(directOrderSend);
                        drHC.child((numOfDO + i) + "").child("addOn").child("numOfAddOn").setValue(directOrderReceiveList.get(i).getAddOn().size());
                    }

                    drHC.child("numOfWalkIn").setValue(numOfDO + (directOrderReceiveList.size() / 2));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //endregion


            //---------------------------------------------------------------------------------------------
            //region send data to HO


            final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school").child(school).child("stall").child(stallId + "").child("do");
            drHO.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfDO").getValue().toString());

                    for (int i = 0; i < directOrderReceiveList.size() / 2; i++) {

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
                                allTID.get(i),
                                true);
                        drHO.child((numOfDO + i) + "").setValue(directOrderSend);
                        drHO.child((numOfDO + i) + "").child("addOn").child("numOfAddOn").setValue(directOrderReceiveList.get(i).getAddOn().size());
                    }

                    drHO.child("numOfDO").setValue(numOfDO + (directOrderReceiveList.size() / 2));

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

                    for (int i = 0; i < directOrderReceiveList.size() / 2; i++) {

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
                                allTID.get(i),
                                true);
                        drNotification.child((numOfDO + i) + "").setValue(directOrderSend);
                        drNotification.child((numOfDO + i) + "").child("addOn").child("numOfAddOn").setValue(directOrderReceiveList.get(i).getAddOn().size());
                    }

                    drNotification.child("numOfWalkIn").setValue(numOfDO + (directOrderReceiveList.size() / 2));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //endregion


            //---------------------------------------------------------------------------------------------
            //region deduct money from customer balance and Budget
            DatabaseReference drCustomerBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
            drCustomerBalance.setValue(customerBalance - overallTotalPrice);

            Calendar calendar = Calendar.getInstance();
            int day1 = calendar.get(Calendar.DAY_OF_WEEK) - 2;

            if (day1 == -1) {
                day1 = 6;
            }


            DatabaseReference drBudgetDeduct = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day").child(day1 + "");
            budget.getCategory().getFood().setLeft(budget.getCategory().getFood().getLeft() - overallTotalPrice);
            drBudgetDeduct.setValue(budget);

            //endregion


            //---------------------------------------------------------------------------------------------
            //region add money to owner

            final DatabaseReference drOwnerBalance = FirebaseDatabase.getInstance().getReference().child("Owner").child(ownerUID).child("balance");
            drOwnerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double ownerBalance = Double.parseDouble(dataSnapshot.getValue().toString().trim());
                    drOwnerBalance.setValue(ownerBalance + overallTotalPrice);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //endregion

            stallNameIntent = directOrderReceiveList.get(0).getStallName();
            dateTimeIntent = dateTimeOrder;
            schoolIntent = directOrderReceiveList.get(0).getSchool();
            totalPriceIntent = overallTotalPrice;


        } else {

            ArrayList<ItemOutside> allItem = new ArrayList<>();
            for (int i = 0; i < allCategoryList.size() / 2; i++) {
                allItem.add(new ItemOutside(allCategoryList.get(i).getName(),
                        allCategoryList.get(i).getPrice(),
                        allCategoryList.get(i).getQuantity(),
                        allCategoryList.get(i).getFoodID(),
                        allCategoryList.get(i).getBarcode(),
                        allCategoryList.get(i).getCategory(),
                        allCategoryList.get(i).getImage()));

            }


            final OutsideSendOrder outsideSendOrder = new OutsideSendOrder(allItem,
                    allCategoryList.get(0).getStallName(),
                    allCategoryList.get(0).getStallID(),
                    allCategoryList.get(0).getStallUID(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    school,
                    "" + CartDisplay.randomStringValue(18) + stallId,
                    MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"),
                    totalPriceForOutside,
                    false);


            //---------------------------------------------------------------------------------------------
            //region send data to HC
            final DatabaseReference drHC = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("outside");
            drHC.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfOutside").getValue().toString());

                    drHC.child(numOfDO + "").setValue(outsideSendOrder);
                    drHC.child(numOfDO + "").child("item").child("numOfItem").setValue(outsideSendOrder.getItem().size());

                    drHC.child("numOfOutside").setValue(numOfDO + 1);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //endregion


            //---------------------------------------------------------------------------------------------
            //region send data to HO
            final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school").child(school).child("stall").child(stallId + "").child("do");
            drHO.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfDO").getValue().toString());

                    drHO.child(numOfDO + "").setValue(outsideSendOrder);
                    drHO.child(numOfDO + "").child("item").child("numOfItem").setValue(outsideSendOrder.getItem().size());

                    drHO.child("numOfDO").setValue(numOfDO + 1);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //endregion


            //---------------------------------------------------------------------------------------------
            //region send data to HC
            final DatabaseReference drNotification = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("outside");
            drNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int numOfDO = Integer.parseInt(dataSnapshot.child("numOfOutside").getValue().toString());

                    drNotification.child(numOfDO + "").setValue(outsideSendOrder);
                    drNotification.child(numOfDO + "").child("item").child("numOfItem").setValue(outsideSendOrder.getItem().size());

                    drNotification.child("numOfOutside").setValue(numOfDO + 1);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            //endregion


            //---------------------------------------------------------------------------------------------
            //region deduct money from customer balance and Budget
            DatabaseReference drCustomerBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("balance");
            drCustomerBalance.setValue(Double.parseDouble(String.format("%.2f", customerBalance - totalPriceForOutside)));

            Calendar calendar = Calendar.getInstance();
            int day1 = calendar.get(Calendar.DAY_OF_WEEK) - 2;

            if (day1 == -1) {
                day1 = 6;
            }


            DatabaseReference drBudgetDeduct = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day").child(day1 + "");
            budget.getCategory().getFood().setLeft(budget.getCategory().getFood().getLeft() - categoryCost.get("food"));
            budget.getCategory().getDrink().setLeft(budget.getCategory().getDrink().getLeft() - categoryCost.get("drink"));
            budget.getCategory().getStationery().setLeft(budget.getCategory().getStationery().getLeft() - categoryCost.get("stationery"));
            budget.getCategory().getOthers().setLeft(budget.getCategory().getOthers().getLeft() - categoryCost.get("others"));
            drBudgetDeduct.setValue(budget);

            //endregion


            //---------------------------------------------------------------------------------------------
            //region add money to owner

            final DatabaseReference drOwnerBalance = FirebaseDatabase.getInstance().getReference().child("Owner").child(ownerUID).child("balance");
            drOwnerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    double ownerBalance = Double.parseDouble(dataSnapshot.getValue().toString().trim());
                    drOwnerBalance.setValue(ownerBalance + totalPriceForOutside);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            //endregion


            stallNameIntent = outsideSendOrder.getStallName();
            dateTimeIntent = outsideSendOrder.getDateTimeOrder();
            schoolIntent = outsideSendOrder.getSchool();
            totalPriceIntent = totalPriceForOutside;


        }



        Intent intent = new Intent(QrCodeScannerPay.this, SuccessfullyQRPurchased.class);
        intent.putExtra("stallName", stallNameIntent);
        intent.putExtra("dateTimeOrder", dateTimeIntent);
        intent.putExtra("school", schoolIntent);
        intent.putExtra("totalPrice", totalPriceIntent);
        startActivity(intent);

        finish();
    }
    //----------------------------------------------------------------------------------------------------------------------------------
    //endregion


    private void getBalanceAndCategory() {
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
                int day1 = calendar.get(Calendar.DAY_OF_WEEK) - 2;

                if (day1 == -1) {
                    day1 = 6;
                }

                budget = dataSnapshot.child(day1 + "").getValue(Budget.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    //region get all the deny list
    public void getAllDenyList(){
        DatabaseReference drDenyList = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school");
        drDenyList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jurongPointDenyList.clear();
                republicPolytechnicDenyList.clear();
                singaporePolytechnicDenyList.clear();
                for (DataSnapshot i : dataSnapshot.getChildren()){
                    if (i.getKey().equals("Jurong Point")){
                        int numOfDeny = Integer.parseInt(i.child("numOfDeny").getValue().toString());
                        for (int h =0; h<numOfDeny;h++){
                            jurongPointDenyList.add(Integer.parseInt(i.child(h+"").getValue().toString()));

                        }
                    }
                    else if (i.getKey().equals("Republic Polytechnic")){
                        int numOfDeny = Integer.parseInt(i.child("numOfDeny").getValue().toString());
                        for (int h =0; h<numOfDeny;h++){
                            republicPolytechnicDenyList.add(Integer.parseInt(i.child(h+"").getValue().toString()));

                        }
                    }
                    else if (i.getKey().equals("Singapore Polytechnic")){
                        int numOfDeny = Integer.parseInt(i.child("numOfDeny").getValue().toString());
                        for (int h =0; h<numOfDeny;h++){
                            singaporePolytechnicDenyList.add(Integer.parseInt(i.child(h+"").getValue().toString()));

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //endregion

}
