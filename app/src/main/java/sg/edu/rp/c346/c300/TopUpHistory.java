package sg.edu.rp.c346.c300;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.adapter.EWalletAdapter;
import sg.edu.rp.c346.c300.adapter.TopUpAdapter;
import sg.edu.rp.c346.c300.model.CreditCard;
import sg.edu.rp.c346.c300.model.EWallet;

public class TopUpHistory extends AppCompatActivity {

    ListView listViewTopUp;

    ArrayList<CreditCard> topUpList = new ArrayList<>();
    TopUpAdapter topUpAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_history);

        listViewTopUp = findViewById(R.id.listViewTopUp);


        DatabaseReference drCreditCardCustomer = FirebaseDatabase.getInstance().getReference().child("creditCardCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drCreditCardCustomer.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfTopUp = Integer.parseInt(dataSnapshot.child("numOfTopUp").getValue().toString());

                topUpList.clear();

                for (int i =numOfTopUp-1; i>-1; i--){
                    topUpList.add(dataSnapshot.child(i+"").getValue(CreditCard.class));
                }

                topUpAdapter = new TopUpAdapter(TopUpHistory.this, topUpList);
                listViewTopUp.setAdapter(topUpAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }
}
