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

import sg.edu.rp.c346.c300.model.AddOn;

public class dateTimeSelection extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView btnSelectDate, btnSelectTime, tvCurrentDateTime, tvEarliestCollection, tvLastChanges;

    RelativeLayout foodDisplyCheckOut;

    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    String dayOfWeek="";

    int lastChanges;

    // use for finding the earliest timing to make the pre-order
    int earliestMinute;
    int earlierHour;

    // use for finding the last changes timing to make changes
    int lastChangesMinute;
    int lastChangesHour;

    DatabaseReference databaseReferenceGettingNumOfCartFood;
    int numOfCartFood=-0;

    Date currentTime = Calendar.getInstance().getTime();

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



                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("dateTimeOrder").setValue(String.format("%02d/%02d/%02d %02d:%02d",dayFinal,monthFinal,yearFinal,hourFinal,minuteFinal));
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("name").setValue(foodName);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("price").setValue(foodPrice);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("quantity").setValue(quantityValue);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("stallName").setValue(stallname);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("totalPrice").setValue(totalPrice);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("additionalNote").setValue(additionalNote+"");
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("startTime").setValue(startTime);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("endTime").setValue(endTime);


                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child("numOfAddOn").setValue(0);


                for (int i =0; i<addOnArrayList.size();i++) {
                    databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child(Integer.toString(i)).child("name").setValue(addOnArrayList.get(i).getName());
                    databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child(Integer.toString(i)).child("price").setValue(addOnArrayList.get(i).getPrice());
                    databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("addOn").child("numOfAddOn").setValue(i+1);

                }

                databaseReferenceGettingNumOfCartFood.setValue(++numOfCartFood);

                Intent intent = new Intent(dateTimeSelection.this, CartDisplay.class);
                startActivity(intent);
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
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis()+(int)6.048e+8);
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

        String timeInputPattern = "HH:mm";
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
                                Date startTimeDate =null; // use to store the start time in date format
                                Date endTimeDate = null; // use to store the end time in date format

                                Calendar earliestDate = Calendar.getInstance();

                                try{
                                    startTimeDate = earliestTimeInputFormat.parse(startTime);
                                    endTimeDate = earliestTimeInputFormat.parse(endTime);


                                    earliestMinute = currentTime.getMinutes()+lastChanges+1;
                                    earlierHour = currentTime.getHours();
                                    if (earliestMinute>59){
                                        earliestMinute=earliestMinute-60;
                                        earlierHour++;
                                    }

                                    if (earlierHour==endTimeDate.getHours()){
                                        if (earliestMinute>endTimeDate.getMinutes()){
                                            earliestDate.add(Calendar.DATE,1);
                                            earlierHour = startTimeDate.getHours();
                                            earliestMinute =startTimeDate.getMinutes();
                                        }
                                    }
                                    else{
                                        if (earlierHour>endTimeDate.getHours()){
                                            earliestDate.add(Calendar.DATE,1);
                                            earlierHour = startTimeDate.getHours();
                                            earliestMinute =startTimeDate.getMinutes();
                                        }
                                    }


                                    earliestTimeConverted = earliestTimeInputFormat.parse(String.format("%02d%02d",earlierHour,earliestMinute));


                                }
                                catch (ParseException e){

                                }

                                tvEarliestCollection.setText(Html.fromHtml(String.format("<b>Earliest</b> Timing: %02d/%02d/%d %s",earliestDate.getTime().getDate(), earliestDate.getTime().getMonth()+1, earliestDate.getTime().getYear()+1900, timeFormat.format(earliestTimeConverted))));

                                //endregion


                                SimpleDateFormat datePattern = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                                String dateSelectedByUser = String.format("%02d/%02d/%02d %02d:%02d", dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal);
                                Date dateFormatSelectedByUser =null; // store the selected date by user in date format

                                try {
                                    dateFormatSelectedByUser = datePattern.parse(dateSelectedByUser);




                                }catch (ParseException e){

                                }

                                //Check condition to enable or disable the foodDisplayCheckOut
                                if (currentTime.compareTo(dateFormatSelectedByUser)==0){
                                        if (dateFormatSelectedByUser.getHours()==earlierHour && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours() && dateFormatSelectedByUser.getHours()<=endTimeDate.getHours()){
                                            if (dateFormatSelectedByUser.getMinutes()>=earliestMinute && dateFormatSelectedByUser.getMinutes()>=startTimeDate.getMinutes() && dateFormatSelectedByUser.getMinutes()<=endTimeDate.getMinutes()){
                                                foodDisplyCheckOut.setEnabled(true);
                                                tvEarliestCollection.setVisibility(View.VISIBLE);
                                                tvLastChanges.setText("11");
                                            }
                                            else{
                                                tvEarliestCollection.setTextColor(Color.RED);
                                                blink(tvEarliestCollection);
                                                tvLastChanges.setText("1");

                                            }
                                        }else if (dateFormatSelectedByUser.getHours()>earlierHour && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours() && dateFormatSelectedByUser.getHours()<endTimeDate.getHours()){

                                            foodDisplyCheckOut.setEnabled(true);
                                            tvEarliestCollection.setVisibility(View.VISIBLE);
                                            tvLastChanges.setText("12");


                                        }
                                        else if (dateFormatSelectedByUser.getHours()==endTimeDate.getHours()){
                                            if (dateFormatSelectedByUser.getMinutes()<=endTimeDate.getMinutes()){
                                                foodDisplyCheckOut.setEnabled(true);
                                                tvEarliestCollection.setVisibility(View.VISIBLE);
                                                tvLastChanges.setText("121");
                                            }
                                            else{
                                                tvEarliestCollection.setTextColor(Color.RED);
                                                blink(tvEarliestCollection);
                                                tvLastChanges.setText("9");
                                            }
                                        }
                                        else{
                                            tvEarliestCollection.setTextColor(Color.RED);
                                            blink(tvEarliestCollection);
                                            tvLastChanges.setText("3");
                                        }

                                    }
                                    else if (currentTime.compareTo(dateFormatSelectedByUser)<0){
                                        if (dateFormatSelectedByUser.getHours()==earlierHour && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours() && dateFormatSelectedByUser.getHours()<=endTimeDate.getHours()){
                                            if (dateFormatSelectedByUser.getMinutes()>=earliestMinute && dateFormatSelectedByUser.getMinutes()>=startTimeDate.getMinutes() && dateFormatSelectedByUser.getMinutes()<=endTimeDate.getMinutes()){
                                                foodDisplyCheckOut.setEnabled(true);
                                                tvEarliestCollection.setVisibility(View.VISIBLE);
                                                tvLastChanges.setText("13");

                                            }
                                            else{
                                                tvEarliestCollection.setTextColor(Color.RED);
                                                blink(tvEarliestCollection);
                                                tvLastChanges.setText("4");
                                                Log.d("now", "now: "+currentTime);
                                                Log.d("Selected", "Selected: "+dateFormatSelectedByUser);


                                            }
                                        }
                                        else if (dateFormatSelectedByUser.getHours()>earlierHour && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours() && dateFormatSelectedByUser.getHours()<endTimeDate.getHours()){
                                            foodDisplyCheckOut.setEnabled(true);
                                            tvEarliestCollection.setVisibility(View.VISIBLE);
                                            tvLastChanges.setText("14");

                                        }
                                        else if (dateFormatSelectedByUser.getHours() == endTimeDate.getHours()){
                                            if (dateFormatSelectedByUser.getMinutes()<=endTimeDate.getMinutes()){
                                                foodDisplyCheckOut.setEnabled(true);
                                                tvEarliestCollection.setVisibility(View.VISIBLE);
                                                tvLastChanges.setText("121");
                                            }
                                            else{
                                                tvEarliestCollection.setTextColor(Color.RED);
                                                blink(tvEarliestCollection);
                                                tvLastChanges.setText("8");
                                            }

                                        }

                                        else{
                                            tvEarliestCollection.setTextColor(Color.RED);
                                            blink(tvEarliestCollection);
                                            tvLastChanges.setText("6");

                                        }
                                    }
                                    else if (currentTime.compareTo(dateFormatSelectedByUser)>0){
                                        tvEarliestCollection.setTextColor(Color.RED);
                                        blink(tvEarliestCollection);
                                        tvLastChanges.setText("7");
                                        Log.d("now", "now: "+currentTime);
                                        Log.d("Selected", "Selected: "+dateFormatSelectedByUser);

                                }



                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();}

        //endregion



    public void blink(final TextView txt) {
        if (txt.getVisibility()==View.VISIBLE){
            txt.setVisibility(View.INVISIBLE);
        }
        else{
            txt.setVisibility(View.VISIBLE);
        }
    }



}
