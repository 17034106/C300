package sg.edu.rp.c346.c300;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.Customer;

public class IndividualEditFoodDateTimeSelection extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView btnSelectDate, btnSelectTime, tvCurrentDateTime, tvEarliestCollection, tvLastChanges;

    RelativeLayout foodDisplyConfirmEdit;

    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    String dayOfWeek="";

    int lastChanges;

    Date dateFormatSelectedByUser; // store the selected date by user in date format


    Date startTimeDate =null; // use to store the start time in date format
    Date endTimeDate = null;

    // use for finding the earliest timing to make the pre-order
    int earliestMinute;
    int earlierHour;

    // use for finding the last changes timing to make changes
    int lastChangesMinute;
    int lastChangesHour;
    String lastChangesToEdit;



    Date currentTime = Calendar.getInstance().getTime();
    Date stringCurrentDateConverted;
    Date stringSelectedUserDateConverted;


    String timePattern = "h:mm a";
    SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
    String datePattern = "dd/MM/yyyy (EEEE)";
    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);

    String customerSchool; //get the user's school
    DatabaseReference drEditConfirmFoodTO;

    int positionInOrder_Firebase; //check the position in the order(firebase)

    String dateTimeOrderString; // for the change dateTimeOrder

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_edit_food_date_time_selection);


        //region  pop up window for select date and time
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width), (int)(height*.41));
        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.gravity = Gravity.CENTER;
//        params.x = 0;
//        params.y = -20;
        params.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(params);
        //endregion


        tvCurrentDateTime = findViewById(R.id.tvCurrentDateTime);
        tvEarliestCollection = findViewById(R.id.tvEarliestOrder);
        tvLastChanges = findViewById(R.id.tvLastChanges);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);



        //region Get the value from the previous activity (intent)
        final Intent intentReceived = getIntent();
        final Collection collectionReceived = (Collection) intentReceived.getSerializableExtra("collection");
        final double deductFirebase = intentReceived.getDoubleExtra("deductFirebase",0.0); // use for changing the food budget amount in firebase

        final String foodName = collectionReceived.getName();
        final double foodPrice = collectionReceived.getPrice();
        final String stallname = collectionReceived.getStallName();
        lastChanges = collectionReceived.getLastChangesInMin();
        final int quantityValue = collectionReceived.getQuantity();
        final String additionalNote = collectionReceived.getAdditionalNote();
        final double totalPrice = collectionReceived.getTotalPrice();
        final String startTime = collectionReceived.getStartTime();
        final String endTime = collectionReceived.getEndTime();
        final int stallId = collectionReceived.getStallId();
        final int foodId = collectionReceived.getFoodId();
        final String tId = collectionReceived.gettId();



        //endregion




        TextView tvtotalPrice = findViewById(R.id.tvTotalPrice);
        tvtotalPrice.setText(String.format("$%.2f", totalPrice));

        btnSelectDate.setText(dateFormat.format(currentTime));
        btnSelectTime.setText(timeFormat.format(currentTime));


        timer(startTime, endTime); // called the method to keep updating the date and time


        //region  get the customer's school
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
        final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                customerSchool = customer.getCustomerschool();

                drEditConfirmFoodTO = FirebaseDatabase.getInstance().getReference().child("to").child("school").child(customerSchool).child("stall").child(stallId+"").child("order");
                drEditConfirmFoodTO.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numOfOrderInOwner =Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());

                        for (int i =0; i<numOfOrderInOwner;i++){
                            if (dataSnapshot.child(i+"").child("tId").getValue().toString().equals(tId)){
                                drEditConfirmFoodTO = drEditConfirmFoodTO.child(i+"");
                            }
                        }

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


        final DatabaseReference drEditConfirmFoodTC = FirebaseDatabase.getInstance().getReference().child("tc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order");


        //region check the position in the order(firebase) - Customer firebase
        drEditConfirmFoodTC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int totalNumOfOrder = Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString().trim());
                for (int i =0; i<totalNumOfOrder;i++){
                    if (dataSnapshot.child(i+"").child("tId").getValue().toString().equals(collectionReceived.gettId())){
                        positionInOrder_Firebase = i;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion







        //region Press Finish edit the confirmed order
        foodDisplyConfirmEdit = findViewById(R.id.FoodDisplyCheckOut);

        foodDisplyConfirmEdit.setEnabled(false);



        foodDisplyConfirmEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //region display popup question dialog
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //region Commit the changes for the confirmed order in customer firebase (TC)
                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("additionalNote").setValue(additionalNote);
                                Date dateTimeOrderDate = MainpageActivity.convertStringToDate(String.format("%02d/%02d/%02d %02d:%02d",dayFinal,monthFinal,yearFinal,hourFinal,minuteFinal), "dd/MM/yyyy HH:mm");
                                dateTimeOrderString = MainpageActivity.convertDateToString(dateTimeOrderDate, "dd/MM/yyyy h:mm a");
                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("dateTimeOrder").setValue(dateTimeOrderString);
                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("lastChanges").setValue(lastChangesToEdit);
                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("price").setValue(foodPrice);
                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("quantity").setValue(quantityValue);
                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("totalPrice").setValue(totalPrice);


                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("addOn").removeValue();

                                drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("addOn").child("numOfAddOn").setValue(0);


                                for (int i =0; i<IndividualEditFoodDisplay.addOnArray.size();i++) {
                                    drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("addOn").child(Integer.toString(i)).child("name").setValue(IndividualEditFoodDisplay.addOnArray.get(i).getName());
                                    drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("addOn").child(Integer.toString(i)).child("price").setValue(IndividualEditFoodDisplay.addOnArray.get(i).getPrice());
                                    drEditConfirmFoodTC.child(positionInOrder_Firebase+"").child("addOn").child("numOfAddOn").setValue(i+1);

                                }
                                //endregion


                                //region commit the changes for the confirmed order in owner firebase (TO)

                                drEditConfirmFoodTO.child("dateTimeOrder").setValue(dateTimeOrderString);
                                drEditConfirmFoodTO.child("price").setValue(foodPrice);
                                drEditConfirmFoodTO.child("quantity").setValue(quantityValue);
                                drEditConfirmFoodTO.child("totalPrice").setValue(totalPrice);
                                drEditConfirmFoodTO.child("additionalNote").setValue(additionalNote+"");
                                drEditConfirmFoodTO.child("lastChanges").setValue(lastChangesToEdit);

                                drEditConfirmFoodTO.child("addOn").removeValue();
                                drEditConfirmFoodTO.child("addOn").child("numOfAddOn").setValue(0);
                                for (int i =0; i<IndividualEditFoodDisplay.addOnArray.size();i++) {
                                    drEditConfirmFoodTO.child("addOn").child(Integer.toString(i)).child("name").setValue(IndividualEditFoodDisplay.addOnArray.get(i).getName());
                                    drEditConfirmFoodTO.child("addOn").child(Integer.toString(i)).child("price").setValue(IndividualEditFoodDisplay.addOnArray.get(i).getPrice());
                                    drEditConfirmFoodTO.child("addOn").child("numOfAddOn").setValue(i+1);

                                }
                                //endregion


                                //region change the food Budget available in firebase

                                // getting the day of the week
                                Date currentTime = Calendar.getInstance().getTime();
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(currentTime);
                                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                                int dayOfWeekInDB = dayOfWeek - 2;
                                if (dayOfWeekInDB==-1){
                                    dayOfWeekInDB =6;
                                }

                                DatabaseReference drChangeFoodBudget  = FirebaseDatabase.getInstance().getReference()
                                        .child("budget")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child("day")
                                        .child(dayOfWeekInDB+"")
                                        .child("category")
                                        .child("food")
                                        .child("left");
                                drChangeFoodBudget.setValue(deductFirebase);
                                //endregion


                                //---------------------------------------------------------------------------------------------------------
                                //region change the amount in prePayment

                                final DatabaseReference drPrePayment = FirebaseDatabase.getInstance().getReference().child("prePayment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                drPrePayment.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int numOfPrePayment = Integer.parseInt(dataSnapshot.child("numOfPrePayment").getValue().toString());
                                        for (int i =0; i<numOfPrePayment;i++){
                                            if (dataSnapshot.child(i+"").child("tID").getValue().toString().equals(collectionReceived.gettId())){

                                                drPrePayment.child(i+"").child("amount").setValue(totalPrice);


                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                //endregion







                                //region add to notification

                                final DatabaseReference drNotification = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
                                drNotification.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString());
                                        drNotification.child(numOfPreOrder+"").setValue(collectionReceived);
                                        drNotification.child(numOfPreOrder+"").child("dateTimeOrder").setValue(dateTimeOrderString);
                                        drNotification.child(numOfPreOrder+"").child("price").setValue(foodPrice);
                                        drNotification.child(numOfPreOrder+"").child("quantity").setValue(quantityValue);
                                        drNotification.child(numOfPreOrder+"").child("totalPrice").setValue(totalPrice);
                                        drNotification.child(numOfPreOrder+"").child("additionalNote").setValue(additionalNote+"");
                                        drNotification.child(numOfPreOrder+"").child("lastChanges").setValue(lastChangesToEdit);
                                        drNotification.child(numOfPreOrder+"").child("status").setValue("Edited");
                                        drNotification.child(numOfPreOrder+"").child("notificationTiming").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));


                                        drNotification.child(numOfPreOrder+"").child("addOn").removeValue();
                                        drNotification.child(numOfPreOrder+"").child("addOn").child("numOfAddOn").setValue(0);
                                        for (int i =0; i<IndividualEditFoodDisplay.addOnArray.size();i++) {
                                            drNotification.child(numOfPreOrder+"").child("addOn").child(Integer.toString(i)).child("name").setValue(IndividualEditFoodDisplay.addOnArray.get(i).getName());
                                            drNotification.child(numOfPreOrder+"").child("addOn").child(Integer.toString(i)).child("price").setValue(IndividualEditFoodDisplay.addOnArray.get(i).getPrice());
                                            drNotification.child(numOfPreOrder+"").child("addOn").child("numOfAddOn").setValue(i+1);

                                        }


                                        drNotification.child("numOfPreOrder").setValue(numOfPreOrder+1);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                //endregion



                                Toast.makeText(IndividualEditFoodDateTimeSelection.this, "Edited Successfully", Toast.LENGTH_SHORT).show();

                                IndividualEditFoodDisplay.getInstance().finish(); // finish IndividualEditFoodDisplay activity from here
                                IndividualCollectionOrder.getInstance().finish();
                                CollectionOrderPage.getInstance().finish();
                                Intent intent = new Intent(IndividualEditFoodDateTimeSelection.this, CollectionOrderPage.class);
                                startActivity(intent);
                                finish();



                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(IndividualEditFoodDateTimeSelection.this);
                builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                //endregion




            }
        });



        //endregion


        //region Display the dateTimePicker



        btnSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(IndividualEditFoodDateTimeSelection.this, IndividualEditFoodDateTimeSelection.this, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });


        btnSelectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(IndividualEditFoodDateTimeSelection.this, IndividualEditFoodDateTimeSelection.this, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });
        //endregion

    }


    //region Display the dateTimePicker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {


        SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
        Date date = new Date(year, month, dayOfMonth-1);
        dayOfWeek = simpledateformat.format(date);

        yearFinal = year;
        monthFinal = month+1;
        dayFinal = dayOfMonth;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(IndividualEditFoodDateTimeSelection.this, IndividualEditFoodDateTimeSelection.this, hour, minute, false);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {



        hourFinal = hourOfDay;
        minuteFinal = minute;

        String timeInputPattern = "h:mm";
        SimpleDateFormat timeInputFormat = new SimpleDateFormat(timeInputPattern);
        Date timeConverted=null;

        try {
            timeConverted = timeInputFormat.parse(hourOfDay+":"+minute);

        }catch (ParseException e){

        }

        btnSelectDate.setText(String.format("%02d/%02d/%02d (%s)",dayFinal,monthFinal,yearFinal,dayOfWeek));
        btnSelectTime.setText(timeFormat.format(timeConverted));
    }

    //endregion



    //region Updating the date and time

    boolean run=true; //set it to false if you want to stop the timer
    Handler mHandler = new Handler();

    public void timer(final String startTime, final String endTime) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (run) {
                    try {
                        Thread.sleep(900);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {

                                String CurrentTimeInputPattern = "HH:mm:ss";
                                SimpleDateFormat CurrentTimeInputFormat = new SimpleDateFormat(CurrentTimeInputPattern);

                                String CurrentTimePattern = "h:mm:ss a";
                                SimpleDateFormat CurrentTimeFormat = new SimpleDateFormat(CurrentTimePattern);

                                Date currentTimeConverted=null;

                                //region Current Timing
                                try {
                                    currentTimeConverted = CurrentTimeInputFormat.parse(currentTime.getHours()+":"+currentTime.getMinutes()+":"+currentTime.getSeconds());

                                }catch (ParseException e){

                                }

                                currentTime = Calendar.getInstance().getTime();
                                tvCurrentDateTime.setText(String.format("Current Timing: %02d/%02d/%d %s",currentTime.getDate(), currentTime.getMonth()+1, currentTime.getYear()+1900, CurrentTimeFormat.format(currentTimeConverted)));

                                //endregion


                                //region Earliest Collection Timing
                                String earliestTimeInputPattern = "HHmm";
                                SimpleDateFormat earliestTimeInputFormat = new SimpleDateFormat(earliestTimeInputPattern);
                                Date earliestTimeConverted = null; // use to store the earliest time to make order, in date format
                                startTimeDate =null; // use to store the start time in date format
                                endTimeDate = null; // use to store the end time in date format

                                Calendar earliestDate = Calendar.getInstance();

                                try{
                                    startTimeDate = earliestTimeInputFormat.parse(startTime);
                                    endTimeDate = earliestTimeInputFormat.parse(endTime);


                                    earliestMinute = currentTime.getMinutes()+lastChanges+1;
                                    earlierHour = currentTime.getHours();
                                    if (earliestMinute>59){ //check whether to plus one hour if the minute is more than 59
                                        earliestMinute=earliestMinute-60;
                                        earlierHour++;
                                    }

                                    if (earlierHour==endTimeDate.getHours()){ //check whether the current timing is after the last order or not. If yes, then add one day and set the earliest hour and minute to start time (minute)
                                        if (earliestMinute>endTimeDate.getMinutes()){
                                            earliestDate.add(Calendar.DATE,1);
                                            earlierHour = startTimeDate.getHours();
                                            earliestMinute =startTimeDate.getMinutes();
                                        }
                                    }
                                    else if (earlierHour>endTimeDate.getHours()){ //hour
                                        earliestDate.add(Calendar.DATE,1);
                                        earlierHour = startTimeDate.getHours();
                                        earliestMinute =startTimeDate.getMinutes();

                                    }

                                    if (currentTime.getHours()<startTimeDate.getHours()){ // check whether the current timing is before the start time, if yes, then change the earliest hour and minute to start time (hour)
                                        earlierHour=startTimeDate.getHours();
                                        earliestMinute = startTimeDate.getMinutes();
                                    }
                                    else if (currentTime.getHours()==startTimeDate.getHours()){ //minute
                                        if (currentTime.getMinutes()<startTimeDate.getMinutes()){
                                            earliestMinute = startTimeDate.getMinutes();
                                        }
                                    }


                                    earliestTimeConverted = earliestTimeInputFormat.parse(String.format("%02d%02d",earlierHour,earliestMinute));


                                }
                                catch (ParseException e){

                                }

                                tvEarliestCollection.setText(Html.fromHtml(String.format("<b>Earliest</b> Timing: %02d/%02d/%d %s",earliestDate.getTime().getDate(), earliestDate.getTime().getMonth()+1, earliestDate.getTime().getYear()+1900, timeFormat.format(earliestTimeConverted))));

                                //endregion


                                SimpleDateFormat datePattern = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                String dateSelectedByUser = String.format("%02d/%02d/%02d %d:%02d", dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal);

                                dateFormatSelectedByUser =null; // store the selected date by user in date format
                                SimpleDateFormat dateComparePattern = new SimpleDateFormat("yyyy/MM/dd");

                                stringCurrentDateConverted = MainpageActivity.convertStringToDate((currentTime.getYear()+1900)+"/"+(currentTime.getMonth()+1)+"/"+currentTime.getDate(),"yyyy/MM/dd" );

                                try {
                                    dateFormatSelectedByUser = datePattern.parse(dateSelectedByUser);

//                                    stringCurrentDateConverted = dateComparePattern.parse(currentTime.getDate()+""+(currentTime.getMonth()+1)+(currentTime.getYear()+1900));
//                                    stringSelectedUserDateConverted = dateComparePattern.parse(dateFormatSelectedByUser.getDate()+""+(dateFormatSelectedByUser.getMonth()+1)+(dateFormatSelectedByUser.getYear()+1900));

//                                    stringCurrentDateConverted = dateComparePattern.parse((currentTime.getYear()+1900)+"/"+(currentTime.getMonth()+1)+"/"+currentTime.getDate());
                                    stringSelectedUserDateConverted = dateComparePattern.parse((dateFormatSelectedByUser.getYear()+1900)+"/"+(dateFormatSelectedByUser.getMonth()+1)+"/"+dateFormatSelectedByUser.getDate());




                                }catch (ParseException e){

                                }


                                //region Check condition to enable or disable the foodDisplayCheckOut
                                if (stringCurrentDateConverted.compareTo(stringSelectedUserDateConverted)==0){
                                    if (currentTime.getHours()<endTimeDate.getHours() || (currentTime.getHours()== endTimeDate.getHours() && currentTime.getMinutes() <=endTimeDate.getMinutes())){
                                        if (dateFormatSelectedByUser.getHours()>startTimeDate.getHours() || ( dateFormatSelectedByUser.getHours()== startTimeDate.getHours() && dateFormatSelectedByUser.getMinutes()>=startTimeDate.getMinutes())){
                                            if (dateFormatSelectedByUser.getHours()<endTimeDate.getHours() || (dateFormatSelectedByUser.getHours()== endTimeDate.getHours() && dateFormatSelectedByUser.getMinutes()<= endTimeDate.getMinutes())){
                                                if (dateFormatSelectedByUser.getHours()>earlierHour || (dateFormatSelectedByUser.getHours()==earlierHour &&  dateFormatSelectedByUser.getMinutes() >= earliestMinute)) {
                                                    correctTimeSelected(true, "9999");
                                                }
                                                else{
                                                    correctTimeSelected(false,"Earliest Timing to pre-order is above");
                                                }
                                            }
                                            else{
                                                correctTimeSelected(false, "Stall is closed during that timing");
                                            }
                                        }
                                        else{
                                            correctTimeSelected(false, "Stall is closed during that timing");
                                        }
                                    }
                                    else{
                                        correctTimeSelected(false, "Earliest timing to pre-order is above");
                                    }
                                }
                                else if (stringCurrentDateConverted.compareTo(stringSelectedUserDateConverted)<0){
                                    if ((dateFormatSelectedByUser.getHours()> startTimeDate.getHours()
                                            || (dateFormatSelectedByUser.getHours()==startTimeDate.getHours()) && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours())
                                            && (dateFormatSelectedByUser.getHours() < endTimeDate.getHours()
                                            || (dateFormatSelectedByUser.getHours()== endTimeDate.getHours())&& dateFormatSelectedByUser.getMinutes() <= endTimeDate.getMinutes())){
                                        correctTimeSelected(true, "8888");
                                    }
                                    else{
                                        correctTimeSelected(false, "Stall is close during that timing");
                                    }
                                }
                                else{
                                    correctTimeSelected(false,"Unable to book past timing");
                                }


                                //endregion



                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();}

    //endregion


    // make the textview blink
    public void blink(final TextView txt) {
        if (txt.getVisibility()==View.VISIBLE){
            txt.setVisibility(View.INVISIBLE);
        }
        else{
            txt.setVisibility(View.VISIBLE);
        }
    }

    // if true, then it will allow the user to press the foodDisplayCheckOut
    public void correctTimeSelected(boolean b, String d){
        if (b== true){
            foodDisplyConfirmEdit.setEnabled(true);
            tvEarliestCollection.setVisibility(View.VISIBLE);
            tvEarliestCollection.setTextColor(Color.BLACK);
            lastChangesHour = dateFormatSelectedByUser.getHours();
            lastChangesMinute = dateFormatSelectedByUser.getMinutes();

            Log.d("What is the first", "What is lastChangeHour 1: "+lastChangesHour);
            Log.d("What is the first", "What is lastChangeMinue 1: "+lastChangesMinute);

            if (lastChangesHour<startTimeDate.getHours()){
                lastChangesHour = startTimeDate.getHours();
                lastChangesMinute = startTimeDate.getMinutes();
            }
            else if (lastChangesHour == startTimeDate.getHours()){
                if (lastChangesMinute<startTimeDate.getMinutes()){
                    lastChangesMinute = startTimeDate.getMinutes();
                }
            }

            Log.d("What is the first", "What is lastChangeHour 2: "+lastChangesHour);
            Log.d("What is the first", "What is lastChangeMinue 2: "+lastChangesMinute);


            lastChangesMinute = lastChangesMinute - lastChanges;
            if (lastChangesMinute<0){
                lastChangesHour--;
                lastChangesMinute+=60;
            }

            Log.d("What is the first", "What is lastChangeHour 3: "+lastChangesHour);
            Log.d("What is the first", "What is lastChangeMinue 3: "+lastChangesMinute);


            SimpleDateFormat timeSelectedToDatePattern = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            SimpleDateFormat timeSelectedToStringPattern = new SimpleDateFormat("h:mm a");
            String timeSelectedLastChanges =null;

            try{
                Date change = timeSelectedToDatePattern.parse(dateFormatSelectedByUser.getDate()+"/"+dateFormatSelectedByUser.getMonth()+1+"/"+ dateFormatSelectedByUser.getYear()+1900+" "+lastChangesHour+":"+lastChangesMinute);


                timeSelectedLastChanges = timeSelectedToStringPattern.format(change);
            }
            catch (ParseException e){

            }


            lastChangesToEdit = String.format("%02d/%02d/%02d %s", dateFormatSelectedByUser.getDate(), dateFormatSelectedByUser.getMonth()+1, dateFormatSelectedByUser.getYear()+1900, timeSelectedLastChanges);
            tvLastChanges.setText(Html.fromHtml(String.format("<b>Latest</b> Editable: %s",lastChangesToEdit)));
            tvLastChanges.setTextColor(Color.rgb(16,124,16));


        }
        else{
            blink(tvEarliestCollection);
            foodDisplyConfirmEdit.setEnabled(false);
            tvEarliestCollection.setTextColor(Color.RED);
            tvLastChanges.setText(d);
            tvLastChanges.setTextColor(Color.RED);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
