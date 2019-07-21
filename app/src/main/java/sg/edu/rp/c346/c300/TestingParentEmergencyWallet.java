package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.adapter.EWalletAdapter;
import sg.edu.rp.c346.c300.model.EWallet;

public class TestingParentEmergencyWallet extends AppCompatActivity {

    ListView parentEmergencyWalletRequest;
    TextView tvEWalletRequest;

    ArrayList<EWallet> eWalletList = new ArrayList<>();
    EWalletAdapter eWalletAdapter;

    int numOfRequestPending;

    static TestingParentEmergencyWallet thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_emergency_wallet);

        thisActivity = this;


        parentEmergencyWalletRequest = findViewById(R.id.parentEmergencyWalletRequest);
        tvEWalletRequest = findViewById(R.id.tvEWalletRequest);


        DatabaseReference drEmergencyWallet = FirebaseDatabase.getInstance().getReference().child("eWalletRequestCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("pending");
        drEmergencyWallet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numOfRequestPending = Integer.parseInt(dataSnapshot.child("numOfRequest").getValue().toString());

                tvEWalletRequest.setText("No. of Requests: "+numOfRequestPending);

                eWalletList.clear();

                for (int i =0; i<numOfRequestPending;i++){

                    EWallet eWallet = dataSnapshot.child(i+"").getValue(EWallet.class);

                    eWalletList.add(eWallet);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        eWalletAdapter = new EWalletAdapter(TestingParentEmergencyWallet.this, eWalletList);
        parentEmergencyWalletRequest.setAdapter(eWalletAdapter);

        parentEmergencyWalletRequest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(TestingParentEmergencyWallet.this, TestingParentEmergencyWalletAnswer.class);
                intent.putExtra("request", eWalletList.get(position));
                intent.putExtra("position", position);
                intent.putExtra("numOfRequestPending", numOfRequestPending);
                startActivity(intent);
            }
        });


    }

    public static TestingParentEmergencyWallet getInstance(){
        return thisActivity;
    }





}
