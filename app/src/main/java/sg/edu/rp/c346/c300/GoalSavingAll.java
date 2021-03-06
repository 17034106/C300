package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import sg.edu.rp.c346.c300.adapter.GoalSavingAdapter;
import sg.edu.rp.c346.c300.model.GoalSaving;

public class GoalSavingAll extends AppCompatActivity {

    public static GoalSavingAll thisActivity;

    DatabaseReference drGoalSaving;

    public static ArrayList<GoalSaving> goalSavingList = new ArrayList<>();

    TextView tvNumOfGoal;

    ListView listView;
    GoalSavingAdapter  goalSavingAdapter;

    double saving; // get how much the kid has save

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_saving_all);

        thisActivity = this;

        getSaving();

        listView = findViewById(R.id.goalSavingListView);

        tvNumOfGoal = findViewById(R.id.tvTotalGoalSaving);

        drGoalSaving = FirebaseDatabase.getInstance().getReference().child("goalSaving").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drGoalSaving.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfGoal = Integer.parseInt(dataSnapshot.child("numOfGoal").getValue().toString());

                tvNumOfGoal.setText("No. of Goal: "+numOfGoal);

                goalSavingList.clear();

                for (int i =0; i<numOfGoal;i++){

                    goalSavingList.add(dataSnapshot.child(i+"").getValue(GoalSaving.class));

                    if (goalSavingList.get(i).getPrice()<=saving){
                        drGoalSaving.child(i+"").child("status").setValue("Completed");
                        goalSavingList.get(i).setStatus("Completed");
                    }
                    else{
                        drGoalSaving.child(i+"").child("status").setValue("In progress");
                        goalSavingList.get(i).setStatus("In Progress");
                    }

                }

                goalSavingAdapter = new GoalSavingAdapter(GoalSavingAll.this, goalSavingList);
                listView.setAdapter(goalSavingAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        GoalSaving goalSaving = goalSavingList.get(position);


                        Intent intent = new Intent(GoalSavingAll.this, GoalSavingDetails.class);
                        intent.putExtra("goalSaving", goalSaving);
                        intent.putExtra("goalPosition", position);
                        intent.putExtra("saving", saving);
                        startActivity(intent);
                        finish();

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        findViewById(R.id.btnGoalSavingAddBiggest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GoalSavingAll.this, GoalSavingAdd.class);
                startActivity(intent);
            }
        });








    }

    //region get the customer's saving
    public void getSaving(){
        DatabaseReference drSaving = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("saving");
        drSaving.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                saving = Double.parseDouble(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //endregion


    public static GoalSavingAll getInstance(){
        return thisActivity;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
