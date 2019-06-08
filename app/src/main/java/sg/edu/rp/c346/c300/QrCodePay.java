package sg.edu.rp.c346.c300;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Customer;

public class QrCodePay extends AppCompatActivity {


    SurfaceView surfaceView;
    CameraSource cameraSource;
    TextView textView;
    BarcodeDetector barcodeDetector;
    final int RequestCameraPermissionID = 1001; // For user-permission

    int scanOnce =0; // allow the scanner to scan once

    String foodName;
    double foodPrice;
    int quantity;
    String stallName;
    String stallId;
    String foodId;
    double totalPrice;
    String currentDateTimeString;
    String addOnListString; //get the value from the qrcode (combined)
    String[] addOnListIndividual; // split out all the value (individual)
    final ArrayList<AddOn> addOnList = new ArrayList<>(); // get the value from the firebase and store them here
    String tId;

    String customerSchool;

    String foodOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_pay);

        getCustomerSchool();


        surfaceView = findViewById(R.id.qrCodeScanner);
        textView = findViewById(R.id.textView);


        barcodeDetector = new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector).setRequestedPreviewSize(1024, 768).setFacing(CameraSource.CAMERA_FACING_BACK).setRequestedFps(30.0f).build();

        //Start the scanner
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QrCodePay.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                    return;
                }
                try {
                    cameraSource.start(surfaceView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        //Detect the QR code
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes = detections.getDetectedItems();

                if (qrCodes.size() !=0 && scanOnce ==0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(1000);

                            scanOnce++;

                            foodOrder = displayOrder(qrCodes.valueAt(0).displayValue.trim());

                            //region Delay
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // Do something after 5s = 5000ms

                                    //region Display the order information and ask whether the user want to accept or not
                                    //----------------------------------------------------------------------------------------------------------------------------------
                                    foodOrder = displayOrder(qrCodes.valueAt(0).displayValue.trim());

                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            foodOrder = displayOrder(qrCodes.valueAt(0).displayValue.trim());
                                            switch (which){
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    makePayment();
                                                    scanOnce=0;
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    scanOnce = 0;
                                                    textView.setText("Please focus on QR code");
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(QrCodePay.this);
                                    builder.setMessage(foodOrder).setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();
                                    //----------------------------------------------------------------------------------------------------------------------------------
                                    //endregion

                                }
                            }, 320);
                            //endregion



                        }
                    });


                }

            }
        });


    }

    //Check the user permission. If the user agree it then the scanner will start working
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

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
        Log.d("what is elementList", "What is the elementList: "+elementList);
        Log.d("what is elementList", "What is the elementList size : "+elementList.length);
        for (int i =0; i<elementList.length;i++){
            String title = "elementList["+i+"]";
        }
        stallId = elementList[0];
        foodId = elementList[1];
        quantity = Integer.parseInt(elementList[2]);
        if(elementList.length==4){
            addOnListString = elementList[3];
        }
        else{
            addOnListString="";
        }
        addOnListIndividual = addOnListString.split("\\*");



        DatabaseReference drMenu = FirebaseDatabase.getInstance().getReference("menu").child("school").child(customerSchool).child("stall").child(stallId);
        drMenu.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                foodName = dataSnapshot.child("food").child(foodId).child("name").getValue().toString();
                foodPrice = Double.parseDouble(dataSnapshot.child("food").child(foodId).child("price").getValue().toString());
                stallName = dataSnapshot.child("StallName").getValue().toString();

                totalPrice = foodPrice;

                addOnList.clear();
                if (elementList.length ==4) {
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
        orderDetail+=String.format("%-15s: $%.2f\n", "Total Price", totalPrice);
        orderDetail+="--------------------------------------------------\n";
        orderDetail+="\nAre you sure?";

        return orderDetail;
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

                for (int i = 0; i < addOnList.size(); i++) {
                    drHC.child(numOfDO + "").child("AddOn").child(i + "").child("name").setValue(addOnList.get(i).getName());
                    drHC.child(numOfDO + "").child("AddOn").child(i + "").child("price").setValue(addOnList.get(i).getPrice());
                }
                drHC.child(numOfDO + "").child("AddOn").child("numOfAddOn").setValue(addOnList.size());

                drHC.child(numOfDO+"").child("totalPrice").setValue(totalPrice);
                drHC.child(numOfDO+"").child("tId").setValue(tId);
                drHC.child("numOfDO").setValue(numOfDO+1);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //region send data to history owner (HO)
        final DatabaseReference drHO = FirebaseDatabase.getInstance().getReference().child("ho").child("school").child(customerSchool).child("stall").child(stallId).child("do");
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

                for (int i = 0; i < addOnList.size(); i++) {
                    drHO.child(numOfDO + "").child("AddOn").child(i + "").child("name").setValue(addOnList.get(i).getName());
                    drHO.child(numOfDO + "").child("AddOn").child(i + "").child("price").setValue(addOnList.get(i).getPrice());
                }
                drHO.child(numOfDO + "").child("AddOn").child("numOfAddOn").setValue(addOnList.size());

                drHO.child(numOfDO+"").child("totalPrice").setValue(totalPrice);
                drHO.child(numOfDO+"").child("tId").setValue(tId);
                drHO.child("numOfDO").setValue(numOfDO+1);

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

}
