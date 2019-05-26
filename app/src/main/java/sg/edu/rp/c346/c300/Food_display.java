package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.model.School;

public class Food_display extends AppCompatActivity {


    TextView foodName;
    TextView foodPrice;
    TextView stallName;
    TextView lastChanges;
    TextView checkOutPrice;
    TextView tvFoodStallDuration;

    static Food_display food_display; // get this class so that i can finish this class at another activity

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_display);

        food_display = this; // get this class so that i can finish this class at another activity


        final Intent intent = getIntent();

        quantityValue = 1;



        foodName = findViewById(R.id.tvFoodName);
        foodPrice = findViewById(R.id.tvFoodPrice);
        stallName = findViewById(R.id.tvFoodStallName);
        lastChanges = findViewById(R.id.tvLastChanges);
        quantityDisplay = findViewById(R.id.QuantityDisplay);
        quantityIncrease = findViewById(R.id.QuantityIncrease);
        quantityDecrease = findViewById(R.id.QuantityDecrease);
        checkOutPrice = findViewById(R.id.tvTotalPrice);
        additionalNote = findViewById(R.id.etAddtionalNotes);
        tvFoodStallDuration = findViewById(R.id.tvFoodStallDuration);

        eachPrice = intent.getDoubleExtra("foodPrice",0);
        totalPrice =  eachPrice * quantityValue;

        school = intent.getStringExtra("school");

        //region displaying the stall's operation hour
        String inputPattern = "HHmm";
        String outputPattern = "h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        String startTime=null;
        String endTime = null;
        try{
            Date startTimeDate = inputFormat.parse(intent.getStringExtra("startTime"));
            Date endTimeDate = inputFormat.parse(intent.getStringExtra("endTime"));
            startTime = outputFormat.format(startTimeDate);
            endTime = outputFormat.format(endTimeDate);
        }catch (ParseException e){

        }

        tvFoodStallDuration.setText(String.format("Working from %s to %s", startTime,endTime));
        //endregion



        int stallId = intent.getIntExtra("stallId",1);
        int foodId = intent.getIntExtra("foodId",1);

        final DatabaseReference databaseReferenceAddOn = FirebaseDatabase.getInstance().getReference().child("menu").child("school").child(school).child("stall").child(Integer.toString(stallId)).child("food").child(Integer.toString(foodId)).child("AddOn");
        Log.d("fasjbfldablfbdsl","blsdfns:  "+databaseReferenceAddOn);

        final LinearLayout linearLayout = findViewById(R.id.linearAddOn);



        databaseReferenceAddOn.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String numOfAddOn = dataSnapshot.child("numOfAddOn").getValue().toString();
                int intNumOfAddOn = Integer.parseInt(numOfAddOn);

                for (i =0; i<intNumOfAddOn;i++){

                    final String name = dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString();
                    final String price = dataSnapshot.child(Integer.toString(i)).child("price").getValue().toString();
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
//                                checkOutPrice.setText(String.format("$%.2f", totalPrice));
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






        quantityDisplay.setText(Integer.toString(quantityValue));

        foodName.setText(intent.getStringExtra("foodName"));
        foodPrice.setText(String.format("$%.2f",intent.getDoubleExtra("foodPrice",0)));
        stallName.setText("Stall: "+intent.getStringExtra("stallName"));
        String lastChangeDisplay = "Changes can only be made before <b>"+intent.getIntExtra("lastChanges",0)+"min</b> of the collection time";
        lastChanges.setText(Html.fromHtml(lastChangeDisplay));
        checkOutPrice.setText(String.format("$%.2f", totalPrice));



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


        RelativeLayout btnCheckOut = findViewById(R.id.FoodDisplyCheckOut);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCheckOut = new Intent(getApplicationContext(), dateTimeSelection.class);

                intentCheckOut.putExtra("foodName", intent.getStringExtra("foodName"));
                intentCheckOut.putExtra("foodPrice", intent.getDoubleExtra("foodPrice",0));
                intentCheckOut.putExtra("stallName", intent.getStringExtra("stallName"));
                intentCheckOut.putExtra("lastChanges",intent.getIntExtra("lastChanges",0) );
                intentCheckOut.putExtra("quantity", quantityValue);
                intentCheckOut.putExtra("additionalNote", additionalNote.getText().toString().trim());
                intentCheckOut.putExtra("totalPrice", totalPrice);
                intentCheckOut.putExtra("startTime", intent.getStringExtra("startTime"));
                intentCheckOut.putExtra("endTime", intent.getStringExtra("endTime"));
                intentCheckOut.putExtra("stallId", intent.getIntExtra("stallId",-1));



                //intentCheckOut.putExtra("addOnArray", addOnArray);

                startActivity(intentCheckOut);
            }
        });


    }


    public void calculateTotal(){

        totalPrice = (quantityValue*eachPrice) + (totalAddOn * quantityValue);


    }

    // get this class so that i can finish this class at another activity
    public static Food_display finishActivity(){
        return  food_display;
    }


}
