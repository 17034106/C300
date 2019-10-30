package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ParentCategorizationLimit extends AppCompatActivity {

    private Button xDefault,xChange;

    private Switch allowMultipleChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_categorization_limit);



        xDefault = (Button) findViewById(R.id.DefaultBtn);
        xChange = (Button) findViewById(R.id.ChangeBtn);

        //region Check whether the parent allows the kid to change multiple times once the kid has decided his categorization
        allowMultipleChanges = findViewById(R.id.allowMultipleChange);

        allowMultipleChanges.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                DatabaseReference drAllowMultipleChange = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("allowToChange");

                if (isChecked){
                    drAllowMultipleChange.setValue(true);

                }
                else{
                    drAllowMultipleChange.setValue(false);
                }


            }
        });

        //endregion


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String kidUID = user.getUid();

//        final ArrayList<String> days = new ArrayList<>();
//        DatabaseReference dbAccessbudget = FirebaseDatabase.getInstance().getReference().child("budget").child(kidUID);
//        dbAccessbudget.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                int intNumOfDays = Integer.parseInt(dataSnapshot.child("day").child("numOfDays").getValue().toString());
//                System.out.println("Num Of Days: " + intNumOfDays);
//                for(int i =0 ;i < intNumOfDays;i++){
//                    days.add(dataSnapshot.child("day").child(String.valueOf(i)).child("day").getValue().toString());
//                }
//                for(int b = 0; b < days.size();b++){
//                    System.out.println("Days: " + days.get(b));
//                }
//                System.out.println("Days ArrayList Size: " + days.size());
//
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });




        xDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                 startActivity(new Intent(ParentLimit.this, Days.class).putExtra("Choice", "Default"));
                AlertDialog.Builder daysDialog = new AlertDialog.Builder(ParentCategorizationLimit.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_spinner,null);
                daysDialog.setTitle("Choose a day");
                final Spinner daySpinner = (Spinner)dialogView.findViewById(R.id.daysSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ParentCategorizationLimit.this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.days));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                daySpinner.setAdapter(adapter);



                daysDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Calendar calendar = Calendar.getInstance();
                        int day1 = calendar.get(Calendar.DAY_OF_WEEK)-2;

                        if (day1==-1){
                            day1 = 6;
                        }

                        if (daySpinner.getSelectedItemPosition() != day1) {

                            Toast.makeText(ParentCategorizationLimit.this, daySpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ParentCategorizationLimit.this, DayDefault.class);
                            intent.putExtra("daySelected", String.valueOf(daySpinner.getSelectedItemPosition())); //string
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(ParentCategorizationLimit.this, "Unable to edit for today", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                daysDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                daysDialog.setView(dialogView);
                daysDialog.show();
            }
        });

        xChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(ParentLimit.this, Days.class).putExtra("Choice", "Change"));
                AlertDialog.Builder daysDialog = new AlertDialog.Builder(ParentCategorizationLimit.this);
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_spinner,null);
                daysDialog.setTitle("Choose a day");
                final Spinner daySpinner = (Spinner)dialogView.findViewById(R.id.daysSpinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(ParentCategorizationLimit.this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.days));
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                daySpinner.setAdapter(adapter);





                daysDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Calendar calendar = Calendar.getInstance();
                        int day1 = calendar.get(Calendar.DAY_OF_WEEK)-2;

                        if (day1==-1){
                            day1 = 6;
                        }

                        int day2 = day1-1;
                        if (day2==-1){
                            day2=6;
                        }

                        if (daySpinner.getSelectedItemPosition() != day1  && daySpinner.getSelectedItemPosition() != day2) {


//                          Toast.makeText(ParentLimit.this, daySpinner.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ParentCategorizationLimit.this, DayChange.class);
                            intent.putExtra("daySelected", String.valueOf(daySpinner.getSelectedItemPosition())); //string
                            startActivity(intent);

                        }
                        else{
                            Toast.makeText(ParentCategorizationLimit.this, "Unable to edit for today or yesterday", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                daysDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                daysDialog.setView(dialogView);
                daysDialog.show();

            }
        });



    }
}
