package sg.edu.rp.c346.c300;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.TimeZoneFormat;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;

public class dateTimeSelection extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView btnSelectDate, btnSelectTime, tvCurrentDateTime, tvEarliestCollection, tvLastChanges;

    RelativeLayout foodDisplyCheckOut;

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

    DatabaseReference databaseReferenceGettingNumOfCartFood;
    int numOfCartFood=-0;

    Date currentTime = Calendar.getInstance().getTime();
    Date stringCurrentDateConverted;
    Date stringSelectedUserDateConverted;


    String timePattern = "h:mm a";
    SimpleDateFormat timeFormat = new SimpleDateFormat(timePattern);
    String datePattern = "dd/MM/yyyy (EEEE)";
    SimpleDateFormat dateFormat = new SimpleDateFormat(datePattern);




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_selection);


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
        Intent intentReceive = getIntent();
        final String foodName = intentReceive.getStringExtra("foodName");
        final double foodPrice = intentReceive.getDoubleExtra("foodPrice",0);
        final String stallname = intentReceive.getStringExtra("stallName");
        lastChanges = intentReceive.getIntExtra("lastChanges", 0);
        final int quantityValue = intentReceive.getIntExtra("quantity", 0);
        final String additionalNote = intentReceive.getStringExtra("additionalNote");
        final double totalPrice = intentReceive.getDoubleExtra("totalPrice",0);
        final String startTime = intentReceive.getStringExtra("startTime");
        final String endTime = intentReceive.getStringExtra("endTime");
        final int stallId = intentReceive.getIntExtra("stallId",-1);
        final int foodId = intentReceive.getIntExtra("foodId", -1);
        final String image = intentReceive.getStringExtra("image");

        final ArrayList<AddOn> addOnArrayList =Food_display.addOnArray;

        //endregion




        TextView tvtotalPrice = findViewById(R.id.tvTotalPrice);
        tvtotalPrice.setText(String.format("$%.2f", totalPrice));

        btnSelectDate.setText(dateFormat.format(currentTime));
        btnSelectTime.setText(timeFormat.format(currentTime));


        timer(startTime, endTime); // called the method to keep updating the date and time

        //region get the number of cart food so that i can know the index of the new added cart food
        databaseReferenceGettingNumOfCartFood = FirebaseDatabase.getInstance().getReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("numOfCartFood");
        databaseReferenceGettingNumOfCartFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numOfCartFood = Integer.parseInt(dataSnapshot.getValue().toString());
                Log.d("----------------","======================="+numOfCartFood);
                Log.d("----------------","======================="+dataSnapshot);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion


        final DatabaseReference databaseReferenceAddFoodCart = FirebaseDatabase.getInstance().getReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        //region Press addToCartButton
        foodDisplyCheckOut = findViewById(R.id.FoodDisplyCheckOut);

        foodDisplyCheckOut.setEnabled(false);



        foodDisplyCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Date dateTimeOrderDate = MainpageActivity.convertStringToDate(String.format("%02d/%02d/%02d %02d:%02d",dayFinal,monthFinal,yearFinal,hourFinal,minuteFinal), "dd/MM/yyyy HH:mm");
                String dateTimeOrderString = MainpageActivity.convertDateToString(dateTimeOrderDate, "dd/MM/yyyy h:mm a");

                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("dateTimeOrder").setValue(dateTimeOrderString);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("name").setValue(foodName);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("price").setValue(foodPrice);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("quantity").setValue(quantityValue);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("stallName").setValue(stallname);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("totalPrice").setValue(totalPrice);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("additionalNote").setValue(additionalNote+"");
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("startTime").setValue(startTime);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("endTime").setValue(endTime);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("lastChanges").setValue(lastChangesToEdit);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("stallId").setValue(stallId);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("foodId").setValue(foodId);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("lastChangesInMin").setValue(lastChanges);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("imageurl").setValue(image);


                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child("numOfAddOn").setValue(0);


                for (int i =0; i<addOnArrayList.size();i++) {
                    databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child(Integer.toString(i)).child("name").setValue(addOnArrayList.get(i).getName());
                    databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child(Integer.toString(i)).child("price").setValue(addOnArrayList.get(i).getPrice());
                    databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child("numOfAddOn").setValue(i+1);

                }

                databaseReferenceGettingNumOfCartFood.setValue(++numOfCartFood);

                Intent intent = new Intent(dateTimeSelection.this, CartDisplay.class);
                startActivity(intent);
                Food_display.finishActivity().finish(); // finish Food_display class when the user confirm the food
                finish();

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(dateTimeSelection.this, dateTimeSelection.this, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis()+(int)8.64e+7);
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

                Log.d("-----====", "What is c.getTimeInMillis: "+c.getTimeInMillis());

                DatePickerDialog datePickerDialog = new DatePickerDialog(dateTimeSelection.this, dateTimeSelection.this, year, month, day);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis()+(int)8.64e+7);
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(dateTimeSelection.this, dateTimeSelection.this, hour, minute, false);
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
                                SimpleDateFormat dateComparePattern = new SimpleDateFormat("ddMMyyyy");

                                try {
                                    dateFormatSelectedByUser = datePattern.parse(dateSelectedByUser);
                                    stringCurrentDateConverted = dateComparePattern.parse(currentTime.getDate()+""+(currentTime.getMonth()+1)+(currentTime.getYear()+1900));
                                    stringSelectedUserDateConverted = dateComparePattern.parse(dateFormatSelectedByUser.getDate()+""+(dateFormatSelectedByUser.getMonth()+1)+(dateFormatSelectedByUser.getYear()+1900));
                                    Log.d("0","qwertyuio--++++++++: "+dateFormatSelectedByUser);


                                }catch (ParseException e){

                                }



                                //region Check condition to enable or disable the foodDisplayCheckOut
                                Log.d("456487", "54621: " +currentTime.compareTo(dateFormatSelectedByUser));

                                if (stringCurrentDateConverted.compareTo(stringSelectedUserDateConverted)==0){
                                    if (dateFormatSelectedByUser.getHours()==earlierHour && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours() && dateFormatSelectedByUser.getHours()<endTimeDate.getHours()){
                                        if (dateFormatSelectedByUser.getMinutes()>=earliestMinute && dateFormatSelectedByUser.getMinutes()>=startTimeDate.getMinutes() ){
                                            correctTimeSelected(true, "11");
                                        }
                                        else{
                                            correctTimeSelected(false,"Earliest timing to pre-order is above");

                                        }
                                    }else if (dateFormatSelectedByUser.getHours()>earlierHour && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours() && dateFormatSelectedByUser.getHours()<endTimeDate.getHours()){

                                        correctTimeSelected(true, "12");

                                    }
                                    else if (dateFormatSelectedByUser.getHours()==endTimeDate.getHours()){
                                        if (dateFormatSelectedByUser.getMinutes()<=endTimeDate.getMinutes()){
                                            correctTimeSelected(true, "121");

                                        }
                                        else{
                                            correctTimeSelected(false,"Stall is closed during that timing");

                                        }
                                    }
                                    else if (dateFormatSelectedByUser.getHours()<earlierHour){
                                        correctTimeSelected(false,"Earliest timing to pre-order is above");

                                    }
                                    else{
                                        correctTimeSelected(false,"Stall is closed during that timing");
                                    }

                                }
                                else if (stringCurrentDateConverted.compareTo(stringSelectedUserDateConverted)<0){
                                    if ( dateFormatSelectedByUser.getHours()>=startTimeDate.getHours() && dateFormatSelectedByUser.getHours()<endTimeDate.getHours()){
                                        if ( dateFormatSelectedByUser.getMinutes()>=startTimeDate.getMinutes() ){
                                            correctTimeSelected(true, "41");

                                        }
                                        else{
                                            correctTimeSelected(false,"Stall is closed during that timing");


                                        }
                                    }
                                    else if (dateFormatSelectedByUser.getHours()==endTimeDate.getHours()){
                                        if (dateFormatSelectedByUser.getMinutes()<=endTimeDate.getMinutes()){
                                            correctTimeSelected(true, "421");

                                        }
                                        else{
                                            correctTimeSelected(false,"Stall is closed during that timing");

                                        }
                                    }
                                    else{
                                        correctTimeSelected(false,"Stall is closed during that timing");
                                    }
                                }
                                else {
                                    correctTimeSelected(false,"Earliest timing to pre-order is above");
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
            foodDisplyCheckOut.setEnabled(true);
            tvEarliestCollection.setVisibility(View.VISIBLE);
            tvEarliestCollection.setTextColor(Color.BLACK);
            lastChangesHour = dateFormatSelectedByUser.getHours();
            lastChangesMinute = dateFormatSelectedByUser.getMinutes();

            Log.d("hhhhhhhhh", "What is the lastChangeHour: "+lastChangesHour+" What is the start time hour: "+startTimeDate.getHours());

            if (lastChangesHour<startTimeDate.getHours()){
                lastChangesHour = startTimeDate.getHours();
                lastChangesMinute = startTimeDate.getMinutes();
            }
            else if (lastChangesHour == startTimeDate.getHours()){
                if (lastChangesMinute<startTimeDate.getMinutes()){
                    lastChangesMinute = startTimeDate.getMinutes();
                }
            }


            lastChangesMinute = lastChangesMinute - lastChanges;
            if (lastChangesMinute<0){
                lastChangesHour--;
                lastChangesMinute+=60;
            }

            Log.d("--------------", "What is the lastChangeHour: "+lastChangesHour+" What is the start time hour: "+startTimeDate.getHours());


            SimpleDateFormat timeSelectedToDatePattern = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            SimpleDateFormat timeSelectedToStringPattern = new SimpleDateFormat("h:mm a");
            String timeSelectedLastChanges =null;

            try{
                Date change = timeSelectedToDatePattern.parse(dateFormatSelectedByUser.getDate()+"/"+dateFormatSelectedByUser.getMonth()+1+"/"+ dateFormatSelectedByUser.getYear()+1900+" "+lastChangesHour+":"+lastChangesMinute);
                Log.d("change", "what is change: "+change);
                Log.d("123445665454", "What is the lastChangeHour: "+lastChangesHour+" What is the lastChangesMinute: "+lastChangesMinute);

                timeSelectedLastChanges = timeSelectedToStringPattern.format(change);
            }
            catch (ParseException e){

            }

            Log.d("timeSelectedLastChanges", "TimeSelctedLastChanges is "+timeSelectedLastChanges);

            lastChangesToEdit = String.format("%02d/%02d/%02d %s", dateFormatSelectedByUser.getDate(), dateFormatSelectedByUser.getMonth()+1, dateFormatSelectedByUser.getYear()+1900, timeSelectedLastChanges);
            tvLastChanges.setText(Html.fromHtml(String.format("<b>Latest</b> Editable: %s",lastChangesToEdit)));
            tvLastChanges.setTextColor(Color.rgb(16,124,16));


        }
        else{
            blink(tvEarliestCollection);
            foodDisplyCheckOut.setEnabled(false);
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
