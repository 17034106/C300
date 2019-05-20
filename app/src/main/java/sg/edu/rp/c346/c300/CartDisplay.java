package sg.edu.rp.c346.c300;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import sg.edu.rp.c346.c300.adapter.CartAdapter;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Cart;
import sg.edu.rp.c346.c300.model.Customer;

public class CartDisplay extends AppCompatActivity {

    ListView listView;
    CartAdapter cartAdapter;


    ArrayList<Cart> cartList = new ArrayList<>();


    //Showing the loading
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_display);

        //region useless code for adding objects into arrayList

//        AddOn addOn1 = new AddOn("1", "Add Rice",0.50);
//        AddOn addOn2 = new AddOn("0", "Add Ice", 0.50);
//
//        ArrayList<AddOn> addOnList = new ArrayList<>();
//        addOnList.addAll(Arrays.asList(addOn1,addOn2));
//
//        Cart cart1 = new Cart("Fishball Noodle", 2.50, "bfdj", 2, "Noodle Stall", 4.5, addOnList);
//        Cart cart2 = new Cart("Fishball Noodle", 2.50,"fjdsb" , 2, "Noodle Stall", 4.5, addOnList);
//
//        for (int i =0; i<cartList.size();i++){
//            Log.d("What is the food Name", "Tell me the food name: "+cartList.get(i).getFoodName());
//            Log.d("What is the Quantity", "Tell me the food quantity: "+cartList.get(i).getFoodQuantity());
//        }
//
//        cartList.addAll(Arrays.asList(cart1,cart2));

        //endregion


        //Showing the loading
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();


        //region retrieving cart objects from firebase
        if (mUser!=null) {
            final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("cart").child(mUser.getUid());


            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Customer customer = dataSnapshot.getValue(Customer.class);

                    int numOfCartFood = Integer.parseInt(dataSnapshot.child("numOfCartFood").getValue().toString());

                    for (int i =0; i<numOfCartFood;i++){


                        String name = dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString();
                        double price = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("price").getValue().toString());
                        int quantity = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("quantity").getValue().toString());
                        String stallName = dataSnapshot.child(Integer.toString(i)).child("stallName").getValue().toString();
                        String dateTimeOrder = dataSnapshot.child(Integer.toString(i)).child("dateTimeOrder").getValue().toString();
                        double totalPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("totalPrice").getValue().toString());
                        String additionalNote = dataSnapshot.child(Integer.toString(i)).child("additionalNote").getValue().toString();
                        String startTime = dataSnapshot.child(Integer.toString(i)).child("startTime").getValue().toString();
                        String endTime = dataSnapshot.child(Integer.toString(i)).child("endTime").getValue().toString();

                        ArrayList<AddOn> addOnList = new ArrayList<>();
                        addOnList.clear();
                        int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                        for (int h =0; h<numOfAddOn;h++){
                            String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                            double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                            AddOn addOn = new AddOn(addOnName, addOnPrice);
                            addOnList.add(addOn);
                            Log.d("What is h", "123456 What is h now: "+h);

                        }




                        Cart cart = new Cart(name, price, dateTimeOrder, quantity, stallName, totalPrice, addOnList,additionalNote, startTime, endTime);
                        cartList.add(cart);

                        Log.d("------------","--------------------------------------------------------------------");
                        Log.d("What is cart", "Tell me what is cart now: "+cart.getName());
                        Log.d("What is cartList", "Tell me what is addOnList size in cartList now: "+cartList.get(0).getAddOnList().size());
                        Log.d("What is name", "Tell me what is name now: "+cartList.get(0).getName());
                        Log.d("What is addOnList", "Tell me what is addOnList size now: "+addOnList.size());


                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        //endregion


        //region Delay the displaying cart code
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                dialog.dismiss();
                Log.d("CartList", "123456789 what is the cartList "+cartList);


                listView = findViewById(R.id.recycle_cart);

                cartAdapter = new CartAdapter(CartDisplay.this, cartList);
                listView.setAdapter(cartAdapter);
            }
        }, 1000);
        //endregion


        findViewById(R.id.btnCartBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        //region useless code (do not remove it)
//        final LinearLayout cartRelativeBiggest= findViewById(R.id.CartRelativeBiggest);



//        for (int i =0; i<5;i++){
//
//            Log.d("What is the I ", "Can u tell me what is I: "+i);
//
//            RelativeLayout relativeLayout = new RelativeLayout(this);
//            RelativeLayout.LayoutParams layoutParamsAll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParamsAll.setMargins(35,25,35,20);
//            relativeLayout.setLayoutParams(layoutParamsAll);
//
//
//            TextView tvStallName = new TextView(this);
//            RelativeLayout.LayoutParams layoutParamsStallName = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,100);
//            layoutParamsStallName.setMargins(0,0,0,10);
//            tvStallName.setLayoutParams(layoutParamsStallName);
//            tvStallName.setText("Stall Name");
//            tvStallName.setTextSize(20);
//            tvStallName.setTextColor(Color.BLACK);
//            tvStallName.setBackgroundResource(R.drawable.draw_line);
//            tvStallName.setPadding(10,0,0,0);
//
//
//
//            RelativeLayout cartRelative1 = new RelativeLayout(this);
//            RelativeLayout.LayoutParams layoutParamsCartRelative1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            cartRelative1.setLayoutParams(layoutParamsCartRelative1);
//            cartRelative1.setPadding(0,100,0,4);
//            cartRelative1.setBackgroundResource(R.drawable.draw_line);
//
//            ImageView imageViewFoodStall = new ImageView(getApplication());
//            imageViewFoodStall.setId(99);
//            RelativeLayout.LayoutParams layoutParamsFoodStallImage = new RelativeLayout.LayoutParams(350,300);
//            layoutParamsFoodStallImage.setMargins(20,18,10,0);
//            imageViewFoodStall.setBackgroundResource(R.drawable.fishball);
//            imageViewFoodStall.setLayoutParams(layoutParamsFoodStallImage);
//
//
//    //        RelativeLayout.LayoutParams layoutParamsTextView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//    //        layoutParamsTextView.setMargins(0,13,0,0);
//    //        layoutParamsTextView.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//    //        layoutParamsTextView.addRule(RelativeLayout.ALIGN_PARENT_END);
//    //        layoutParamsTextView.addRule(RelativeLayout.RIGHT_OF, 1);
//    //        layoutParamsTextView.addRule(RelativeLayout.END_OF, 1);
//
//            TextView tvFoodName = new TextView(this);
//            tvFoodName.setId(51);
//            RelativeLayout.LayoutParams layoutParamsTextView1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParamsTextView1.setMargins(0,13,0,0);
//            layoutParamsTextView1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            layoutParamsTextView1.addRule(RelativeLayout.ALIGN_PARENT_END);
//            layoutParamsTextView1.addRule(RelativeLayout.RIGHT_OF, 99);
//            layoutParamsTextView1.addRule(RelativeLayout.END_OF, 99);
//            tvFoodName.setLayoutParams(layoutParamsTextView1);
//            tvFoodName.setText("Name");
//
//            TextView tvFoodPrice = new TextView(this);
//            tvFoodPrice.setId(52);
//            RelativeLayout.LayoutParams layoutParamsTextView2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParamsTextView2.setMargins(0,13,0,0);
//            layoutParamsTextView2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            layoutParamsTextView2.addRule(RelativeLayout.ALIGN_PARENT_END);
//            layoutParamsTextView2.addRule(RelativeLayout.RIGHT_OF, 99);
//            layoutParamsTextView2.addRule(RelativeLayout.END_OF, 99);
//            layoutParamsTextView2.addRule(RelativeLayout.BELOW, 51);
//            tvFoodPrice.setLayoutParams(layoutParamsTextView2);
//            tvFoodPrice.setText("Price");
//
//
//            TextView tvOrderTiming = new TextView(this);
//            tvOrderTiming.setId(53);
//            RelativeLayout.LayoutParams layoutParamsTextView3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParamsTextView3.setMargins(0,13,0,0);
//            layoutParamsTextView3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            layoutParamsTextView3.addRule(RelativeLayout.ALIGN_PARENT_END);
//            layoutParamsTextView3.addRule(RelativeLayout.RIGHT_OF, 99);
//            layoutParamsTextView3.addRule(RelativeLayout.END_OF, 99);
//            layoutParamsTextView3.addRule(RelativeLayout.BELOW, 52);
//            tvOrderTiming.setLayoutParams(layoutParamsTextView3);
//            tvOrderTiming.setText("Order Timing");
//
//
//            TextView tvLastChanges = new TextView(this);
//            tvLastChanges.setId(54);
//            RelativeLayout.LayoutParams layoutParamsTextView4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParamsTextView4.setMargins(0,13,0,0);
//            layoutParamsTextView4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            layoutParamsTextView4.addRule(RelativeLayout.ALIGN_PARENT_END);
//            layoutParamsTextView4.addRule(RelativeLayout.RIGHT_OF, 99);
//            layoutParamsTextView4.addRule(RelativeLayout.END_OF, 99);
//            layoutParamsTextView4.addRule(RelativeLayout.BELOW, 53);
//            tvLastChanges.setLayoutParams(layoutParamsTextView4);
//            tvLastChanges.setText("Last Changes");
//
//
//            RelativeLayout cartRelativeQuantity = new RelativeLayout(this);
//            RelativeLayout.LayoutParams layoutParamsCartQuantity = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            layoutParamsCartQuantity.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            cartRelative1.setLayoutParams(layoutParamsCartQuantity);
//            cartRelative1.setBackgroundResource(R.drawable.draw_line);
//
//
//
//
//
//
//
//            TextView textViewQuantityValue = new TextView(this);
//            textViewQuantityValue.setId(49);
//            RelativeLayout.LayoutParams layoutParamsQuantityValue = new RelativeLayout.LayoutParams(70,50);
//            layoutParamsQuantityValue.addRule(RelativeLayout.RIGHT_OF,48);
//            textViewQuantityValue.setLayoutParams(layoutParamsQuantityValue);
//
//
//            ImageButton imageButtonDecrease = new ImageButton(this);
//            imageButtonDecrease.setId(48);
//            RelativeLayout.LayoutParams layoutParamsDecrease = new RelativeLayout.LayoutParams(50,50);
//            imageButtonDecrease.setLayoutParams(layoutParamsDecrease);
//            imageButtonDecrease.setBackgroundResource(R.drawable.ic_minus);
//            imageButtonDecrease.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    quantity--;
//
//                }
//            });
//
//            ImageButton imageButtonIncrease = new ImageButton(this);
//            imageButtonIncrease.setId(50);
//            RelativeLayout.LayoutParams layoutParamsIncrease = new RelativeLayout.LayoutParams(50,50);
//            layoutParamsIncrease.addRule(RelativeLayout.RIGHT_OF,49);
//            imageButtonIncrease.setLayoutParams(layoutParamsIncrease);
//            imageButtonIncrease.setBackgroundResource(R.drawable.ic_plus);
//            imageButtonIncrease.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
//
//
//
//
//
//
//            cartRelative1.addView(imageViewFoodStall);
//            cartRelative1.addView(tvFoodName);
//            cartRelative1.addView(tvFoodPrice);
//            cartRelative1.addView(tvOrderTiming);
//            cartRelative1.addView(tvLastChanges);
//
//            cartRelativeQuantity.addView(imageButtonDecrease);
//            cartRelativeQuantity.addView(textViewQuantityValue);
//            cartRelativeQuantity.addView(imageButtonIncrease);
//            cartRelative1.addView(cartRelativeQuantity);
//
//
//
//            relativeLayout.addView(cartRelative1);
//
//            relativeLayout.addView(tvStallName);
//            cartRelativeBiggest.addView(relativeLayout);
//
//
//        }


//endregion

    }
}
