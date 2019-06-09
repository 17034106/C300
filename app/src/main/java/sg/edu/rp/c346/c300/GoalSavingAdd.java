package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GoalSavingAdd extends AppCompatActivity {

    EditText etGoalSavingAddName, etGoalSavingAddPrice;

    DatabaseReference drGoalSaving;
    int numOfGoal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_saving_add);


        //region  pop up window for select date and time
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.85), (int)(height*.5));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = 90;
//        params.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(params);
        //endregion


        etGoalSavingAddName = findViewById(R.id.etGoalAddName);
        etGoalSavingAddPrice = findViewById(R.id.etGoalAddPrice);


        drGoalSaving = FirebaseDatabase.getInstance().getReference().child("goalSaving").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drGoalSaving.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numOfGoal = Integer.parseInt(dataSnapshot.child("numOfGoal").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        findViewById(R.id.btnGoalAddConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String goalName = etGoalSavingAddName.getText().toString().trim();
                String goalPriceString = etGoalSavingAddPrice.getText().toString().trim();

                if (!goalName.isEmpty() && !goalPriceString.isEmpty()){

                    double goalPrice = Double.parseDouble(goalPriceString);

                    drGoalSaving.child(numOfGoal+"").child("name").setValue(goalName);
                    drGoalSaving.child(numOfGoal+"").child("price").setValue(goalPrice);


                    drGoalSaving.child("numOfGoal").setValue(numOfGoal+1);

                    GoalSavingAll.getInstance().finish();
                    Intent intent = new Intent(GoalSavingAdd.this, GoalSavingAll.class);
                    startActivity(intent);
                    finish();


                }
                else{
                    Toast.makeText(GoalSavingAdd.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                }




            }
        });






    }
}
