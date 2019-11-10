package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.edu.rp.c346.c300.adapter.EReciptAdapter;
import sg.edu.rp.c346.c300.adapter.GoalSavingAdapter;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.ItemOutside;
import sg.edu.rp.c346.c300.model.OutsideSendOrder;

public class HistoryTransactionQRPayment extends AppCompatActivity {

    TextView noNotification;

    ArrayList<OutsideSendOrder> outsideSendOrderList = new ArrayList<>();
    ListView listViewQRPayment;
    EReciptAdapter eReciptAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_transaction_qrpayment);

        listViewQRPayment = findViewById(R.id.listViewQRPayment);
        noNotification = findViewById(R.id.noNotification);

        DatabaseReference drORPaymentTransaction = FirebaseDatabase.getInstance().getReference().child("hc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("outside");
        drORPaymentTransaction.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                outsideSendOrderList.clear();
                int numOfOutside = Integer.parseInt(dataSnapshot.child("numOfOutside").getValue().toString());

                for (int i = numOfOutside-1; i>-1;i--){
                    boolean isItFood = Boolean.parseBoolean(dataSnapshot.child(i+"").child("ISITFOOD").getValue().toString());
                    String customerUID = dataSnapshot.child(i+"").child("customerUID").getValue().toString();
                    String dateTimeOrder = dataSnapshot.child(i+"").child("dateTimeOrder").getValue().toString();
                    String school = dataSnapshot.child(i+"").child("school").getValue().toString();
                    int stallId = Integer.parseInt(dataSnapshot.child(i+"").child("stallId").getValue().toString());
                    String stallName = dataSnapshot.child(i+"").child("stallName").getValue().toString();
                    String stallUID = dataSnapshot.child(i+"").child("stallUID").getValue().toString();
                    String tId = dataSnapshot.child(i+"").child("tId").getValue().toString();
                    double totalPrice = Double.parseDouble(dataSnapshot.child(i+"").child("totalPrice").getValue().toString());

                    ArrayList<ItemOutside> itemOutsideList = new ArrayList<>();
                    int numOfItem = Integer.parseInt(dataSnapshot.child(i+"").child("item").child("numOfItem").getValue().toString());
                    for (int h =0; h<numOfItem;h++){
                        itemOutsideList.add(dataSnapshot.child(i+"").child("item").child(h+"").getValue(ItemOutside.class));
                    }

                    outsideSendOrderList.add(new OutsideSendOrder(itemOutsideList, stallName, stallId, stallUID, customerUID, school, tId, dateTimeOrder, totalPrice, isItFood));

                }

                if (numOfOutside==0){
                    noNotification.setVisibility(View.VISIBLE);
                }
                else{
                    noNotification.setVisibility(View.INVISIBLE);
                }


                eReciptAdapter = new EReciptAdapter(HistoryTransactionQRPayment.this, outsideSendOrderList);
                listViewQRPayment.setAdapter(eReciptAdapter);
                eReciptAdapter.notifyDataSetChanged();

                if(getIntent().getBooleanExtra("parent",false)){ // if parent are the one who seeing the transaction history
                    listViewQRPayment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which){
                                        case DialogInterface.BUTTON_POSITIVE:
                                            setDeny(outsideSendOrderList.get(position));
                                            break;

                                        case DialogInterface.BUTTON_NEGATIVE:
                                            //No button clicked
                                            break;
                                    }
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(HistoryTransactionQRPayment.this);
                            builder.setMessage("Are you sure to deny kid to purchase any item from this stall?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }


    private void setDeny(final OutsideSendOrder item){
        final DatabaseReference drDenyList = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school").child(item.getSchool());
        drDenyList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfDeny = Integer.parseInt(dataSnapshot.child("numOfDeny").getValue().toString());

                for (int i =0; i<numOfDeny;i++){
                    if (Integer.parseInt(dataSnapshot.child(i+"").getValue().toString()) == (item.getStallID())){
                        Toast.makeText(HistoryTransactionQRPayment.this, "Already Denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                drDenyList.child(numOfDeny+"").setValue(item.getStallID());
                drDenyList.child("numOfDeny").setValue(numOfDeny+1);
                Toast.makeText(HistoryTransactionQRPayment.this, "Denied Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
