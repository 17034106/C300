package sg.edu.rp.c346.c300;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class StatisticMonthly extends AppCompatActivity {

    ArrayList<String> xAxesMonth = new ArrayList<>();
    ArrayList<Double> yAxesMonthCharityAmt = new ArrayList<>();
    ArrayList<String> yearList = new ArrayList<>();

    SpinnerDialog spinnerDialog;

    int numOfYears;

    String type; //get the type (saving, spending, charity) from StatisticMain class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_monthly);


        type = getIntent().getStringExtra("type");//get the type (saving, spending, charity) from StatisticMain class
        Toast.makeText(this, type, Toast.LENGTH_SHORT).show();

        //Line Graph
        final LineChart lineChart = findViewById(R.id.monthgraph);
        lineChart.setNoDataText("Year and Month has not selected");

        //Button
        Button generateGraph = findViewById(R.id.Generate_Graph);

        //Text View
        final TextView monthlyTV = findViewById(R.id.tvMonthlyProfit);
        monthlyTV.setVisibility(View.INVISIBLE);

        //Get Current Date and set into the Calender
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        final int CalenderNextMonth = cal.get(Calendar.MONTH) + 1;

        // Get the UID from .getCurrentUser which logs in already
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Log.d("Profit.Java", "-----What is USERUID: " + userUID);

        //Access to Graph
        final DatabaseReference dbAccessProfit = FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(userUID).child(type).child("year");
        dbAccessProfit.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("------------Pass");
                numOfYears = Integer.parseInt(dataSnapshot.child("numOfYears").getValue().toString());
                Log.d("GraphProfitsMonth.java", "-----What is number of Years: " + numOfYears);
                //Place all the Years in the ArrayList
                for (int i = 0; i < numOfYears; i++) {
                    yearList.add(dataSnapshot.child(String.valueOf(i)).child("year").getValue().toString());
                    Log.d("GraphProfitsMonth.java", "------------What is Year added into the Spinner: " +
                            dataSnapshot.child(String.valueOf(i)).child("year").getValue().toString());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Create a Spinner Dialog(Activity,ArrayList,DialogTitle)
        spinnerDialog = new SpinnerDialog(StatisticMonthly.this, yearList, "Select Year");

        //Spinner will listen if there's any item being clicked
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(final String year, int position) {

                monthlyTV.setText(year + " Monthly Profit");
                monthlyTV.setVisibility(View.VISIBLE);
                xAxesMonth.clear();
                yAxesMonthCharityAmt.clear();

                //Access into the Year then the Month Firebase
                final DatabaseReference dbAccessProfitYearlyMonthly = FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(userUID).child(type).child("year").child(String.valueOf(position)).child("months");
                dbAccessProfitYearlyMonthly.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int numOfMonths = Integer.parseInt(dataSnapshot.child("numOfMonths").getValue().toString());
                        Log.d("GraphProfitsMonth.java", "-----What is number of months: " + numOfMonths);
                        for (int i = 0; i < CalenderNextMonth; i++) {
                            //Add all the Firebase's Month into the Month ArrayList
                            xAxesMonth.add(dataSnapshot.child(String.valueOf(i)).child("monthName").getValue().toString());
                            //Add all the Firebase's Month's Profit into the Month ArrayList
                            yAxesMonthCharityAmt.add(Double.parseDouble(dataSnapshot.child(String.valueOf(i)).child(type+"s").getValue().toString()));

//                                Log.d("GraphProfitsMonth.java", "------------What is Month: " + dataSnapshot.child(String.valueOf(i)).child("monthName").getValue().toString()
//                                        + "   What is Profit: " + Double.parseDouble(dataSnapshot.child(String.valueOf(i)).child("profit").getValue().toString()));
                        }

//                            Log.d("GraphProfitsMonth.java", "----*------Tell me the xAxesMonth Size: " + xAxesMonth.size());
//                            Log.d("GraphProfitsMonth.java", "----------Tell me the yAxesMonthCharityAmt Size: " + yAxesMonthCharityAmt.size());

//                            for (int a = 0; a < numOfMonths; a++) {
//                                Log.d("GraphProfitsMonth.java", "--------Values in xAxesMonth: " + xAxesMonth.get(a));
//                                Log.d("GraphProfitsMonth.java", "--------Values in yAxesMonthCharityAmt: " + yAxesMonthCharityAmt.get(a));
//                            }

                        //Put the X-axes and Y-axes in an arrayList Object Entry
                        ArrayList<Entry> MonthlyValues = new ArrayList<Entry>();
                        for (int i = 0; i < CalenderNextMonth; i++) {
                            float f = yAxesMonthCharityAmt.get(i).floatValue();
                            MonthlyValues.add(new Entry(i, f));
                        }

                        //Create a LINE that will contains the X-Values and Y-Values by placing the ArrayList MonthlyValues into here
                        LineDataSet MonthlySet = new LineDataSet(MonthlyValues, xAxesMonth.get(0) + " - " + xAxesMonth.get(xAxesMonth.size() - 1));
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
                        lineChartLeftYAxis.setTextSize(15);
                        lineChartLeftYAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                return String.format("$%.2f", value);
                            }
                        });
                        lineChartLeftYAxis.setAxisMinimum(0);

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
                                    if (value <= xAxesMonth.size() - 1) {
                                        return xAxesMonth.get((int) value);
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


                        //Description
                        Description description = new Description();
                        description.setText(year + " Monthly "+type);
                        description.setTextSize(15);
                        description.setTextColor(Color.rgb(1, 69, 241));
                        lineChart.setDescription(description);
                        lineChart.invalidate();


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

                spinnerDialog.showSpinerDialog();
            }
        });



    }
}
