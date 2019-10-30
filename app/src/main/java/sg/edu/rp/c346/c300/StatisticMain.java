package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

public class StatisticMain extends AppCompatActivity {

    MaterialBetterSpinner spinnerDailyMonthly;
    MaterialBetterSpinner spinnerType;

    String dailyOrMonth="";
    String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_main);



        spinnerDailyMonthly = findViewById(R.id.spinnerDailyMonthly);
        spinnerType = findViewById(R.id.spinnerType);

        final ArrayList<String> dailyMonthlyList = new ArrayList<>();
        dailyMonthlyList.add("Daily");
        dailyMonthlyList.add("Monthly");

        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("Saving");
        typeList.add("Spending");
        typeList.add("Charity");

        ArrayAdapter dailyMonthlyListAdapter = new ArrayAdapter<String>(StatisticMain.this, android.R.layout.simple_list_item_1,dailyMonthlyList);
        spinnerDailyMonthly.setAdapter(dailyMonthlyListAdapter);

        ArrayAdapter typeListAdapter = new ArrayAdapter<String>(StatisticMain.this, android.R.layout.simple_list_item_1,typeList);
        spinnerType.setAdapter(typeListAdapter);


        spinnerDailyMonthly.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (position==0){
                    dailyOrMonth= "daily";
                }
                else if (position==1){
                    dailyOrMonth = "month";
                }
            }
        });


        spinnerType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    type = "saving";
                }
                else if (position==1){
                    type = "spending";
                }
                else{
                    type = "charity";
                }
            }
        });


        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if (dailyOrMonth.equals("daily")){

                    Intent intent = new Intent(StatisticMain.this, StatisticDaily.class);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
                else if (dailyOrMonth.equals("month")){
                    Intent intent1 = new Intent(StatisticMain.this, StatisticMonthly.class);
                    intent1.putExtra("type", type);
                    startActivity(intent1);
                }





            }
        });



    }
}
