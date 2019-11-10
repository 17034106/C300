package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class HistoryTransactionMainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction_main_page);


        findViewById(R.id.notificationPreOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryTransactionMainPage.this, HistoryTransactionPreOrder.class);
                intent.putExtra("page","PreOrder");
                startActivity(intent);
            }
        });


        findViewById(R.id.notificationWalkIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryTransactionMainPage.this, HistoryTransactionPreOrder.class);
                intent.putExtra("page","WalkIn");
                startActivity(intent);
            }
        });


        findViewById(R.id.notificationOutsideTitle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryTransactionMainPage.this, HistoryTransactionQRPayment.class);
                intent.putExtra("parent", false);
                startActivity(intent);
            }
        });


    }
}
