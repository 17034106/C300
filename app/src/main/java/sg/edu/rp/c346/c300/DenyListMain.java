package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.adapter.DenyListAdapter;
import sg.edu.rp.c346.c300.adapter.EWalletAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.Food;

public class DenyListMain extends AppCompatActivity {

    MaterialBetterSpinner locationSpinner;

    ArrayList<Integer> jurongPointDenyList = MainpageActivity.jurongPointDenyList;
    ArrayList<Integer> republicPolytechnicDenyList = MainpageActivity.republicPolytechnicDenyList;
    ArrayList<Integer> singaporePolytechnicDenyList = MainpageActivity.singaporePolytechnicDenyList;

    ArrayList<String> jurongPointDenyListDetails = new ArrayList<>();
    ArrayList<String> republicPolytechnicDenyListDetails = new ArrayList<>();
    ArrayList<String> singaporePolytechnicDenyListDetails = new ArrayList<>();

    DenyListAdapter denyListAdapter;

    TextView displayDeny;

    ListView listViewDeny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deny_list_main);

        getDetails();

        displayDeny = findViewById(R.id.DisplayDeny);
        listViewDeny = findViewById(R.id.listViewDeny);

        locationSpinner = findViewById(R.id.locationSpinner);
        ArrayList<String> locationList = new ArrayList<>();
        locationList.add("Jurong Point");
        locationList.add("Republic Polytechnic");
        locationList.add("Singapore Polytechnic");

        ArrayAdapter locationAdapter = new ArrayAdapter<String>(DenyListMain.this, android.R.layout.simple_list_item_1,locationList);
        locationSpinner.setAdapter(locationAdapter);


        locationSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){

                    denyListAdapter = new DenyListAdapter(DenyListMain.this, jurongPointDenyList, jurongPointDenyListDetails, "Jurong Point");

                    if (jurongPointDenyList.size()==0){
                        displayDeny.setVisibility(View.VISIBLE);
                        displayDeny.setText("No Denied Stall");
                    }
                    else{
                        displayDeny.setVisibility(View.INVISIBLE);
                    }


                    listViewDeny.setAdapter(denyListAdapter);

                    if (getIntent().getBooleanExtra("parent",false)){//check whether the user is parent. if yes can un-deny
                        listViewDeny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                unDeny("Jurong Point", position);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(DenyListMain.this);
                                builder.setMessage("Remove deny for "+jurongPointDenyListDetails.get(position)+"?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();

                            }
                        });
                    }

                }
                else if (position==1){
                    denyListAdapter = new DenyListAdapter(DenyListMain.this, republicPolytechnicDenyList, republicPolytechnicDenyListDetails, "Republic Polytechnic");

                    if (republicPolytechnicDenyList.size()==0){
                        displayDeny.setVisibility(View.VISIBLE);
                        displayDeny.setText("No Denied Stall");
                    }
                    else{
                        displayDeny.setVisibility(View.INVISIBLE);
                    }

                    listViewDeny.setAdapter(denyListAdapter);

                    if (getIntent().getBooleanExtra("parent",false)){//check whether the user is parent. if yes can un-deny
                        listViewDeny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                unDeny("Republic Polytechnic", position);
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(DenyListMain.this);
                                builder.setMessage("Remove deny for "+republicPolytechnicDenyListDetails.get(position)+"?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();



                            }
                        });
                    }


                }
                else{
                    denyListAdapter = new DenyListAdapter(DenyListMain.this, singaporePolytechnicDenyList, singaporePolytechnicDenyListDetails, "Singapore Polytechnic");

                    if (singaporePolytechnicDenyList.size()==0){
                        displayDeny.setVisibility(View.VISIBLE);
                        displayDeny.setText("No Denied Stall");
                    }
                    else{
                        displayDeny.setVisibility(View.INVISIBLE);
                    }

                    listViewDeny.setAdapter(denyListAdapter);


                    if (getIntent().getBooleanExtra("parent",false)){ //check whether the user is parent. if yes can un-deny
                        listViewDeny.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:

                                                unDeny("Singapore Polytechnic", position);

                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(DenyListMain.this);
                                builder.setMessage("Remove deny for "+singaporePolytechnicDenyListDetails.get(position)+"?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();



                            }
                        });
                    }


                }




            }
        });




    }





    private void getDetails(){

        DatabaseReference drMenu = FirebaseDatabase.getInstance().getReference().child("menu").child("school");


            drMenu.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (jurongPointDenyList.size()!=0) {
                        for (int i : jurongPointDenyList) {
                            jurongPointDenyListDetails.add(dataSnapshot.child("Jurong Point").child("stall").child(i + "").child("stallName").getValue().toString());
                        }
                    }

                    if (republicPolytechnicDenyList.size()!=0){
                        for (int i : republicPolytechnicDenyList) {
                            republicPolytechnicDenyListDetails.add(dataSnapshot.child("Republic Polytechnic").child("stall").child(i + "").child("stallName").getValue().toString());
                        }
                    }


                    if (singaporePolytechnicDenyList.size()!=0){
                        for (int i : singaporePolytechnicDenyList) {
                            singaporePolytechnicDenyListDetails.add(dataSnapshot.child("Singapore Polytechnic").child("stall").child(i + "").child("stallName").getValue().toString());
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }


    public void unDeny(final String location, final int position){
        final DatabaseReference drDeny = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school");
        drDeny.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int num = Integer.parseInt(dataSnapshot.child(location).child("numOfDeny").getValue().toString());

                drDeny.child(location).child("numOfDeny").setValue(num-1);

                for (int i =position; i<num-1;i++){
                    drDeny.child(location).child(position+"").setValue(Integer.parseInt(dataSnapshot.child(location).child((position+1)+"").getValue().toString()));
                }
                drDeny.child(location).child((num-1)+"").removeValue();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Toast.makeText(this, "Remove Deny Successfully", Toast.LENGTH_SHORT).show();
        finish();

    }





}
