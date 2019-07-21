package sg.edu.rp.c346.c300;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Arrays;

import sg.edu.rp.c346.c300.adapter.EWalletAdapter;
import sg.edu.rp.c346.c300.model.EWallet;

public class EmergencyWalletNotificationMain extends AppCompatActivity {

    MaterialBetterSpinner typeSpinner;
    ListView listViewEWallet;

    ArrayList<EWallet> acceptedRequest = new ArrayList<>();
    ArrayList<EWallet> rejectedRequest = new ArrayList<>();
    ArrayAdapter<String> typeAdapter;

    EWalletAdapter eWalletAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_wallet_notification_main);

        typeSpinner = findViewById(R.id.typeSpinner);
        listViewEWallet = findViewById(R.id.listViewEWallet);
        typeSpinner.setText("Request Type");








        ArrayList<String> typeList = new ArrayList<>();
        typeList.add("Accepted");
        typeList.add("Rejected");

        typeAdapter = new ArrayAdapter<String>(EmergencyWalletNotificationMain.this, android.R.layout.simple_list_item_1,typeList);
        typeSpinner.setAdapter(typeAdapter);

        typeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){

                    eWalletAdapter = new EWalletAdapter(EmergencyWalletNotificationMain.this, acceptedRequest);

                }
                else{
                    eWalletAdapter = new EWalletAdapter(EmergencyWalletNotificationMain.this, rejectedRequest);

                }

                listViewEWallet.setAdapter(eWalletAdapter);


            }
        });




        DatabaseReference drEWallet = FirebaseDatabase.getInstance().getReference().child("eWalletRequestCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drEWallet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //region for accepted request
                int numOfRequestAccepted = Integer.parseInt(dataSnapshot.child("accepted").child("numOfRequest").getValue().toString());

                for (int i =numOfRequestAccepted-1; i>=0;i--){
                    acceptedRequest.add(dataSnapshot.child("accepted").child(i+"").getValue(EWallet.class));
                }

                //endregion


                //region for rejected request

                int numOfRequestRejected = Integer.parseInt(dataSnapshot.child("rejected").child("numOfRequest").getValue().toString());

                for (int i =numOfRequestRejected-1; i>=0;i--){
                    rejectedRequest.add(dataSnapshot.child("rejected").child(i+"").getValue(EWallet.class));
                }

                //endregion

                Log.d("Size","What is the size for accepted: "+acceptedRequest.size());
                Log.d("Size","What is the size for rejected: "+rejectedRequest.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
