package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.adapter.FoodAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;

public class IndividualEditFoodDisplay extends AppCompatActivity {

    TextView tvTid;
    TextView foodName;
    TextView foodPrice;
    TextView stallName;
    TextView lastChanges;
    TextView checkOutPrice;
    TextView tvFoodStallDuration;


    TextView quantityDisplay;
    int quantityValue;

    ImageButton quantityDecrease, quantityIncrease;

    EditText additionalNote;

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser mUser = mAuth.getCurrentUser();
    public static String school ="";

    boolean isClicked = false;

    double totalAddOn =0;

    double eachPrice =0;

    static double totalPrice = 0;

    int i =0; // use for for-loop

    public static ArrayList<AddOn> addOnArray= new ArrayList<>();

    int lastChangeInMin;

    static IndividualEditFoodDisplay thisActivy; // finish this activity from another activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_edit_food_display_1);

            thisActivy = this;


            final Intent intent = getIntent();

            quantityValue = 1;

            tvTid = findViewById(R.id.IndividualEditTID);
            foodName = findViewById(R.id.tvFoodName);
            foodPrice = findViewById(R.id.tvFoodPrice);
            stallName = findViewById(R.id.tvFoodStallName);
            lastChanges = findViewById(R.id.tvIndividualLastChanges);
            quantityDisplay = findViewById(R.id.QuantityDisplay);
            quantityIncrease = findViewById(R.id.QuantityIncrease);
            quantityDecrease = findViewById(R.id.QuantityDecrease);
            additionalNote = findViewById(R.id.etAddtionalNotes);
            tvFoodStallDuration = findViewById(R.id.tvFoodStallDuration);
            checkOutPrice = findViewById(R.id.tvTotalPrice);




            eachPrice = intent.getDoubleExtra("foodPrice",0);
            totalPrice =  eachPrice * quantityValue;

            school = MainpageActivity.school; //got it from MainpageActivity

            quantityDisplay.setText(Integer.toString(quantityValue));

            tvTid.setText(intent.getStringExtra("tId"));
            foodName.setText(intent.getStringExtra("foodName"));
            foodPrice.setText(String.format("$%.2f",intent.getDoubleExtra("foodPrice",0)));
            stallName.setText("Stall: "+intent.getStringExtra("stallName"));
            int lastChangesInMin = intent.getIntExtra("lastChangesInMin",-1);
            String lastChangeDisplay = "Changes can only be made before <b>"+lastChangesInMin+"min</b> of the collection time";
            lastChanges.setText(Html.fromHtml(lastChangeDisplay));
            checkOutPrice.setText(String.format("$%.2f", totalPrice));


        //region displaying the stall's operation hour
            Date startTimeDate = MainpageActivity.convertStringToDate(intent.getStringExtra("startTime"), "HHmm");
            Date endTimeDate = MainpageActivity.convertStringToDate(intent.getStringExtra("endTime"), "HHmm");
            String startTime = MainpageActivity.convertDateToString(startTimeDate, "h:mm a");
            String endTime = MainpageActivity.convertDateToString(endTimeDate, "h:mm a");
            tvFoodStallDuration.setText(String.format("Working from %s to %s", startTime,endTime));
            //endregion



            int stallId = intent.getIntExtra("stallId",1);
//            int foodId = Integer.parseInt(intent.getStringExtra("tId").charAt(18)+"");
            int foodId = intent.getIntExtra("foodId", 1);
            Log.d("What is the TID", "What is the TID: "+intent.getStringExtra("tId"));

            //region display all AddOn && getting the lastChanges
            //---------------------------------------------------------------------------------------------------------------
            final DatabaseReference databaseReferenceAddOn = FirebaseDatabase.getInstance().getReference().child("menu").child("school").child(school).child("stall").child(Integer.toString(stallId)).child("food").child(Integer.toString(foodId));

            final LinearLayout linearLayout = findViewById(R.id.linearAddOn);

            databaseReferenceAddOn.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    String numOfAddOn = dataSnapshot.child("AddOn").child("numOfAddOn").getValue().toString();
                    int intNumOfAddOn = Integer.parseInt(numOfAddOn);

                    for (i =0; i<intNumOfAddOn;i++){

                        final String name = dataSnapshot.child("AddOn").child(Integer.toString(i)).child("name").getValue().toString();
                        final String price = dataSnapshot.child("AddOn").child(Integer.toString(i)).child("price").getValue().toString();
                        final double doublePrice = Double.parseDouble(price);



                        RelativeLayout relativeAddOn = new RelativeLayout(getApplication());

                        RelativeLayout.LayoutParams layoutParamsName = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        layoutParamsName.setMargins(35,20,0,20);
                        TextView tvAddOnName = new TextView(getApplication());
                        tvAddOnName.setLayoutParams(layoutParamsName);
                        tvAddOnName.setText(name);
                        tvAddOnName.setTextSize(17);
                        tvAddOnName.setTextColor(Color.BLACK);


                        RelativeLayout.LayoutParams layoutParamsPrice = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsPrice.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        layoutParamsPrice.setMargins(0,20,135,20);
                        TextView tvAddOnPrice = new TextView(getApplication());
                        tvAddOnPrice.setLayoutParams(layoutParamsPrice);
                        tvAddOnPrice.setText(String.format("$%.2f",doublePrice));
                        tvAddOnPrice.setTextSize(17);
                        tvAddOnPrice.setTextColor(Color.BLACK);

                        addOnArray.clear();


                        RelativeLayout.LayoutParams layoutParamsCheckBox = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                        layoutParamsCheckBox.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        layoutParamsCheckBox.setMargins(0,6,40,20);
                        CheckBox cbAddOnWant = new CheckBox(getApplication());
                        cbAddOnWant.setLayoutParams(layoutParamsCheckBox);
                        cbAddOnWant.setId(i);
                        cbAddOnWant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                Log.d("What is addOnArray", "Tell me what is addOnArray: "+addOnArray);
//                            addOnArray.clear();
//                            Log.d("addOnArray is remove", "AddOn Array is remove ");

                                if (isChecked){

                                    addOnArray.add(new AddOn(name, Double.parseDouble(price)));
                                    Log.d("tyuiotyuioyui", "What is i: "+i);

//                                totalPrice+=(doublePrice*quantityValue);
                                    totalAddOn += doublePrice;
//                                checkOutPrice.setText(String.format("$%.2f", totalPrice));
//                                isClicked = true;
                                }
                                else{
//                                totalPrice-=(doublePrice*quantityValue);
                                    for (int c=0; c<addOnArray.size();c++){
                                        if (addOnArray.get(c).getName().equals(name)){
                                            addOnArray.remove(c);
                                        }
                                    }


                                    totalAddOn-=doublePrice;
                                checkOutPrice.setText(String.format("$%.2f", totalPrice));
//                                isClicked=false;
                                }

                                calculateTotal();
                                checkOutPrice.setText(String.format("$%.2f", totalPrice));
                                Log.d("--------","----------------------------------------");
                                Log.d("What is addOnArray", "Tell me what is addOnArray: "+addOnArray);
                                Log.d("What is addOnArray size", "Tell me what is addOnArray Size : "+addOnArray.size());


                            }
                        });




                        relativeAddOn.addView(tvAddOnName);
                        relativeAddOn.addView(tvAddOnPrice);
                        relativeAddOn.addView(cbAddOnWant);
                        linearLayout.addView(relativeAddOn);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //---------------------------------------------------------------------------------------------------------------
            //endregion


            ImageButton floatingButtonCart = findViewById(R.id.floatingButtonCart);
            floatingButtonCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getBaseContext(), CartDisplay.class);
                    startActivity(intent);
                }
            });

            ImageButton floatingButtonBack = findViewById(R.id.floatingButtonBack);
            floatingButtonBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });










            quantityDecrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (quantityValue>1){
                        quantityValue--;
                        quantityDisplay.setText(Integer.toString(quantityValue));

                        calculateTotal();

                        checkOutPrice.setText(String.format("$%.2f", totalPrice));

                    }

                }
            });

            quantityIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    quantityValue++;
                    quantityDisplay.setText(Integer.toString(quantityValue));
                    calculateTotal();
                    checkOutPrice.setText(String.format("$%.2f", totalPrice));


                }
            });

        RelativeLayout btnChoosingDateTime = findViewById(R.id.IndividualEditDatTime);

        btnChoosingDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentDateTime = new Intent(getApplicationContext(), IndividualEditFoodDateTimeSelection.class);

                intentDateTime.putExtra("foodName", intent.getStringExtra("foodName"));
                intentDateTime.putExtra("foodPrice", intent.getDoubleExtra("foodPrice",0));
                intentDateTime.putExtra("stallName", intent.getStringExtra("stallName"));
                intentDateTime.putExtra("lastChanges",intent.getIntExtra("lastChanges",0) );
                intentDateTime.putExtra("lastChangesInMin", intent.getIntExtra("lastChangesInMin",0));
                Log.d("FoodDisplay", "What is the lastChangesInMin: "+intent.getIntExtra("lastChangesInMin",-1));
                intentDateTime.putExtra("quantity", quantityValue);
                intentDateTime.putExtra("additionalNote", additionalNote.getText().toString().trim());
                intentDateTime.putExtra("totalPrice", totalPrice);
                intentDateTime.putExtra("startTime", intent.getStringExtra("startTime"));
                intentDateTime.putExtra("endTime", intent.getStringExtra("endTime"));
                intentDateTime.putExtra("stallId", intent.getIntExtra("stallId",-1));
                intentDateTime.putExtra("foodId", intent.getIntExtra("foodId", -1));
                intentDateTime.putExtra("tId", intent.getStringExtra("tId"));

                startActivity(intentDateTime);
            }
        });





    }

    public void calculateTotal(){

        totalPrice = (quantityValue*eachPrice) + (totalAddOn * quantityValue);


    }


    //finish this activity from another activity
    public static IndividualEditFoodDisplay getInstance(){
        return thisActivy;
    }


}
