package sg.edu.rp.c346.c300;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class StatisticDaily extends AppCompatActivity {

    ArrayList<String> xAxesDates = new ArrayList<>();
    ArrayList<Double> yAxesDatesProfit = new ArrayList<>();

    SpinnerDialog spinnerDialogDates;

    int numOfYears;
    int numOfMonths;

    int fbNumOfDates;
    String year, month;


    String type; //get the type (saving, spending, charity) from StatisticMain class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_daily);

        type = getIntent().getStringExtra("type");//get the type (saving, spending, charity) from StatisticMain class
        Toast.makeText(this, type, Toast.LENGTH_SHORT).show();

        //Line Graph
        final LineChart lineChart = findViewById(R.id.datesGraph);
        lineChart.setNoDataText("Year has not selected");

        //Button
        Button generateGraph = findViewById(R.id.YMGenerate_Graph);

        //Text View
        final TextView DatesTextView = findViewById(R.id.tvDatesProfit);
        DatesTextView.setVisibility(View.INVISIBLE);

        //To store the year and months to display in the Spinner
        final ArrayList<String> yearMonthList = new ArrayList<>();


        // Get the UID from .getCurrentUser which logs in already
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUID = user.getUid();
//        Log.d("Profit.Java", "-----What is USERUID: " + userUID);

        //Get Current Date and set into the Calender
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int CalenderNextMonth = cal.get(Calendar.MONTH) + 1;
        //Get Current Month by formatting the calender with MMM
        final String CalenderThisMonth = new SimpleDateFormat("MMMM").format(cal.getTime());

        //Get the Day of the Current Month
        final int CalenderForThisMonthDay = cal.get(Calendar.DAY_OF_MONTH);


        //Access Graph to get the Year + Month
        final DatabaseReference dbAccessProfit = FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(userUID).child(type).child("year");
        dbAccessProfit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numOfYears = Integer.parseInt(dataSnapshot.child("numOfYears").getValue().toString());
                //Log.d("GraphProfitsMonth.java", "-----What is number of Years: " + numOfYears);

                for (int i = 0; i < numOfYears; i++) {
                    for (int x = 0; x < CalenderNextMonth; x++) {
                        //Add the Year + Month into the yearMonthList Arraylist for Spinner Value
                        yearMonthList.add(dataSnapshot.child(String.valueOf(i)).child("year").getValue().toString() + " "
                                + dataSnapshot.child(String.valueOf(i)).child("months").child(String.valueOf(x)).child("monthName").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Create a Spinner Dialog(Activity,ArrayList,DialogTitle)
        spinnerDialogDates = new SpinnerDialog(StatisticDaily.this, yearMonthList, "Select Year and Month");

        //       Spinner will listen if there's any item being clicked
        spinnerDialogDates.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String yearMonth, int position) {
                int yearMonthLength = yearMonth.length();
                year = yearMonth.substring(0, 4);
                month = yearMonth.substring(5, yearMonthLength);
                //Log.d("GraphProfitsMonthDate","---------YearMonthLength: " + yearMonthLength);
                //Log.d("GraphProfitsMonthDate","--------Year: " + year);
                //Log.d("GraphProfitsMonthDate","---------Month: " + month);

                DatesTextView.setText(yearMonth + " Daily Profit");
                DatesTextView.setVisibility(View.VISIBLE);
                xAxesDates.clear();
                yAxesDatesProfit.clear();

                final DatabaseReference dbAccessProfitYearlyMonthlyWeekly = FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(userUID).child(type).child("year");
                dbAccessProfitYearlyMonthlyWeekly.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //Log.d("GraphProfitsMonthDate","--------NumOfYears: " + numOfYears);
                        for (int a = 0; a < numOfYears; a++) {
                            String fbYear = dataSnapshot.child(String.valueOf(a)).child("year").getValue().toString();
                            //If Selected Spinner Value Year Match with fbYear variable
                            if (year.equals(fbYear)) {

                                int fbNumOfMonths = Integer.parseInt(dataSnapshot.child(String.valueOf(a)).child("months").child("numOfMonths").getValue().toString());
                                //Log.d("GraphProfitsMonthDate","--------NumOfMonths: " + fbNumOfMonths);

                                for (int b = 0; b < fbNumOfMonths; b++) {
                                    String fbMonth = dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b)).child("monthName").getValue().toString();

                                    //If Selected Spinner Value Month Match with fbMonth variable
                                    if (month.equals(fbMonth)) {

                                        fbNumOfDates = Integer.parseInt(dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b)).child("dates").child("numOfDays").getValue().toString());
                                        //Log.d("GraphProfitsMonthDate", "--------NumOfDays: " + fbNumOfDates);

                                        //If Selected Spinner Month does not Match with Current Month
                                        //It will add all the dates in the ArrayList
                                        if (!month.equals(CalenderThisMonth)) {
                                            for (int c = 0; c < fbNumOfDates; c++) {
                                                xAxesDates.add(dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b))
                                                        .child("dates").child(String.valueOf(c)).child("date").getValue().toString().substring(0, 2));
                                                yAxesDatesProfit.add(Double.parseDouble(dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b))
                                                        .child("dates").child(String.valueOf(c)).child(type+"s").getValue().toString()));
                                            }

                                        }
                                        //If Selected Spinner Month does Match with Current Month
                                        //It will only add all the dates before the Current date
                                        else {

                                            if (type.equalsIgnoreCase("saving") || type.equalsIgnoreCase("charity")){ //saving graph will not show the today's statistic

                                                for (int d = 0; d < CalenderForThisMonthDay-1; d++) {
                                                    xAxesDates.add(dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b))
                                                            .child("dates").child(String.valueOf(d)).child("date").getValue().toString().substring(0, 2));
                                                    yAxesDatesProfit.add(Double.parseDouble(dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b))
                                                            .child("dates").child(String.valueOf(d)).child(type+"s").getValue().toString()));

                                                }

                                            }
                                            else {

                                                for (int d = 0; d < CalenderForThisMonthDay; d++) {
                                                    xAxesDates.add(dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b))
                                                            .child("dates").child(String.valueOf(d)).child("date").getValue().toString().substring(0, 2));
                                                    yAxesDatesProfit.add(Double.parseDouble(dataSnapshot.child(String.valueOf(a)).child("months").child(String.valueOf(b))
                                                            .child("dates").child(String.valueOf(d)).child(type + "s").getValue().toString()));

                                                }
                                            }
                                        }
                                        //Get out of the for loop
                                        break;

                                    }
                                }
                            }
                        }
//                        for(int p = 0; p < xAxesDates.size(); p++){
//                            System.out.println("Dates: "+ xAxesDates.get(p));
//                            System.out.println("Profit of Week: "+ yAxesDatesProfit.get(p));
//                        }

                        //Put the X-axes and Y-axes in an arrayList Object Entry
                        ArrayList<Entry> MonthlyValues = new ArrayList<Entry>();
                        for (int i = 0; i < xAxesDates.size(); i++) {
                            float f = yAxesDatesProfit.get(i).floatValue();
                            MonthlyValues.add(new Entry(i, f));
                        }


                        //Create a LINE that will contains the X-Values and Y-Values by placing the ArrayList MonthlyValues into here
                        LineDataSet MonthlySet = new LineDataSet(MonthlyValues, "Date: " + xAxesDates.get(0) + month + " - " + "Date: " + xAxesDates.get(xAxesDates.size() - 1) + month);
                        MonthlySet.setColor(Color.GREEN);
                        MonthlySet.setValueTextSize(10);

                        //Change the format of the Line's data labels
                        MonthlySet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                return String.format("$%.2f", value);
                            }
                        });

                        //Create an ArrayList that will contain all the lines,
                        // and put the line created into the ArrayList
                        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                        dataSets.add(MonthlySet);

                        //Convert the ArrayList that contain all the Lines into Lines
                        LineData data = new LineData(dataSets);
                        lineChart.setData(data);
                        lineChart.setDrawGridBackground(true);
                        lineChart.setDrawBorders(true);

                        //Get Y-Axis on the Right of the graph
                        YAxis lineChartRightYAxis = lineChart.getAxisRight();
                        //Remove the Y-Axis on the right of the line graph
                        lineChartRightYAxis.setEnabled(false);

                        //Get Y-Axis on the Left of the graph
                        YAxis lineChartLeftYAxis = lineChart.getAxisLeft();
                        lineChartLeftYAxis.setAxisMinimum(0);
                        lineChartLeftYAxis.setTextSize(15);
                        lineChartLeftYAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                return String.format("$%.2f", value);
                            }
                        });

                        //Get X-Axis
                        XAxis lineChartXAxis = lineChart.getXAxis();
                        lineChartXAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        lineChartXAxis.setTextSize(10);
                        lineChartXAxis.setGranularityEnabled(true);
                        //Change the X-Axis labels
                        lineChartXAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                if (value >= 0) {
                                    if (value <= xAxesDates.size() - 1) {
                                        return xAxesDates.get((int) value);
                                    }
                                    return "";
                                }
                                return "";
                            }
                            //    ArrayList<String> XAxisLabel = new ArrayList<>();
//                                    XAxisLabel.clear();
//                                    for(int i = 0; i < xAxesMonth.size();i++){
//                                        XAxisLabel.add(xAxesMonth.get(i));
//                                    }
//                                    Log.d("graph","--------XLabelSize: " + XAxisLabel.size());
//                                    Log.d("graph","--------XAxesMonthSize: " + xAxesMonth.size());
//                                    return XAxisLabel.get((int)value);
                        });


                        float minXRange = 10;
                        float maxXRange = 10;
                        lineChart.setVisibleXRange(minXRange, maxXRange);
                        lineChart.invalidate();
//                        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//                            @Override
//                            public void onValueSelected(Entry e, Highlight h) {
//
//                            }
//
//                            @Override
//                            public void onNothingSelected() {
//
//                            }
//                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        generateGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialogDates.showSpinerDialog();
            }
        });



    }
}
