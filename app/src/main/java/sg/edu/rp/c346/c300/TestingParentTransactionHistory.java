package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TestingParentTransactionHistory extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_transaction_history);

        findViewById(R.id.PreOrderHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(TestingParentTransactionHistory.this, TestingParentTransactionPreOrderAll.class);
                intent.putExtra("page","PreOrder");
                startActivity(intent);
            }
        });


        findViewById(R.id.walkInOrderHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(TestingParentTransactionHistory.this, TestingParentTransactionPreOrderAll.class);
                intent.putExtra("page","WalkIn");
                startActivity(intent);
            }
        });


        findViewById(R.id.QRPaymentHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentTransactionHistory.this, HistoryTransactionQRPayment.class);
                intent.putExtra("parent", true);
                startActivity(intent);
            }
        });


    }
}
