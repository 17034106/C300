package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Budget;
import sg.edu.rp.c346.c300.model.Cart;

public class BudgetInformation extends AppCompatActivity {


    TextView foodMin, foodMax, drinkMin, drinkMax, stationeryMin, stationeryMax, charityMin, charityMax, othersMin, othersMax;
    TextView foodValue, drinkValue, stationeryValue, charityValue, othersValue;
//    Switch switchFood, switchDrink, switchStationery, switchCharity, switchOthers;
    SeekBar seekBarFood, seekBarDrink, seekBarStationery, seekBarCharity, seekBarOthers;
    TextView totalValueAvailable, totalValueSelected;

    CircularProgressButton btnConfirm;


    double totalBudget;
    double cumulativeBudget;

    int minSeekBarFood, maxSeekBarFood, minSeekBarDrink, maxSeekBarDrink, minSeekBarStationery, maxSeekBarStationery, minSeekBarCharity, maxSeekBarCharity, minSeekBarOthers, maxSeekBarOthers;
    int progressSeekBarFood, progressSeekBarDrink, progressSeekBarStationery, progressSeekBarCharity, progressSeekBarOthers;

    DatabaseReference drBudget;

    Budget budget;

    int dayOfWeekInDB; // need to minus 2 from the actual result of dayOfWeek

    boolean withinBudget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget_information);


        foodMin = findViewById(R.id.foodMin);
        foodMax = findViewById(R.id.foodMax);
        drinkMin = findViewById(R.id.drinkMin);
        drinkMax = findViewById(R.id.drinkMax);
        stationeryMin = findViewById(R.id.stationeryMin);
        stationeryMax = findViewById(R.id.stationeryMax);
        charityMin = findViewById(R.id.charityMin);
        charityMax = findViewById(R.id.charityMax);
        othersMin = findViewById(R.id.othersMin);
        othersMax = findViewById(R.id.othersMax);

        foodValue = findViewById(R.id.foodValue);
        drinkValue = findViewById(R.id.drinkValue);
        stationeryValue = findViewById(R.id.stationeryValue);
        charityValue = findViewById(R.id.charityValue);
        othersValue = findViewById(R.id.othersValue);

//        switchFood = findViewById(R.id.switchFood);
//        switchDrink = findViewById(R.id.switchDrink);
//        switchStationery = findViewById(R.id.switchStationery);
//        switchCharity = findViewById(R.id.switchCharity);
//        switchOthers = findViewById(R.id.switchOthers);

        seekBarFood = findViewById(R.id.seekBarFood);
        seekBarDrink = findViewById(R.id.seekBarDrink);
        seekBarStationery = findViewById(R.id.seekBarStationery);
        seekBarCharity = findViewById(R.id.seekBarCharity);
        seekBarOthers = findViewById(R.id.seekBarOthers);

        totalValueAvailable = findViewById(R.id.totalValueAvailable);
        totalValueSelected = findViewById(R.id.totalValueSelected);

        btnConfirm = findViewById(R.id.btnConfirm);


        drBudget = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day");

        // getting the day of the week
        Date currentTime = Calendar.getInstance().getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentTime);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        dayOfWeekInDB = dayOfWeek - 2;
        if (dayOfWeekInDB==-1){
            dayOfWeekInDB =6;
        }

        Log.d("What is the value", "What is the day now: "+dayOfWeekInDB);


        //region retrieving all the data for the budget
        //-----------------------------------------------------------------------------------------------------------------------------------------
        drBudget.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                budget = dataSnapshot.child(dayOfWeekInDB+"").getValue(Budget.class);

                totalBudget = budget.getAllowance();
                totalValueAvailable.setText(String.format("%.2f", totalBudget));

                if (budget.getCategory().getFood().getChangedValueMax() !=-1 || budget.getCategory().getFood().getChangedValueMin() !=-1){
                    foodMin.setText(String.format("$%.2f",budget.getCategory().getFood().getChangedValueMin()));
                    foodMax.setText(String.format("$%.2f",budget.getCategory().getFood().getChangedValueMax()));
                    minSeekBarFood = (int) (budget.getCategory().getFood().getChangedValueMin() * 10);
                    maxSeekBarFood = (int) (budget.getCategory().getFood().getChangedValueMax()*10);
                }
                else{
                    foodMin.setText(String.format("$%.2f",budget.getCategory().getFood().getDefaultValueMin()));
                    foodMax.setText(String.format("$%.2f",budget.getCategory().getFood().getDefaultValueMax()));
                    minSeekBarFood = (int) (budget.getCategory().getFood().getDefaultValueMin() * 10);
                    maxSeekBarFood = (int) (budget.getCategory().getFood().getDefaultValueMax()*10);
                }


                if (budget.getCategory().getDrink().getChangedValueMax() !=-1 || budget.getCategory().getDrink().getChangedValueMin() !=-1){
                    drinkMin.setText(String.format("$%.2f",budget.getCategory().getDrink().getChangedValueMin()));
                    drinkMax.setText(String.format("$%.2f",budget.getCategory().getDrink().getChangedValueMax()));
                    minSeekBarDrink = (int) (budget.getCategory().getDrink().getChangedValueMin() * 10);
                    maxSeekBarDrink = (int) (budget.getCategory().getDrink().getChangedValueMax()*10);
                }
                else{
                    drinkMin.setText(String.format("$%.2f",budget.getCategory().getDrink().getDefaultValueMin()));
                    drinkMax.setText(String.format("$%.2f",budget.getCategory().getDrink().getDefaultValueMax()));
                    minSeekBarDrink = (int) (budget.getCategory().getDrink().getDefaultValueMin() * 10);
                    maxSeekBarDrink = (int) (budget.getCategory().getDrink().getDefaultValueMax()*10);
                }


                if (budget.getCategory().getStationery().getChangedValueMax() !=-1 || budget.getCategory().getStationery().getChangedValueMin() !=-1){
                    stationeryMin.setText(String.format("$%.2f",budget.getCategory().getStationery().getChangedValueMin()));
                    stationeryMax.setText(String.format("$%.2f",budget.getCategory().getStationery().getChangedValueMax()));
                    minSeekBarStationery = (int) (budget.getCategory().getStationery().getChangedValueMin() * 10);
                    maxSeekBarStationery = (int) (budget.getCategory().getStationery().getChangedValueMax()*10);
                }
                else{
                    stationeryMin.setText(String.format("$%.2f",budget.getCategory().getStationery().getDefaultValueMin()));
                    stationeryMax.setText(String.format("$%.2f",budget.getCategory().getStationery().getDefaultValueMax()));
                    minSeekBarStationery = (int) (budget.getCategory().getStationery().getDefaultValueMin() * 10);
                    maxSeekBarStationery = (int) (budget.getCategory().getStationery().getDefaultValueMax()*10);
                }



                if (budget.getCategory().getCharity().getChangedValueMax() !=-1 || budget.getCategory().getCharity().getChangedValueMin() !=-1){
                    charityMin.setText(String.format("$%.2f",budget.getCategory().getCharity().getChangedValueMin()));
                    charityMax.setText(String.format("$%.2f",budget.getCategory().getCharity().getChangedValueMax()));
                    minSeekBarCharity = (int) (budget.getCategory().getCharity().getChangedValueMin() * 10);
                    maxSeekBarCharity = (int) (budget.getCategory().getCharity().getChangedValueMax()*10);
                }
                else{
                    charityMin.setText(String.format("$%.2f",budget.getCategory().getCharity().getDefaultValueMin()));
                    charityMax.setText(String.format("$%.2f",budget.getCategory().getCharity().getDefaultValueMax()));
                    minSeekBarCharity = (int) (budget.getCategory().getCharity().getDefaultValueMin() * 10);
                    maxSeekBarCharity = (int) (budget.getCategory().getCharity().getDefaultValueMax()*10);
                }



                if (budget.getCategory().getOthers().getChangedValueMax() !=-1 || budget.getCategory().getOthers().getChangedValueMin() !=-1){
                    othersMin.setText(String.format("$%.2f",budget.getCategory().getOthers().getChangedValueMin()));
                    othersMax.setText(String.format("$%.2f",budget.getCategory().getOthers().getChangedValueMax()));
                    minSeekBarOthers = (int) (budget.getCategory().getOthers().getChangedValueMin() * 10);
                    maxSeekBarOthers = (int) (budget.getCategory().getOthers().getChangedValueMax()*10);
                }
                else{
                    othersMin.setText(String.format("$%.2f",budget.getCategory().getOthers().getDefaultValueMin()));
                    othersMax.setText(String.format("$%.2f",budget.getCategory().getOthers().getDefaultValueMax()));
                    minSeekBarOthers = (int) (budget.getCategory().getOthers().getDefaultValueMin() * 10);
                    maxSeekBarOthers = (int) (budget.getCategory().getOthers().getDefaultValueMax()*10);
                }


                seekBarFood.setMax(maxSeekBarFood-minSeekBarFood);
                seekBarDrink.setMax(maxSeekBarDrink-minSeekBarDrink);
                seekBarStationery.setMax(maxSeekBarStationery-minSeekBarStationery);
                seekBarCharity.setMax(maxSeekBarCharity-minSeekBarCharity);
                seekBarOthers.setMax(maxSeekBarOthers-minSeekBarOthers);



                progressSeekBarFood =(int) (budget.getCategory().getFood().getAmount() *10);
                progressSeekBarDrink =(int) (budget.getCategory().getDrink().getAmount() *10);
                progressSeekBarStationery =(int) (budget.getCategory().getStationery().getAmount() *10);
                progressSeekBarCharity =(int) (budget.getCategory().getCharity().getAmount() *10);
                progressSeekBarOthers =(int) (budget.getCategory().getOthers().getAmount() *10);


                foodValue.setText(String.format("%.2f", progressSeekBarFood/10.0));
                drinkValue.setText(String.format("%.2f",progressSeekBarDrink/10.0));
                stationeryValue.setText(String.format("%.2f",progressSeekBarStationery/10.0));
                charityValue.setText(String.format("%.2f", progressSeekBarCharity/10.0));
                othersValue.setText(String.format("%.2f",progressSeekBarOthers/10.0));

                cumulativeBudget = (progressSeekBarFood + progressSeekBarDrink + progressSeekBarStationery + progressSeekBarCharity + progressSeekBarOthers)/10.0;
                totalValueSelected.setText(String.format("%.2f", cumulativeBudget));
                totalValueSelected.setTextColor(Color.GREEN);


                seekBarFood.setProgress(progressSeekBarFood-minSeekBarFood);
                seekBarDrink.setProgress(progressSeekBarDrink- minSeekBarDrink);
                seekBarStationery.setProgress(progressSeekBarStationery- minSeekBarStationery);
                seekBarCharity.setProgress(progressSeekBarCharity- minSeekBarCharity);
                seekBarOthers.setProgress(progressSeekBarOthers- minSeekBarOthers);




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //-----------------------------------------------------------------------------------------------------------------------------------------
        //endregion


        //region seekBar
        //------------------------------------------------------------------------------------------------------------------------------------------
        seekBarFood.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressNow = progressSeekBarFood;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressSeekBarFood = progress+minSeekBarFood;
                foodValue.setText(String.format("%.2f", progressSeekBarFood/10.0));
                progressNow = progress + minSeekBarFood;

                cumulativeBudget = (progressSeekBarFood + progressSeekBarDrink + progressSeekBarStationery + progressSeekBarCharity + progressSeekBarOthers)/10.0;
                totalValueSelected.setText(String.format("%.2f", cumulativeBudget));

                checking();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        seekBarDrink.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressNow = progressSeekBarDrink;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressSeekBarDrink = progress+minSeekBarDrink;
                drinkValue.setText(String.format("%.2f", progressSeekBarDrink/10.0));
                progressNow = progress + minSeekBarDrink;

                cumulativeBudget = (progressSeekBarFood + progressSeekBarDrink + progressSeekBarStationery + progressSeekBarCharity + progressSeekBarOthers)/10.0;
                totalValueSelected.setText(String.format("%.2f", cumulativeBudget));

                checking();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        seekBarStationery.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressNow = progressSeekBarStationery;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressSeekBarStationery = progress+minSeekBarStationery;
                stationeryValue.setText(String.format("%.2f", progressSeekBarStationery/10.0));
                progressNow = progress + minSeekBarStationery;

                cumulativeBudget = (progressSeekBarFood + progressSeekBarDrink + progressSeekBarStationery + progressSeekBarCharity + progressSeekBarOthers)/10.0;
                totalValueSelected.setText(String.format("%.2f", cumulativeBudget));

                checking();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {



            }
        });



        seekBarCharity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressNow = progressSeekBarCharity;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressSeekBarCharity = progress+minSeekBarCharity;
                charityValue.setText(String.format("%.2f", progressSeekBarCharity/10.0));
                progressNow = progress + minSeekBarCharity;

                cumulativeBudget = (progressSeekBarFood + progressSeekBarDrink + progressSeekBarStationery + progressSeekBarCharity + progressSeekBarOthers)/10.0;
                totalValueSelected.setText(String.format("%.2f", cumulativeBudget));


                checking();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });



        seekBarOthers.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressNow = progressSeekBarOthers;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressSeekBarOthers = progress+minSeekBarOthers;
                othersValue.setText(String.format("%.2f", progressSeekBarOthers/10.0));
                progressNow = progress + minSeekBarOthers;

                cumulativeBudget = (progressSeekBarFood + progressSeekBarDrink + progressSeekBarStationery + progressSeekBarCharity + progressSeekBarOthers)/10.0;
                totalValueSelected.setText(String.format("%.2f", cumulativeBudget));

                checking();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //----------------------------------------------------------------------------------------------------------------------------
        //endregion




        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (withinBudget) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:


                                    budget.getCategory().getFood().setAmount(progressSeekBarFood / 10.0);
                                    budget.getCategory().getDrink().setAmount(progressSeekBarDrink / 10.0);
                                    budget.getCategory().getStationery().setAmount(progressSeekBarStationery / 10.0);
                                    budget.getCategory().getCharity().setAmount(progressSeekBarCharity / 10.0);
                                    budget.getCategory().getOthers().setAmount(progressSeekBarOthers / 10.0);


                                    drBudget.child(dayOfWeekInDB + "").setValue(budget);

                                    Toast.makeText(BudgetInformation.this, "Successfully updated", Toast.LENGTH_SHORT).show();

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(BudgetInformation.this);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();

                }
                else{
                    Toast.makeText(BudgetInformation.this, "Over Budget", Toast.LENGTH_SHORT).show();
                }

            }
        });





    }


    // checking the consider - ensure that cumulative Budget is lower than the total budget
    private void checking(){
        if (cumulativeBudget>totalBudget){
            totalValueSelected.setTextColor(Color.RED);
            withinBudget = false;

        }
        else {
            totalValueSelected.setTextColor(Color.GREEN);
            withinBudget = true;
        }
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
