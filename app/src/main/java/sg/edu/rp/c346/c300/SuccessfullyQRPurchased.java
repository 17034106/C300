package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SuccessfullyQRPurchased extends AppCompatActivity {

    TextView tvStallName, tvDateTimeOrder, tvSchool, tvTotalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully_qrpurchased);


        tvStallName = findViewById(R.id.stall);
        tvDateTimeOrder = findViewById(R.id.dateTime);
        tvSchool = findViewById(R.id.location);
        tvTotalPrice = findViewById(R.id.totalPrice);


        String stallName = getIntent().getStringExtra("stallName");
        String dateTimeOrder = getIntent().getStringExtra("dateTimeOrder");
        String school = getIntent().getStringExtra("school");
        double totalPrice = getIntent().getDoubleExtra("totalPrice", -1);


        tvStallName.setText("Stall: "+stallName);
        tvDateTimeOrder.setText("Date: "+dateTimeOrder);
        tvSchool.setText("Location: "+school);
        tvTotalPrice.setText(String.format("Total Price: $%.2f", totalPrice));


        findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
