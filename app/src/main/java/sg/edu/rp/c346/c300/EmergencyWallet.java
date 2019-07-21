package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.adapter.EWalletAdapter;
import sg.edu.rp.c346.c300.adapter.GoalSavingAdapter;
import sg.edu.rp.c346.c300.model.EWallet;
import sg.edu.rp.c346.c300.model.GoalSaving;

public class EmergencyWallet extends AppCompatActivity {

    RelativeLayout btnEWalletBiggest;
    TextView tvEWalletRequest;
    ListView listViewEWallet;

    ArrayList<EWallet>  eWalletList = new ArrayList<>();
    EWalletAdapter eWalletAdapter;

    ArrayList<String> allCategoryList = new ArrayList<>();


    int numOfRequestPending;

    public static EmergencyWallet thisActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_wallet);

        thisActivity = this;


        btnEWalletBiggest = findViewById(R.id.btnEWalletBiggest);
        tvEWalletRequest = findViewById(R.id.tvEWalletRequest);
        listViewEWallet = findViewById(R.id.listViewEWallet);



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


        eWalletAdapter = new EWalletAdapter(EmergencyWallet.this, eWalletList);
        listViewEWallet.setAdapter(eWalletAdapter);

        listViewEWallet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });




        btnEWalletBiggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numOfRequestPending<3){
                    Intent intent = new Intent(EmergencyWallet.this, EmergencyWalletAdd.class);
                    intent.putStringArrayListExtra("allCategoryList", allCategoryList);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(EmergencyWallet.this, "Maximum 3 requests at a time", Toast.LENGTH_SHORT).show();
                }

            }
        });






        //region retrieve all the category for EmergencyWalletAdd class
        FirebaseDatabase.getInstance().getReference().child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfCategory = Integer.parseInt(dataSnapshot.child("numOfCategory").getValue().toString());
                for (int i=0; i<numOfCategory; i++){
                    allCategoryList.add(dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion

    }

    public static EmergencyWallet getInstance(){
        return thisActivity;
    }

}
