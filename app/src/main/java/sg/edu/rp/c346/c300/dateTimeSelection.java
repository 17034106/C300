package sg.edu.rp.c346.c300;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.TimeZoneFormat;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import sg.edu.rp.c346.c300.model.AddOn;

public class dateTimeSelection extends Activity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    TextView btnSelectDate, btnSelectTime;

    int day, month, year, hour, minute;
    int dayFinal, monthFinal, yearFinal, hourFinal, minuteFinal;

    String dayOfWeek="";

    DatabaseReference databaseReferenceGettingNumOfCartFood;
    int numOfCartFood=-0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_selection);


        //region  pop up window for select date and time
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width), (int)(height*.35));
        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.gravity = Gravity.CENTER;
//        params.x = 0;
//        params.y = -20;
        params.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(params);
        //endregion




        Intent intentReceive = getIntent();
        final String foodName = intentReceive.getStringExtra("foodName");
        final double foodPrice = intentReceive.getDoubleExtra("foodPrice",0);
        final String stallname = intentReceive.getStringExtra("stallName");
        int lastChanges = intentReceive.getIntExtra("lastChanges", 0);
        final int quantityValue = intentReceive.getIntExtra("quantity", 0);
        final String additionalNote = intentReceive.getStringExtra("additionalNote");
        final double totalPrice = intentReceive.getDoubleExtra("totalPrice",0);

        final ArrayList<AddOn> addOnArrayList =Food_display.addOnArray;
        Log.d("What is addOnArrayList", "What is the first position of the addOnArrayList : "+addOnArrayList);




        TextView tvtotalPrice = findViewById(R.id.tvTotalPrice);
        tvtotalPrice.setText(String.format("$%.2f", totalPrice));



        //region Press addToCartButton


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


        final DatabaseReference databaseReferenceAddFoodCart = FirebaseDatabase.getInstance().getReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        RelativeLayout foodDisplyCheckOut = findViewById(R.id.FoodDisplyCheckOut);
        foodDisplyCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("----------------","================++++++++++="+numOfCartFood);

                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("dateTimeOrder").setValue(String.format("%02d/%02d/%02d %02d:%02d",dayFinal,monthFinal,yearFinal,hourFinal,minuteFinal));
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("name").setValue(foodName);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("price").setValue(foodPrice);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("quantity").setValue(quantityValue);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("stallName").setValue(stallname);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("totalPrice").setValue(totalPrice);
                databaseReferenceAddFoodCart.child(Integer.toString(numOfCartFood)).child("additionalNote").setValue(additionalNote+"");

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

        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(dateTimeSelection.this, dateTimeSelection.this, year, month, day);
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

        TimePickerDialog timePickerDialog = new TimePickerDialog(dateTimeSelection.this, dateTimeSelection.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        hourFinal = hourOfDay;
        minuteFinal = minute;

        btnSelectDate.setText(String.format("%02d/%02d/%02d (%s)",dayFinal,monthFinal,yearFinal,dayOfWeek));
        btnSelectTime.setText(String.format("%02d:%02d",hourFinal,minuteFinal));
    }

    //endregion
}
