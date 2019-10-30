package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DayDefault extends AppCompatActivity {

    private EditText FMin, FMax, DMin, DMax, SMin, SMax, CMin, CMax, OMin, OMax, xAmount;
    private Button xChange;

    DatabaseReference xRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_default);

        FMin = (EditText) findViewById(R.id.MinFood);
        FMax = (EditText) findViewById(R.id.MaxFood);
        DMin = (EditText) findViewById(R.id.MinDrink);
        DMax = (EditText) findViewById(R.id.MaxDrink);
        SMin = (EditText) findViewById(R.id.MinStationery);
        SMax = (EditText) findViewById(R.id.MaxStationery);
        CMin = (EditText) findViewById(R.id.MinCharity);
        CMax = (EditText) findViewById(R.id.MaxCharity);
        OMin = (EditText) findViewById(R.id.MinOthers);
        OMax = (EditText) findViewById(R.id.MaxOthers);

        xAmount = (EditText) findViewById(R.id.amount);

        xChange = (Button) findViewById(R.id.changeDefault);

        final String dayOfWeek = getIntent().getStringExtra("Day");
        System.out.println("Intent: " + getIntent().getStringExtra("daySelected"));

        xRef = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day").child(getIntent().getStringExtra("daySelected"));
        xRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                String showFMin = dataSnapshot.child("category").child("food").child("defaultValueMin").getValue().toString();
                String showFMax = dataSnapshot.child("category").child("food").child("defaultValueMax").getValue().toString();
                String showDMin = dataSnapshot.child("category").child("drink").child("defaultValueMin").getValue().toString();
                String showDMax = dataSnapshot.child("category").child("drink").child("defaultValueMax").getValue().toString();
                String showSMin = dataSnapshot.child("category").child("stationery").child("defaultValueMin").getValue().toString();
                String showSMax = dataSnapshot.child("category").child("stationery").child("defaultValueMax").getValue().toString();
                String showCMin = dataSnapshot.child("category").child("charity").child("defaultValueMin").getValue().toString();
                String showCMax = dataSnapshot.child("category").child("charity").child("defaultValueMax").getValue().toString();
                String showOMin = dataSnapshot.child("category").child("others").child("defaultValueMin").getValue().toString();
                String showOMax = dataSnapshot.child("category").child("others").child("defaultValueMax").getValue().toString();
                String showAllow = dataSnapshot.child("allowance").getValue().toString();


                FMin.setText(showFMin);
                FMax.setText(showFMax);
                DMin.setText(showDMin);
                DMax.setText(showDMax);
                SMin.setText(showSMin);
                SMax.setText(showSMax);
                CMin.setText(showCMin);
                CMax.setText(showCMax);
                OMin.setText(showOMin);
                OMax.setText(showOMax);

                xAmount.setText(showAllow);


                xChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("What---", "What is foodMin"+FMin.getText().toString());
                        String FoodMin = FMin.getText().toString();
                        int foodmin = Integer.parseInt(FoodMin);
                        String FoodMax = FMax.getText().toString();
                        int foodmax = Integer.parseInt(FoodMax);

                        String DrinkMin = DMin.getText().toString();
                        int drinkmin = Integer.parseInt(DrinkMin);
                        String DrinkMax = DMax.getText().toString();
                        int drinkmax = Integer.parseInt(DrinkMax);

                        String StationMin = SMin.getText().toString();
                        int stationmin = Integer.parseInt(StationMin);
                        String StationMax = SMax.getText().toString();
                        int stationmax = Integer.parseInt(StationMax);

                        String CharityMin = CMin.getText().toString();
                        int charitymin = Integer.parseInt(CharityMin);
                        String CharityMax = CMax.getText().toString();
                        int charitymax = Integer.parseInt(CharityMax);

                        String OthersMin = OMin.getText().toString();
                        int othersmin = Integer.parseInt(OthersMin);
                        String OthersMax = OMax.getText().toString();
                        int othersmax = Integer.parseInt(OthersMax);

                        String AmountSet = xAmount.getText().toString();
                        int amountset = Integer.parseInt(AmountSet);

                        int totalMin = foodmin + drinkmin + stationmin + charitymin + othersmin;

                        if (totalMin > amountset){
                            Toast.makeText(DayDefault.this, "Please input amount lesser than the amount", Toast.LENGTH_SHORT).show();
                        }
                        else if (foodmin < 0 || drinkmin < 0 || stationmin < 0 || charitymin < 0 || othersmin < 0){
                            Toast.makeText(DayDefault.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                        }
                        else if(FoodMin.isEmpty() || FoodMax.isEmpty() || DrinkMin.isEmpty() || DrinkMax.isEmpty() || StationMin.isEmpty() || StationMax.isEmpty() || CharityMin.isEmpty() || CharityMax.isEmpty() || OthersMin.isEmpty() || OthersMax.isEmpty()){
                            Toast.makeText(DayDefault.this, "All field must be filled up", Toast.LENGTH_SHORT).show();
                        }
                        else if (foodmin >= foodmax || drinkmin >= drinkmax || stationmin >= stationmax || charitymin >= charitymax || othersmin >= othersmax){
                            Toast.makeText(DayDefault.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            xRef.child("category").child("food").child("defaultValueMin").setValue(foodmin);
                            xRef.child("category").child("food").child("defaultValueMax").setValue(foodmax);
                            xRef.child("category").child("drink").child("defaultValueMin").setValue(drinkmin);
                            xRef.child("category").child("drink").child("defaultValueMax").setValue(drinkmax);
                            xRef.child("category").child("stationery").child("defaultValueMin").setValue(stationmin);
                            xRef.child("category").child("stationery").child("defaultValueMax").setValue(stationmax);
                            xRef.child("category").child("charity").child("defaultValueMin").setValue(charitymin);
                            xRef.child("category").child("charity").child("defaultValueMax").setValue(charitymax);
                            xRef.child("category").child("others").child("defaultValueMin").setValue(othersmin);
                            xRef.child("category").child("others").child("defaultValueMax").setValue(othersmax);
                            xRef.child("allowance").setValue(amountset);

//                            startActivity(new Intent(DayDefault.this, Days.class));
                        }
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
