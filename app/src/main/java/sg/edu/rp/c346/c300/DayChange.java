package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DayChange extends AppCompatActivity {

    private EditText FMin, FMax, DMin, DMax, SMin, SMax, CMin, CMax, OMin, OMax, Amt;
    private CheckBox DefFood, DefDrink, DefStation, DefCharity, DefOthers, DefAmount;
    private Button xChange;

    DatabaseReference xRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_change);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String uid = user.getUid();

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
        Amt = (EditText) findViewById(R.id.amount);

        DefFood = (CheckBox) findViewById(R.id.defaultFood);
        DefDrink = (CheckBox) findViewById(R.id.defaultDrink);
        DefStation = (CheckBox) findViewById(R.id.defaultStationery);
        DefCharity = (CheckBox) findViewById(R.id.defaultCharity);
        DefOthers = (CheckBox) findViewById(R.id.defaultOthers);
        DefAmount = (CheckBox) findViewById(R.id.defaultAmount);


        xChange = (Button) findViewById(R.id.change);

        String dayOfWeek = getIntent().getStringExtra("Day");

        xRef = FirebaseDatabase.getInstance().getReference().child("budget").child(uid).child("day").child(getIntent().getStringExtra("daySelected"));
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
                Amt.setText(showAllow);



                final String allowance = dataSnapshot.child("allowance").getValue().toString();
                final int Allowance = Integer.parseInt(allowance);
                Amt.setText(Allowance+"");

                xChange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int totalMin = 0;

                        String FoodMin = FMin.getText().toString();
                        String FoodMax = FMax.getText().toString();

                        if(DefFood.isChecked() && FoodMin.isEmpty() && FoodMax.isEmpty() || DefFood.isChecked() && !FoodMin.isEmpty() && !FoodMax.isEmpty()){ ;

                            String DefFoodMin = dataSnapshot.child("category").child("food").child("defaultValueMin").getValue().toString();
                            int deffoodmin = Integer.parseInt(DefFoodMin);

                            FMin.getText().clear();
                            FMax.getText().clear();

                            totalMin = totalMin + deffoodmin;
                        }
                        else if (!DefFood.isChecked() && !FoodMin.isEmpty() && !FoodMax.isEmpty()){
                            int foodmin = Integer.parseInt(FoodMin);
                            int foodmax = Integer.parseInt(FoodMax);

                            if (foodmin < 0 || foodmax < 0){
                                Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                            }
                            else if (foodmin >= foodmax){
                                Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                totalMin = totalMin + foodmin;
                            }
                        }

                        String DrinkMin = DMin.getText().toString();
                        String DrinkMax = DMax.getText().toString();

                        if(DefDrink.isChecked() && DrinkMin.isEmpty() && DrinkMax.isEmpty() || DefDrink.isChecked() && !DrinkMin.isEmpty() && !DrinkMax.isEmpty()){

                            String DefDrinkMin = dataSnapshot.child("category").child("drink").child("defaultValueMin").getValue().toString();
                            int defdrinkmin = Integer.parseInt(DefDrinkMin);

                            DMin.getText().clear();
                            DMax.getText().clear();

                            totalMin = totalMin + defdrinkmin;
                        }
                        else if (!DefDrink.isChecked() && !DrinkMin.isEmpty() && !DrinkMax.isEmpty()){
                            int drinkmin = Integer.parseInt(DrinkMin);
                            int drinkmax = Integer.parseInt(DrinkMax);

                            if (drinkmin < 0 || drinkmax < 0){
                                Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                            }
                            else if (drinkmin >= drinkmax){
                                Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                totalMin = totalMin + drinkmin;
                            }
                        }




                        String StationMin = SMin.getText().toString();
                        String StationMax = SMax.getText().toString();

                        if(DefStation.isChecked() && StationMin.isEmpty() && StationMax.isEmpty() || DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){

                            String DefStationMin = dataSnapshot.child("category").child("stationery").child("defaultValueMin").getValue().toString();
                            int defstationmin = Integer.parseInt(DefStationMin);

                            SMin.getText().clear();
                            SMax.getText().clear();

                            totalMin = totalMin + defstationmin;
                        }
                        else if (!DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){
                            int stationmin = Integer.parseInt(StationMin);
                            int stationmax = Integer.parseInt(StationMax);

                            if (stationmin < 0 || stationmax < 0){
                                Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                            }
                            else if (stationmin >= stationmax){
                                Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                totalMin = totalMin + stationmin;
                            }
                        }




                        String CharityMin = CMin.getText().toString();
                        String CharityMax = CMax.getText().toString();

                        if(DefCharity.isChecked() && CharityMin.isEmpty() && CharityMax.isEmpty() || DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){

                            String DefCharityMin = dataSnapshot.child("category").child("charity").child("defaultValueMin").getValue().toString();
                            int defcharitymin = Integer.parseInt(DefCharityMin);

                            CMin.getText().clear();
                            CMax.getText().clear();

                            totalMin = totalMin + defcharitymin;
                        }
                        else if (!DefCharity.isChecked() && !CharityMin.isEmpty() && !CharityMax.isEmpty()){
                            int charitymin = Integer.parseInt(CharityMin);
                            int charitymax = Integer.parseInt(CharityMax);

                            if (charitymin < 0 || charitymax < 0){
                                Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                            }
                            else if (charitymin >= charitymax){
                                Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                totalMin = totalMin + charitymin;
                            }
                        }


                        String OthersMin = OMin.getText().toString();
                        String OthersMax = OMax.getText().toString();

                        if(DefOthers.isChecked() && OthersMin.isEmpty() && OthersMax.isEmpty() || DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){

                            String DefOthersMin = dataSnapshot.child("category").child("others").child("defaultValueMin").getValue().toString();
                            int defothersmin = Integer.parseInt(DefOthersMin);

                            CMin.getText().clear();
                            CMax.getText().clear();

                            totalMin = totalMin + defothersmin;
                        }
                        else if (!DefOthers.isChecked() && !OthersMin.isEmpty() && !OthersMax.isEmpty()){
                            int othersmin = Integer.parseInt(OthersMin);
                            int othersmax = Integer.parseInt(OthersMax);

                            if (othersmin < 0 || othersmax < 0){
                                Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                            }
                            else if (othersmin >= othersmax){
                                Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                            }
                            else{

                                totalMin = totalMin + othersmin;
                            }
                        }

                        if (totalMin > Allowance){
                            Toast.makeText(DayChange.this, "Please do not exceed the current amount", Toast.LENGTH_SHORT).show();
                        }else{

                            // ADD Food
                            if(DefFood.isChecked() && FoodMin.isEmpty() && FoodMax.isEmpty() || DefFood.isChecked() && !FoodMin.isEmpty() && !FoodMax.isEmpty()){ ;


                                FMin.getText().clear();
                                FMax.getText().clear();

                                xRef.child("category").child("food").child("changedValueMin").setValue(-1);
                                xRef.child("category").child("food").child("changedValueMax").setValue(-1);

                            }
                            else if (!DefFood.isChecked() && !FoodMin.isEmpty() && !FoodMax.isEmpty()){
                                int foodmin = Integer.parseInt(FoodMin);
                                int foodmax = Integer.parseInt(FoodMax);

                                if (foodmin < 0 || foodmax < 0){
                                    Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                                }
                                else if (foodmin >= foodmax){
                                    Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    xRef.child("category").child("food").child("changedValueMin").setValue(foodmin);
                                    xRef.child("category").child("food").child("changedValueMax").setValue(foodmax);
                                }
                            }
                            else{
                                Toast.makeText(DayChange.this, "Please input only 1 of the fields for each catergory", Toast.LENGTH_SHORT).show();
                            }


                            //ADD Drink
                            if(DefDrink.isChecked() && DrinkMin.isEmpty() && DrinkMax.isEmpty() || DefDrink.isChecked() && !DrinkMin.isEmpty() && !DrinkMax.isEmpty()){ ;

                                DMin.getText().clear();
                                DMax.getText().clear();

                                xRef.child("category").child("drink").child("changedValueMin").setValue(-1);
                                xRef.child("category").child("drink").child("changedValueMax").setValue(-1);

                            }
                            else if (!DefDrink.isChecked() && !DrinkMin.isEmpty() && !DrinkMax.isEmpty()){
                                int drinkmin = Integer.parseInt(DrinkMin);
                                int drinkmax = Integer.parseInt(DrinkMax);

                                if (drinkmin < 0 || drinkmax < 0){
                                    Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                                }
                                else if (drinkmin >= drinkmax){
                                    Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    xRef.child("category").child("drink").child("changedValueMin").setValue(drinkmin);
                                    xRef.child("category").child("drink").child("changedValueMax").setValue(drinkmax);
                                }
                            }
                            else{
                                Toast.makeText(DayChange.this, "Please input only 1 of the fields for each catergory", Toast.LENGTH_SHORT).show();
                            }


                            //ADD Stationery
                            if(DefStation.isChecked() && StationMin.isEmpty() && StationMax.isEmpty() || DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){

                                SMin.getText().clear();
                                SMax.getText().clear();

                                xRef.child("category").child("stationery").child("changedValueMin").setValue(-1);
                                xRef.child("category").child("stationery").child("changedValueMax").setValue(-1);
                            }
                            else if (!DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){
                                int stationmin = Integer.parseInt(StationMin);
                                int stationmax = Integer.parseInt(StationMax);

                                if (stationmin < 0 || stationmax < 0){
                                    Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                                }
                                else if (stationmin >= stationmax){
                                    Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    xRef.child("category").child("stationery").child("changedValueMin").setValue(stationmin);
                                    xRef.child("category").child("stationery").child("changedValueMax").setValue(stationmax);
                                }
                            }
                            else{
                                Toast.makeText(DayChange.this, "Please input only 1 of the fields for each catergory", Toast.LENGTH_SHORT).show();
                            }


                            //ADD Charity
                            if(DefCharity.isChecked() && CharityMin.isEmpty() && CharityMax.isEmpty() || DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){

                                CMin.getText().clear();
                                CMax.getText().clear();

                                xRef.child("category").child("charity").child("changedValueMin").setValue(-1);
                                xRef.child("category").child("charity").child("changedValueMax").setValue(-1);
                            }
                            else if (!DefCharity.isChecked() && !CharityMin.isEmpty() && !CharityMax.isEmpty()){
                                int charitymin = Integer.parseInt(CharityMin);
                                int charitymax = Integer.parseInt(CharityMax);

                                if (charitymin < 0 || charitymax < 0){
                                    Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                                }
                                else if (charitymin >= charitymax){
                                    Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    xRef.child("category").child("charity").child("changedValueMin").setValue(charitymin);
                                    xRef.child("category").child("charity").child("changedValueMax").setValue(charitymax);
                                }
                            }
                            else{
                                Toast.makeText(DayChange.this, "Please input only 1 of the fields for each catergory", Toast.LENGTH_SHORT).show();
                            }


                            //ADD Others
                            if(DefOthers.isChecked() && OthersMin.isEmpty() && OthersMax.isEmpty() || DefStation.isChecked() && !StationMin.isEmpty() && !StationMax.isEmpty()){

                                CMin.getText().clear();
                                CMax.getText().clear();

                                xRef.child("category").child("others").child("changedValueMin").setValue(-1);
                                xRef.child("category").child("others").child("changedValueMax").setValue(-1);
                            }
                            else if (!DefOthers.isChecked() && !OthersMin.isEmpty() && !OthersMax.isEmpty()){
                                int othersmin = Integer.parseInt(OthersMin);
                                int othersmax = Integer.parseInt(OthersMax);

                                if (othersmin < 0 || othersmax < 0){
                                    Toast.makeText(DayChange.this, "All field must be at least more than 0", Toast.LENGTH_SHORT).show();
                                }
                                else if (othersmin >= othersmax){
                                    Toast.makeText(DayChange.this, "Please ensure that all the minimum value is smaller than maximum value.", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    xRef.child("category").child("others").child("changedValueMin").setValue(othersmin);
                                    xRef.child("category").child("others").child("changedValueMax").setValue(othersmax);
                                }
                            }
                            else{
                                Toast.makeText(DayChange.this, "Please input only 1 of the fields for each catergory", Toast.LENGTH_SHORT).show();
                            }

                            String amt = Amt.getText().toString();
                            //Amount
                            if (DefAmount.isChecked() && !amt.isEmpty() || DefAmount.isChecked() && amt.isEmpty()){
                                Amt.getText().clear();

                                xRef.child("changedAllowance").setValue(-1);
                                Amt.setHint("Current Amount: $" + Allowance);
                            }
                            else if(!DefAmount.isChecked() && !amt.isEmpty()){
                                int newAmt = Integer.parseInt(amt);

                                if(totalMin > newAmt){
                                    Toast.makeText(DayChange.this, "Please do not exceed the current amount", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    xRef.child("changedAllowance").setValue(newAmt);
                                    Amt.setHint("Current Amount: $" + newAmt);
                                }
                            }

//                            startActivity(new Intent(DayChange.this, Days.class));


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
