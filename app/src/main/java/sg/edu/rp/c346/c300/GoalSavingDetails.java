package sg.edu.rp.c346.c300;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.Date;

import me.itangqi.waveloadingview.WaveLoadingView;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.GoalSaving;

public class GoalSavingDetails extends AppCompatActivity {

    WaveLoadingView goalSavingWaveLoadingView;
    TextView goalName, goalPrice, goalRemainingAmountRequire, goalSavingNameTitle;
    EditText currentSaving;

    TextView goalSavingNumberOfDay;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    EditText dailySaving;


    GoalSaving goalSavingIntentReceive;

    double remainingAmountRequired;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_saving_details);

        goalSavingWaveLoadingView = findViewById(R.id.GoalSavingWaveLoadingView);
        currentSaving = findViewById(R.id.currentSaving);
        goalName = findViewById(R.id.GoalSavingName);
        goalSavingNameTitle = findViewById(R.id.GoalSavingName);
        goalPrice = findViewById(R.id.GoalSavingPrice);
        goalRemainingAmountRequire = findViewById(R.id.GoalSavingRemainingAmountRequired);
        goalSavingNumberOfDay = findViewById(R.id.GoalSavingNumberOfDay);
        dailySaving = findViewById(R.id.dailySaving);

        goalSavingWaveLoadingView.setProgressValue(0);

        //Get the value from GoalSavingAll class
        Intent intentReceive = getIntent();
        goalSavingIntentReceive = (GoalSaving) intentReceive.getSerializableExtra("goalSaving");



        goalName.setText(goalSavingIntentReceive.getName());
        goalSavingNameTitle.setText(goalSavingIntentReceive.getName());
        goalPrice.setText(String.format("$%.2f", goalSavingIntentReceive.getPrice()));




        findViewById(R.id.buttonCalculate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentSaving.getText()!=null) {

                    int percentageOfCompletion = (int) ((Double.parseDouble(currentSaving.getText().toString()) / goalSavingIntentReceive.getPrice()) * 100);

                    goalSavingWaveLoadingView.setProgressValue(percentageOfCompletion);

                    remainingAmountRequired = goalSavingIntentReceive.getPrice() - Double.parseDouble(currentSaving.getText().toString());
                    goalRemainingAmountRequire.setText(String.format("$%.2f", remainingAmountRequired));


                    if ((int) ((Double.parseDouble(currentSaving.getText().toString()) / goalSavingIntentReceive.getPrice()) * 100) < 50) {
                        goalSavingWaveLoadingView.setBottomTitle(String.format("%d%%", percentageOfCompletion));
                        goalSavingWaveLoadingView.setCenterTitle("");
                        goalSavingWaveLoadingView.setTopTitle("");
                    } else if (percentageOfCompletion < 80) {
                        goalSavingWaveLoadingView.setBottomTitle("");
                        goalSavingWaveLoadingView.setCenterTitle(String.format("%d%%", percentageOfCompletion));
                        goalSavingWaveLoadingView.setTopTitle("");
                    } else {
                        goalSavingWaveLoadingView.setBottomTitle("");
                        goalSavingWaveLoadingView.setCenterTitle("");
                        goalSavingWaveLoadingView.setTopTitle(String.format("%d%%", percentageOfCompletion));
                    }

                }
                else{
                    Toast.makeText(GoalSavingDetails.this, "Enter the amount", Toast.LENGTH_SHORT).show();
                }


            }
        });



        //region Display DatePicker
        goalSavingNumberOfDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(GoalSavingDetails.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                goalSavingNumberOfDay.setText(dayOfMonth+"/"+month+"/"+year);
            }
        };

        //endregion


        findViewById(R.id.btnCalculateReultOfWhen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (goalSavingNumberOfDay != null) {

                    Date currentDate = MainpageActivity.convertStringToDate(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy"), "dd/MM/yyyy");
                    Date userSelected = MainpageActivity.convertStringToDate(goalSavingNumberOfDay.getText().toString(), "dd/MM/yyyy");

                    Calendar currentDateCalendar = Calendar.getInstance();
                    currentDateCalendar.setTime(currentDate);
                    Calendar userSelectedCalendar = Calendar.getInstance();
                    userSelectedCalendar.setTime(userSelected);

                    int numOfWeekDays = 0;
                    int numOfDays = 0;
                    while (userSelectedCalendar.after(currentDateCalendar) || userSelectedCalendar.compareTo(currentDateCalendar) == 0) {
                        if (currentDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || currentDateCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {

                        } else {
                            numOfWeekDays++;
                        }
                        numOfDays++;
                        currentDateCalendar.add(Calendar.DATE, 1);
                    }


                    double eachDaySavingDeductedAndWeekdays = remainingAmountRequired / (double) numOfWeekDays;
                    double eachDaySavingDeducted = remainingAmountRequired / (double) numOfDays;

                    double eachDaySavingWeekdays = goalSavingIntentReceive.getPrice() / (double) numOfWeekDays;
                    double eachDaySaving = goalSavingIntentReceive.getPrice() / (double) numOfWeekDays;


                    final LinearLayout linearLayout = findViewById(R.id.resultOfWhen);


                    linearLayout.removeAllViews();

                    RelativeLayout.LayoutParams layoutParamsBiggestTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsBiggestTitle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParamsBiggestTitle.setMargins(35, 20, 0, 70);

                    RelativeLayout.LayoutParams layoutParamsSecondTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsSecondTitle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParamsSecondTitle.setMargins(35, 20, 0, 60);

                    RelativeLayout.LayoutParams layoutParamsThirdTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsThirdTitle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParamsThirdTitle.setMargins(35, 20, 0, 50);

                    RelativeLayout.LayoutParams layoutParamsFouthTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsFouthTitle.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    layoutParamsFouthTitle.setMargins(35, 20, 0, 20);


                    TextView tvResult = new TextView(getApplication());
                    tvResult.setLayoutParams(layoutParamsBiggestTitle);
                    tvResult.setText("Result: ");
                    tvResult.setTextSize(24);
                    tvResult.setTextColor(Color.BLACK);
                    tvResult.setTypeface(tvResult.getTypeface(), Typeface.BOLD_ITALIC);


                    TextView tvWithDeductedTitle = new TextView(getApplication());
                    tvWithDeductedTitle.setLayoutParams(layoutParamsSecondTitle);
                    tvWithDeductedTitle.setText("With Current Saving Balance ");
                    tvWithDeductedTitle.setTextSize(22);
                    tvWithDeductedTitle.setTextColor(Color.BLACK);
                    tvWithDeductedTitle.setTypeface(tvWithDeductedTitle.getTypeface(), Typeface.BOLD_ITALIC);


                    TextView tvDeductedWeekday = new TextView(getApplication());
                    tvDeductedWeekday.setLayoutParams(layoutParamsThirdTitle);
                    tvDeductedWeekday.setText("Only Weekday: ");
                    tvDeductedWeekday.setTextSize(20);
                    tvDeductedWeekday.setTextColor(Color.BLACK);
                    tvDeductedWeekday.setTypeface(tvDeductedWeekday.getTypeface(), Typeface.BOLD_ITALIC);


                    TextView tvEachDaySavingDeductedAndWeekdays = new TextView(getApplication());
                    tvEachDaySavingDeductedAndWeekdays.setLayoutParams(layoutParamsFouthTitle);
                    tvEachDaySavingDeductedAndWeekdays.setText(String.format("$%.2f", eachDaySavingDeductedAndWeekdays));
                    tvEachDaySavingDeductedAndWeekdays.setTextSize(18);
                    tvEachDaySavingDeductedAndWeekdays.setTextColor(Color.GRAY);


                    TextView tvDeducted = new TextView(getApplication());
                    tvDeducted.setLayoutParams(layoutParamsThirdTitle);
                    tvDeducted.setText("All Days: ");
                    tvDeducted.setTextSize(20);
                    tvDeducted.setTextColor(Color.BLACK);
                    tvDeducted.setTypeface(tvDeducted.getTypeface(), Typeface.BOLD_ITALIC);


                    TextView tvEachDaySavingDeducted = new TextView(getApplication());
                    tvEachDaySavingDeducted.setLayoutParams(layoutParamsFouthTitle);
                    tvEachDaySavingDeducted.setText(String.format("$%.2f", eachDaySavingDeducted));
                    tvEachDaySavingDeducted.setTextSize(18);
                    tvEachDaySavingDeducted.setTextColor(Color.GRAY);


                    TextView tvWithoutDeductedTitle = new TextView(getApplication());
                    tvWithoutDeductedTitle.setLayoutParams(layoutParamsSecondTitle);
                    tvWithoutDeductedTitle.setText("Without Current Saving Balance ");
                    tvWithoutDeductedTitle.setTextSize(22);
                    tvWithoutDeductedTitle.setTextColor(Color.BLACK);
                    tvWithoutDeductedTitle.setTypeface(tvWithoutDeductedTitle.getTypeface(), Typeface.BOLD_ITALIC);


                    TextView tvWeekday = new TextView(getApplication());
                    tvWeekday.setLayoutParams(layoutParamsThirdTitle);
                    tvWeekday.setText("Only Weekday: ");
                    tvWeekday.setTextSize(20);
                    tvWeekday.setTextColor(Color.BLACK);
                    tvWeekday.setTypeface(tvWeekday.getTypeface(), Typeface.BOLD_ITALIC);


                    TextView tvEachDaySavingWeekdays = new TextView(getApplication());
                    tvEachDaySavingWeekdays.setLayoutParams(layoutParamsFouthTitle);
                    tvEachDaySavingWeekdays.setText(String.format("$%.2f", eachDaySavingWeekdays));
                    tvEachDaySavingWeekdays.setTextSize(18);
                    tvEachDaySavingWeekdays.setTextColor(Color.GRAY);


                    TextView tvEachDay = new TextView(getApplication());
                    tvEachDay.setLayoutParams(layoutParamsThirdTitle);
                    tvEachDay.setText("All Days: ");
                    tvEachDay.setTextSize(20);
                    tvEachDay.setTextColor(Color.BLACK);
                    tvEachDay.setTypeface(tvEachDay.getTypeface(), Typeface.BOLD_ITALIC);


                    TextView tvEachDaySaving = new TextView(getApplication());
                    tvEachDaySaving.setLayoutParams(layoutParamsFouthTitle);
                    tvEachDaySaving.setText(String.format("$%.2f", eachDaySaving));
                    tvEachDaySaving.setTextSize(18);
                    tvEachDaySaving.setTextColor(Color.GRAY);


                    linearLayout.addView(tvResult);
                    linearLayout.addView(tvWithDeductedTitle);
                    linearLayout.addView(tvDeductedWeekday);
                    linearLayout.addView(tvEachDaySavingDeductedAndWeekdays);
                    linearLayout.addView(tvDeducted);
                    linearLayout.addView(tvEachDaySavingDeducted);
                    linearLayout.addView(tvWithoutDeductedTitle);
                    linearLayout.addView(tvWeekday);
                    linearLayout.addView(tvEachDaySavingWeekdays);
                    linearLayout.addView(tvEachDay);
                    linearLayout.addView(tvEachDaySaving);

                }
                else{
                    Toast.makeText(GoalSavingDetails.this, "Please Choose a Date", Toast.LENGTH_SHORT).show();
                }


            }

        });


        findViewById(R.id.btnCalculateResultOfNumberOfDay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dailySaving.getText()!=null){

                    double dailySavingDouble = Double.parseDouble(dailySaving.getText().toString());

                    double withCurrentSaving =goalSavingIntentReceive.getPrice()- Double.parseDouble(currentSaving.getText().toString());
                    int  numOfDayWithSaving = (int) (withCurrentSaving / dailySavingDouble);
                    if(withCurrentSaving % dailySavingDouble > 0){
                        numOfDayWithSaving ++;
                    }

                    int numOfDayWithoutSaving = (int) (goalSavingIntentReceive.getPrice() / dailySavingDouble);
                    if (goalSavingIntentReceive.getPrice() % dailySavingDouble > 0){
                        numOfDayWithoutSaving++;
                    }


                    final LinearLayout linearLayout = findViewById(R.id.resultOfNumberOfDay);


                    linearLayout.removeAllViews();

                    RelativeLayout.LayoutParams layoutParamsBiggestTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsBiggestTitle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParamsBiggestTitle.setMargins(35, 20, 0, 50);

                    RelativeLayout.LayoutParams layoutParamsSecondTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsSecondTitle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    layoutParamsSecondTitle.setMargins(35, 20, 0, 40);

                    RelativeLayout.LayoutParams layoutParamsFouthTitle = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    layoutParamsFouthTitle.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                    layoutParamsFouthTitle.setMargins(35, 20, 0, 20);

                    TextView tvResult = new TextView(getApplication());
                    tvResult.setLayoutParams(layoutParamsBiggestTitle);
                    tvResult.setText("Result: ");
                    tvResult.setTextSize(24);
                    tvResult.setTextColor(Color.BLACK);
                    tvResult.setTypeface(tvResult.getTypeface(), Typeface.BOLD_ITALIC);

                    TextView tvWithSavingTitle = new TextView(getApplication());
                    tvWithSavingTitle.setLayoutParams(layoutParamsSecondTitle);
                    tvWithSavingTitle.setText("With Current Saving Balance ");
                    tvWithSavingTitle.setTextSize(22);
                    tvWithSavingTitle.setTextColor(Color.BLACK);
                    tvWithSavingTitle.setTypeface(tvWithSavingTitle.getTypeface(), Typeface.BOLD_ITALIC);

                    TextView tvWithSaving = new TextView(getApplication());
                    tvWithSaving.setLayoutParams(layoutParamsFouthTitle);
                    tvWithSaving.setText(String.format("%d days", numOfDayWithSaving));
                    tvWithSaving.setTextSize(18);
                    tvWithSaving.setTextColor(Color.GRAY);


                    TextView tvWithoutSavingTitle = new TextView(getApplication());
                    tvWithoutSavingTitle.setLayoutParams(layoutParamsSecondTitle);
                    tvWithoutSavingTitle.setText("Without Current Saving Balance ");
                    tvWithoutSavingTitle.setTextSize(22);
                    tvWithoutSavingTitle.setTextColor(Color.BLACK);
                    tvWithoutSavingTitle.setTypeface(tvWithoutSavingTitle.getTypeface(), Typeface.BOLD_ITALIC);

                    TextView tvWithoutSaving = new TextView(getApplication());
                    tvWithoutSaving.setLayoutParams(layoutParamsFouthTitle);
                    tvWithoutSaving.setText(String.format("%d days", numOfDayWithoutSaving));
                    tvWithoutSaving.setTextSize(18);
                    tvWithoutSaving.setTextColor(Color.GRAY);


                    linearLayout.addView(tvResult);
                    linearLayout.addView(tvWithSavingTitle);
                    linearLayout.addView(tvWithSaving);
                    linearLayout.addView(tvWithoutSavingTitle);
                    linearLayout.addView(tvWithoutSaving);


                }
                else{
                    Toast.makeText(GoalSavingDetails.this, "Please enter the daily Saving", Toast.LENGTH_SHORT).show();
                }


            }
        });



    }
}
