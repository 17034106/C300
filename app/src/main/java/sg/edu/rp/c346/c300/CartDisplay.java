package sg.edu.rp.c346.c300;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import sg.edu.rp.c346.c300.adapter.CartAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Cart;
import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.model.PrePayment;
import sg.edu.rp.c346.c300.model.RetrieveTiming;

public class CartDisplay extends AppCompatActivity {

    SwipeMenuListView listView;
    CartAdapter cartAdapter;

    TextView displayNoMenu;


    public static ArrayList<Cart> cartList = new ArrayList<>();

    public static String school ="";
    public static ArrayList<Cart> transactionList = new ArrayList<>();

    ArrayList<String> tIdList = new ArrayList<>(); // use for storing all unique Transaction ID
    DatabaseReference drTO;
    DatabaseReference drTC;
    DatabaseReference drNotificationPreOrder;
    DatabaseReference drRemoveALLCartFood;
    int numOfOwnerOrder;
    int numOfCustomerOrder;
    int numOfAddOnSend;

    int numOfCartFood;

    int numOfPrePayment; // get the position of the PrePayment


    static double overallTotalPrice=0;

    Map<Integer,Double> budgetAvailable; // get the budget that the customer has setted.
    Map<Integer, Double> totalSpent ; // check how much the customer planning to spend

    double customerBalance; // get the customer's balance

    private ArrayList<RetrieveTiming> retrieveTimings;


    //Showing the loading
    ProgressDialog dialog;


    static TextView tvOveralLTotalPrice;

    DatabaseReference drCart = FirebaseDatabase.getInstance().getReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    Map<Integer, Integer> checkingNumOfOrderRepeat = new Hashtable<Integer, Integer>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_display);


        final DatabaseReference drPrePayment = FirebaseDatabase.getInstance().getReference().child("prePayment").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drPrePayment.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numOfPrePayment = Integer.parseInt(dataSnapshot.child("numOfPrePayment").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        budgetAvailable = new Hashtable<Integer, Double>();
        totalSpent = new Hashtable<Integer, Double>();


        displayNoMenu = findViewById(R.id.DisplayNoMenu);

        cartList.clear();

        //Showing the loading
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading...");
        dialog.show();

        tvOveralLTotalPrice = findViewById(R.id.tvOverallTotalPrice);
        calculateOverallTotalPrice();


        getBudget();
        getCustomerBalance();

        //region retrieving cart objects from firebase
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
        if (FirebaseAuth.getInstance().getCurrentUser()!=null) {


            drCart.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //Customer customer = dataSnapshot.getValue(Customer.class);

                    cartList.clear();
                    int numOfCartFood = Integer.parseInt(dataSnapshot.child("numOfCartFood").getValue().toString());

                    if(numOfCartFood==0){
                        displayNoMenu.setVisibility(TextView.VISIBLE);
                    }
                    else{
                        displayNoMenu.setVisibility(TextView.INVISIBLE);
                    }

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
                        String lastChanges = dataSnapshot.child(Integer.toString(i)).child("lastChanges").getValue().toString();
                        int stallId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("stallId").getValue().toString());
                        int foodId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("foodId").getValue().toString());
                        int lastChangesInMin = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("lastChangesInMin").getValue().toString());
                        String image = dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();
                        String stallUID = dataSnapshot.child(Integer.toString(i)).child("stallUID").getValue().toString();
                        String school = dataSnapshot.child(Integer.toString(i)).child("school").getValue().toString();


                        ArrayList<AddOn> addOnList = new ArrayList<>();
                        addOnList.clear();
                        int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                        for (int h =0; h<numOfAddOn;h++){
                            String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                            double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                            AddOn addOn = new AddOn(addOnName, addOnPrice);
                            addOnList.add(addOn);
//                            Log.d("What is h", "123456 What is h now: "+h);

                        }


                        Cart cart = new Cart(name, price, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList,additionalNote, startTime, endTime, lastChanges, lastChangesInMin, image, stallUID,school);
                        cartList.add(cart);




                    }

                    retrieveTimings = startEndTimeChecking(cartList);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
        //endregion




        //region Delay the displaying cart code
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                dialog.dismiss();
//                Log.d("CartList", "123456789 what is the cartList "+cartList);


                listView = findViewById(R.id.recycle_cart);

                cartAdapter = new CartAdapter(CartDisplay.this, cartList);
                listView.setAdapter(cartAdapter);


                //region Swiping left to display the delete button
                SwipeMenuCreator creator = new SwipeMenuCreator() {

                    @Override
                    public void create(SwipeMenu menu) {


                        // create "delete" item
                        SwipeMenuItem deleteItem = new SwipeMenuItem(
                                getApplicationContext());
                        // set item background
                        deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                                0x3F, 0x25)));
                        // set item width
                        deleteItem.setWidth(170);
                        // set a icon
                        deleteItem.setIcon(R.drawable.ic_delete_bin_white);
                        //set item title
                        deleteItem.setTitle("Delete");
                        // set item title fontsize
                        deleteItem.setTitleSize(18);
                        //set item title font color
                        deleteItem.setTitleColor(Color.WHITE);
                        // add to menu
                        menu.addMenuItem(deleteItem);
                    }
                };

                // set creator
                listView.setMenuCreator(creator);

                listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                        switch (index) {
                            case 0:
                                //region call the dialog to ask question
                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:

                                                //region do not remove this------------------------------------------------------------------------------------
                                                Toast.makeText(CartDisplay.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                                deleteCart(position); // call the deleteCart function
                                                final Handler handler = new Handler(); // delay the code to calculate the total price again
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        // Do something after 5s = 5000ms
                                                        calculateOverallTotalPrice();
                                                    }
                                                }, 1000);
                                                break;
                                                //endregion


                                            case DialogInterface.BUTTON_NEGATIVE:
                                                //No button clicked
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(CartDisplay.this);
                                builder.setMessage("Do you want to delete?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();
                                //endregion


                                break;

                        }
                        // false : close the menu; true : not close the menu
                        return false;
                    }
                });

                //endregion


            }
        }, 1000);
        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
        //endregion


//        //region  get the customer's school
//        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
//        final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//        databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Customer customer = dataSnapshot.getValue(Customer.class);
//                school = customer.getCustomerschool();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//        //--------------------------------------------------------------------------------------------------------------------------------------------------------------
//        //endregion


        //Press the check out button
        findViewById(R.id.tvCheckOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkingNumOfOrderRepeat.clear(); // clearing the hashMap

                drCart.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        int count=0; // use for creating 0 in the hashtable

                        transactionList.clear();
                        tIdList.clear();// use for storing all unique Transaction ID
                        numOfCartFood = Integer.parseInt(dataSnapshot.child("numOfCartFood").getValue().toString());

                        //region all the cartfood in the firebase so that i can store in both owner("TO")
                        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        for (int i =0; i<numOfCartFood;i++){

                            //region repeated code from the top
                            final String name = dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString();
                            final double price = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("price").getValue().toString());
                            final int quantity = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("quantity").getValue().toString());
                            final String stallName = dataSnapshot.child(Integer.toString(i)).child("stallName").getValue().toString();
                            final String dateTimeOrder = dataSnapshot.child(Integer.toString(i)).child("dateTimeOrder").getValue().toString();
                            final double totalPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("totalPrice").getValue().toString());
                            final String additionalNote = dataSnapshot.child(Integer.toString(i)).child("additionalNote").getValue().toString();
                            final String startTime = dataSnapshot.child(Integer.toString(i)).child("startTime").getValue().toString();
                            final String endTime = dataSnapshot.child(Integer.toString(i)).child("endTime").getValue().toString();
                            final String lastChanges = dataSnapshot.child(Integer.toString(i)).child("lastChanges").getValue().toString();
                            final int stallId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("stallId").getValue().toString());
                            final int foodId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("foodId").getValue().toString());
                            final int lastChangesInMin = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("lastChangesInMin").getValue().toString());
                            final String image = dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();
                            final String stallUID = dataSnapshot.child(Integer.toString(i)).child("stallUID").getValue().toString();
                            school = dataSnapshot.child(Integer.toString(i)).child("school").getValue().toString();

                            final ArrayList<AddOn> addOnList= new ArrayList<>();
                            addOnList.clear();
                            numOfAddOnSend = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                            for (int h =0; h<numOfAddOnSend;h++){
                                String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                                double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                                AddOn addOn = new AddOn(addOnName, addOnPrice);
                                addOnList.add(addOn);

                            }




                            Date dateTimeInDate = MainpageActivity.convertStringToDate(dateTimeOrder,"dd/MM/yyyy h:mm a");
                            Calendar dateTimeInCalendar = Calendar.getInstance();
                            dateTimeInCalendar.setTime(dateTimeInDate);


                            int dayForDateTimeOrder = dateTimeInCalendar.get(Calendar.DAY_OF_WEEK)-2;

                            if (dayForDateTimeOrder==-1){
                                dayForDateTimeOrder = 6;
                            }


                            if (count==0){
                                totalSpent.put(dayForDateTimeOrder-1, 0.0);
                                totalSpent.put(dayForDateTimeOrder, 0.0);
                                totalSpent.put(dayForDateTimeOrder+1, 0.0);
                            }
                            count++;



                            totalSpent.put(dayForDateTimeOrder, (totalSpent.get(dayForDateTimeOrder)+totalPrice));


                            Cart cart = new Cart(name, price, dateTimeOrder, quantity, stallName, stallId, foodId,totalPrice, addOnList,additionalNote, startTime, endTime, lastChanges,lastChangesInMin, image, stallUID,school);
                            transactionList.add(cart);
                            //endregion


                        }
                        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                        //endregion



                        if (overallTimingChecking(cartList)  ) { // checking the booking timing is valid or not
                            //region checking whether the amount required is less or equal to the available amount
                            boolean pass = true;
                            for (Map.Entry<Integer, Double> i : budgetAvailable.entrySet()) {
//
                                if (totalSpent.containsKey(i.getKey()) && i.getValue() < totalSpent.get(i.getKey())) {
                                    pass = false;
                                    break;
                                }
                                else if (totalSpent.containsKey(i.getKey())){
                                    customerBalance -= totalSpent.get(i.getKey());
                                }

                            }
//                            Toast.makeText(CartDisplay.this, "Pass: " + pass, Toast.LENGTH_SHORT).show();

                            //endregion

                            if (customerBalance>=0) {
                                if (pass) { //if the the amount required is less than or equal the available amount


                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:

                                                    //region deduct the amount from the budget
                                                    for (Map.Entry<Integer, Double> i : budgetAvailable.entrySet()) {
                                                        if (totalSpent.containsKey(i.getKey()) && i.getValue() > totalSpent.get(i.getKey())) {
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("budget")
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .child("day")
                                                                    .child(i.getKey() + "")
                                                                    .child("category")
                                                                    .child("food")
                                                                    .child("left")
                                                                    .setValue(Double.parseDouble(String.format("%.2f", i.getValue() - totalSpent.get(i.getKey()))));
                                                            Log.d("Can see this", "Can I see this pls");
                                                        }
                                                    }


                                                    //endregion


                                                    //region remove all the cart foods when the user want to checkout
                                                    // -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    drRemoveALLCartFood = FirebaseDatabase.getInstance().getReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                    for (int i = 0; i < numOfCartFood; i++) {
                                                        drRemoveALLCartFood.child(Integer.toString(i)).removeValue();
                                                    }
                                                    drRemoveALLCartFood.child("numOfCartFood").setValue(0);
                                                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    //endregion


                                                    //region add all the cart food to the "TO" for owner
                                                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    drTO = FirebaseDatabase.getInstance().getReference().child("to").child("school").child(school).child("stall");
                                                    drTO.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                                                            for (int i = 0; i < numOfCartFood; i++) {

                                                                numOfOwnerOrder = Integer.parseInt(dataSnapshot.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child("numOfOrder").getValue().toString());

                                                                if (checkingNumOfOrderRepeat.containsKey(transactionList.get(i).getStallId())) {
                                                                    numOfOwnerOrder = checkingNumOfOrderRepeat.get(transactionList.get(i).getStallId()) + 1;
                                                                    checkingNumOfOrderRepeat.put(transactionList.get(i).getStallId(), checkingNumOfOrderRepeat.get(transactionList.get(i).getStallId()) + 1);
                                                                } else {
                                                                    checkingNumOfOrderRepeat.put(transactionList.get(i).getStallId(), numOfOwnerOrder);
                                                                }


                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("dateTimeOrder").setValue(transactionList.get(i).getDateTimeOrder());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("name").setValue(transactionList.get(i).getName());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("price").setValue(transactionList.get(i).getPrice());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("quantity").setValue(transactionList.get(i).getQuantity());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("stallName").setValue(transactionList.get(i).getStallName());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("totalPrice").setValue(transactionList.get(i).getTotalPrice());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("additionalNote").setValue(transactionList.get(i).getAdditionalNote() + "");
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("lastChanges").setValue(transactionList.get(i).getLastChanges());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("lastChangesInMin").setValue(transactionList.get(i).getLastChangesInMin());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("stallId").setValue(transactionList.get(i).getStallId());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("foodId").setValue(transactionList.get(i).getFoodId());
                                                                String tId; // use for setting a unique Transaction ID
                                                                tId = "" + randomStringValue(17) + transactionList.get(i).getStallId() + transactionList.get(i).getFoodId() + checkingNumOfOrderRepeat.get(transactionList.get(i).getStallId());
                                                                tIdList.add(tId);
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("tId").setValue(tId);
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("startTime").setValue(transactionList.get(i).getStartTime());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("endTime").setValue(transactionList.get(i).getEndTime());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("customerUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("status").setValue("purchased");
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("imageurl").setValue(transactionList.get(i).getImage());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("stallUID").setValue(transactionList.get(i).getStallUID());
                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("school").setValue(school);


                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("addOn").child("numOfAddOn").setValue(0);
                                                                for (int h = 0; h < transactionList.get(i).getAddOnList().size(); h++) {
                                                                    drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("addOn").child(Integer.toString(h)).child("name").setValue(transactionList.get(i).getAddOnList().get(h).getName());
                                                                    drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("addOn").child(Integer.toString(h)).child("price").setValue(transactionList.get(i).getAddOnList().get(h).getPrice());
                                                                    drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child(Integer.toString(numOfOwnerOrder)).child("addOn").child("numOfAddOn").setValue(h + 1);

                                                                }

                                                                drTO.child(Integer.toString(transactionList.get(i).getStallId())).child("order").child("numOfOrder").setValue(numOfOwnerOrder + 1);

//                                                            //region add to the PrePayment firebase
//                                                            DatabaseReference drPrePaymentTID = FirebaseDatabase.getInstance().getReference().child("prePaymentTID");
//                                                            drPrePaymentTID.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tIdList.get(i)).child("toOrderID").setValue(numOfOwnerOrder); // check the order position in TO
//                                                            //endregion
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    //endregion


                                                    //region add all the cart food to the "TC" for customer
                                                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    drTC = FirebaseDatabase.getInstance().getReference().child("tc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order");
                                                    drTC.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            numOfCustomerOrder = Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());

                                                            for (int i = 0; i < numOfCartFood; i++) {
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("dateTimeOrder").setValue(transactionList.get(i).getDateTimeOrder());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("name").setValue(transactionList.get(i).getName());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("price").setValue(transactionList.get(i).getPrice());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("quantity").setValue(transactionList.get(i).getQuantity());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("stallName").setValue(transactionList.get(i).getStallName());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("totalPrice").setValue(transactionList.get(i).getTotalPrice());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("additionalNote").setValue(transactionList.get(i).getAdditionalNote() + "");
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("lastChanges").setValue(transactionList.get(i).getLastChanges());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("stallId").setValue(transactionList.get(i).getStallId());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("foodId").setValue(transactionList.get(i).getFoodId());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("tId").setValue(tIdList.get(i));
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("startTime").setValue(transactionList.get(i).getStartTime());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("endTime").setValue(transactionList.get(i).getEndTime());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("lastChangesInMin").setValue(transactionList.get(i).getLastChangesInMin());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("customerUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("status").setValue("purchased");
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("imageurl").setValue(transactionList.get(i).getImage());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("stallUID").setValue(transactionList.get(i).getStallUID());
                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("school").setValue(transactionList.get(i).getSchool());

                                                                drTC.child(Integer.toString(numOfCustomerOrder + i)).child("addOn").child("numOfAddOn").setValue(0);
                                                                for (int h = 0; h < transactionList.get(i).getAddOnList().size(); h++) {
                                                                    drTC.child(Integer.toString(numOfCustomerOrder + i)).child("addOn").child(Integer.toString(h)).child("name").setValue(transactionList.get(i).getAddOnList().get(h).getName());
                                                                    drTC.child(Integer.toString(numOfCustomerOrder + i)).child("addOn").child(Integer.toString(h)).child("price").setValue(transactionList.get(i).getAddOnList().get(h).getPrice());
                                                                    drTC.child(Integer.toString(numOfCustomerOrder + i)).child("addOn").child("numOfAddOn").setValue(h + 1);

                                                                }

                                                                drTC.child("numOfOrder").setValue(numOfCustomerOrder + i + 1);

                                                                //region add to the PrePayment Firebase
                                                                PrePayment prePayment = new PrePayment(transactionList.get(i).getStallUID()
                                                                        , FirebaseAuth.getInstance().getCurrentUser().getUid()
                                                                        , transactionList.get(i).getTotalPrice()
                                                                        , transactionList.get(i).getDateTimeOrder()
                                                                        , transactionList.get(i).getSchool()
                                                                        , tIdList.get(i));

                                                                drPrePayment.child(Integer.toString(numOfPrePayment + i)).setValue(prePayment);
                                                                drPrePayment.child("numOfPrePayment").setValue(numOfPrePayment + i + 1);

//                                                            DatabaseReference drPrePaymentTID = FirebaseDatabase.getInstance().getReference().child("prePaymentTID");
//                                                            drPrePaymentTID.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tIdList.get(i)).child("stallID").setValue(transactionList.get(i).getStallId());
//                                                            drPrePaymentTID.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tIdList.get(i)).child("foodID").setValue(transactionList.get(i).getFoodId());
//                                                            drPrePaymentTID.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(tIdList.get(i)).child("tcOrderID").setValue(numOfCustomerOrder + i); // check the order position in TC
//
//                                                            drPrePaymentTID.child("numOfPrePaymentTID").setValue(numOfPrePayment + i+1);

                                                                //endregion
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    //endregion


                                                    //region send notification to the firebase
                                                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

                                                    drNotificationPreOrder = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
                                                    drNotificationPreOrder.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString().trim());


                                                            for (int i = 0; i < numOfCartFood; i++) {
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("dateTimeOrder").setValue(transactionList.get(i).getDateTimeOrder());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("name").setValue(transactionList.get(i).getName());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("price").setValue(transactionList.get(i).getPrice());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("quantity").setValue(transactionList.get(i).getQuantity());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("stallName").setValue(transactionList.get(i).getStallName());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("totalPrice").setValue(transactionList.get(i).getTotalPrice());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("additionalNote").setValue(transactionList.get(i).getAdditionalNote() + "");
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("lastChanges").setValue(transactionList.get(i).getLastChanges());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("stallId").setValue(transactionList.get(i).getStallId());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("foodId").setValue(transactionList.get(i).getFoodId());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("tId").setValue(tIdList.get(i));
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("startTime").setValue(transactionList.get(i).getStartTime());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("endTime").setValue(transactionList.get(i).getEndTime());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("lastChangesInMin").setValue(transactionList.get(i).getLastChangesInMin());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("customerUID").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("status").setValue("purchased");
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("imageurl").setValue(transactionList.get(i).getImage());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("stallUID").setValue(transactionList.get(i).getStallUID());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("school").setValue(transactionList.get(i).getSchool());
                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("notificationTiming").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy h:mm:ss a"));


                                                                drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("addOn").child("numOfAddOn").setValue(0);
                                                                for (int h = 0; h < transactionList.get(i).getAddOnList().size(); h++) {
                                                                    drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("addOn").child(Integer.toString(h)).child("name").setValue(transactionList.get(i).getAddOnList().get(h).getName());
                                                                    drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("addOn").child(Integer.toString(h)).child("price").setValue(transactionList.get(i).getAddOnList().get(h).getPrice());
                                                                    drNotificationPreOrder.child(Integer.toString(numOfPreOrder + i)).child("addOn").child("numOfAddOn").setValue(h + 1);

                                                                }

                                                                drNotificationPreOrder.child("numOfPreOrder").setValue(numOfPreOrder + i + 1);


                                                            }


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });


                                                    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
                                                    //endregion

                                                    //reload the activity
                                                    final Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            // Do something after 5s = 5000ms
                                                            Intent intent = new Intent(CartDisplay.this, CartDisplay.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    }, 1000);


                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    //No button clicked
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(CartDisplay.this);
                                    builder.setMessage("Are you sure about the orders?").setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();

                                } else {
                                    Toast.makeText(CartDisplay.this, "Exceeded the Budget", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(CartDisplay.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(CartDisplay.this, "Check the booking timing", Toast.LENGTH_SHORT).show();
                        }




                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }


        });







        //region exit the cart display
        findViewById(R.id.btnCartBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //endregion





        //region useless code (BUT do not remove it)
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


    //region getting the budget information
    public void getBudget(){



        DatabaseReference drBudget = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day");
        drBudget.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                int day1 = calendar.get(Calendar.DAY_OF_WEEK)-2;

                if (day1==-1){
                    day1 = 6;
                }
                int day2 = day1+1;
                if (day2==7){
                    day2=0;
                }
                double leftDay1 = Double.parseDouble(dataSnapshot.child(day1+"").child("category").child("food").child("left").getValue().toString());
                double leftDay2 = Double.parseDouble(dataSnapshot.child(day2+"").child("category").child("food").child("left").getValue().toString());

                budgetAvailable.put(day1, leftDay1);
                budgetAvailable.put(day2, leftDay2);


//                Toast.makeText(CartDisplay.this, budgetAvailable+"", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
    //endregion



    //region get the customer balance
    public void getCustomerBalance(){
        DatabaseReference drCustomerBalance = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drCustomerBalance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerBalance = Double.parseDouble(dataSnapshot.child("balance").getValue().toString().trim());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //endregion



    //region Deleting the cart food based on the user's choice
    public void deleteCart(int position){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        DatabaseReference databaseReferenceGettingNumOfCartFood = FirebaseDatabase.getInstance().getReference().child("cart").child(mUser.getUid()).child("numOfCartFood");


        DatabaseReference drDeleteCart = FirebaseDatabase.getInstance().getReference().child("cart").child(mUser.getUid());
        drDeleteCart.child(Integer.toString(position)).removeValue();
        databaseReferenceGettingNumOfCartFood.setValue(position); //set the numOfCartFood after removing all the items after the position and itself



        ArrayList<Cart> readdedCartList = new ArrayList<Cart>();

        int nextPosition = position+1;

        if (nextPosition<cartList.size()){
            for (int i =nextPosition;i<cartList.size();i++){
                readdedCartList.add(cartList.get(i));
                drDeleteCart.child(Integer.toString(i)).removeValue();
            }
        }


        final DatabaseReference databaseReferenceAddFoodCart = FirebaseDatabase.getInstance().getReference().child("cart").child(mUser.getUid());



        for (int t =0; t<readdedCartList.size();t++){
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("dateTimeOrder").setValue(readdedCartList.get(t).getDateTimeOrder());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("name").setValue(readdedCartList.get(t).getName());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("price").setValue(readdedCartList.get(t).getPrice());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("quantity").setValue(readdedCartList.get(t).getQuantity());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("stallName").setValue(readdedCartList.get(t).getStallName());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("totalPrice").setValue(readdedCartList.get(t).getTotalPrice());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("additionalNote").setValue(readdedCartList.get(t).getAdditionalNote());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("startTime").setValue(readdedCartList.get(t).getStartTime());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("endTime").setValue(readdedCartList.get(t).getEndTime());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("lastChanges").setValue(readdedCartList.get(t).getLastChanges());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("stallId").setValue(readdedCartList.get(t).getStallId());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("foodId").setValue(readdedCartList.get(t).getFoodId());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("lastChangesInMin").setValue(readdedCartList.get(t).getLastChangesInMin());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("imageurl").setValue(readdedCartList.get(t).getImage());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("stallUID").setValue(readdedCartList.get(t).getStallUID());
            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("school").setValue(readdedCartList.get(t).getSchool());

            databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("addOn").child("numOfAddOn").setValue(0);

            ArrayList<AddOn> readdedAddOnList = readdedCartList.get(t).getAddOnList();

            for (int i =0; i<readdedAddOnList.size();i++) {
                databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("addOn").child(Integer.toString(i)).child("name").setValue(readdedAddOnList.get(i).getName());
                databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("addOn").child(Integer.toString(i)).child("price").setValue(readdedAddOnList.get(i).getPrice());
                databaseReferenceAddFoodCart.child(Integer.toString(position+t)).child("addOn").child("numOfAddOn").setValue(i+1);

            }

        }

        databaseReferenceGettingNumOfCartFood.setValue(position+readdedCartList.size());


        Intent intent = new Intent(CartDisplay.this, CartDisplay.class);
        startActivity(intent);
        finish();

    }
    //endregion


    //region Calculate the overall total price in the cart
    public static void calculateOverallTotalPrice(){
        DatabaseReference drOverallTotalPrice = FirebaseDatabase.getInstance().getReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        drOverallTotalPrice.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfCartFood = Integer.parseInt(dataSnapshot.child("numOfCartFood").getValue().toString());
                overallTotalPrice=0;


                for (int i =0; i<numOfCartFood;i++){
                    overallTotalPrice += Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("totalPrice").getValue().toString());
                }
                tvOveralLTotalPrice.setText(String.format("$%.2f", overallTotalPrice));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //endregion


    //region generate random string value for TID
    private static final String STRING_VALUE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvxyz0123456789";
    public static String randomStringValue(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        while (count-- != 0) {
            int num = (int)(Math.random()*STRING_VALUE.length());  // Math.floor(Math.random() * 10);    returns a random integer from 0 to 9
            stringBuilder.append(STRING_VALUE.charAt(num));
        }
        return stringBuilder.toString();
    }
    //endregion


    //region check whether all booking timing is okay or not
    public boolean overallTimingChecking(ArrayList<Cart> cartList){

        boolean matched  = true;
        int h=0;

        for (int i =0; i<cartList.size(); i++) {

            Date startTimeDate = MainpageActivity.convertStringToDate(retrieveTimings.get(i).getStartTime(),"HHmm");
            Date endTimeDate = MainpageActivity.convertStringToDate(retrieveTimings.get(i).getEndTime(),"HHmm");
            int lastChangesInMin = retrieveTimings.get(i).getLastChangesInMin();

            Date currentTime = Calendar.getInstance().getTime();
            Calendar earliestDate = Calendar.getInstance();

            int earlierHour =0;
            int earliestMinute =0;


            earliestMinute = currentTime.getMinutes()+lastChangesInMin+1;
            earlierHour = currentTime.getHours();
            if (earliestMinute>59){ //check whether to plus one hour if the minute is more than 59
                earliestMinute=earliestMinute-60;
                earlierHour++;
            }

            if (earlierHour==endTimeDate.getHours()){ //check whether the current timing is after the last order or not. If yes, then add one day and set the earliest hour and minute to start time (minute)
                if (earliestMinute>endTimeDate.getMinutes()){
                    earliestDate.add(Calendar.DATE,1);
                    earlierHour = startTimeDate.getHours();
                    earliestMinute =startTimeDate.getMinutes();
                }
            }
            else if (earlierHour>endTimeDate.getHours()){ //hour
                earliestDate.add(Calendar.DATE,1);
                earlierHour = startTimeDate.getHours();
                earliestMinute =startTimeDate.getMinutes();

            }

            if (currentTime.getHours()<startTimeDate.getHours()){ // check whether the current timing is before the start time, if yes, then change the earliest hour and minute to start time (hour)
                earlierHour=startTimeDate.getHours();
                earliestMinute = startTimeDate.getMinutes();
            }
            else if (currentTime.getHours()==startTimeDate.getHours()){ //minute
                if (currentTime.getMinutes()<startTimeDate.getMinutes()){
                    earliestMinute = startTimeDate.getMinutes();
                }
            }

            Date dateFormatSelectedByUser = MainpageActivity.convertStringToDate(cartList.get(i).getDateTimeOrder(), "dd/MM/yyyy h:mm a");
            Date stringSelectedUserDateConverted = MainpageActivity.convertStringToDate(String.format("%02d/%02d/%d",dateFormatSelectedByUser.getDate(),dateFormatSelectedByUser.getMonth(), dateFormatSelectedByUser.getYear()),"dd/MM/yyyy");

            Date stringCurrentDateConverted = MainpageActivity.convertStringToDate(String.format("%02d/%02d/%d",currentTime.getDate(),currentTime.getMonth(), currentTime.getYear()),"dd/MM/yyyy");


            if (stringCurrentDateConverted.compareTo(stringSelectedUserDateConverted)==0){
                if (currentTime.getHours()<endTimeDate.getHours() || (currentTime.getHours()== endTimeDate.getHours() && currentTime.getMinutes() <=endTimeDate.getMinutes())){
                    if (dateFormatSelectedByUser.getHours()>startTimeDate.getHours() || ( dateFormatSelectedByUser.getHours()== startTimeDate.getHours() && dateFormatSelectedByUser.getMinutes()>=startTimeDate.getMinutes())){
                        if (dateFormatSelectedByUser.getHours()<endTimeDate.getHours() || (dateFormatSelectedByUser.getHours()== endTimeDate.getHours() && dateFormatSelectedByUser.getMinutes()<= endTimeDate.getMinutes())){
                            if (dateFormatSelectedByUser.getHours()>earlierHour || (dateFormatSelectedByUser.getHours()==earlierHour &&  dateFormatSelectedByUser.getMinutes() >= earliestMinute)) {
//                                correctTimeSelected(true, "9999");
                                matched = true;
                                h=1;
                            }
                            else{
//                                correctTimeSelected(false,"Earliest Timing to pre-order is above");
                                matched = false;
                                h=2;
                            }
                        }
                        else{
//                            correctTimeSelected(false, "Stall is closed during that timing");
                            matched=false;
                            h=3;
                        }
                    }
                    else{
//                        correctTimeSelected(false, "Stall is closed during that timing");
                        matched = false;
                        h=4;
                    }
                }
                else{
//                    correctTimeSelected(false, "Earliest timing to pre-order is above");
                    matched = false;
                    h=5;
                }
            }
            else if (stringCurrentDateConverted.compareTo(stringSelectedUserDateConverted)<0){
                if ((dateFormatSelectedByUser.getHours()> startTimeDate.getHours()
                        || (dateFormatSelectedByUser.getHours()==startTimeDate.getHours()) && dateFormatSelectedByUser.getHours()>=startTimeDate.getHours())
                        && (dateFormatSelectedByUser.getHours() < endTimeDate.getHours()
                        || (dateFormatSelectedByUser.getHours()== endTimeDate.getHours())&& dateFormatSelectedByUser.getMinutes() <= endTimeDate.getMinutes())){
//                    correctTimeSelected(true, "8888");
                    matched = true;
                    h=6;
                }
                else{
//                    correctTimeSelected(false, "Stall is close during that timing");
                    matched=false;
                    h=7;
                }
            }
            else{
                matched = false;
                h=8;

            }


        }


//        Toast.makeText(this, matched+" "+h, Toast.LENGTH_SHORT).show();
//        Log.d("match with h","--------------Match and h is what: "+matched+" "+h);

        return matched;
    }
    //endregion


    //region retrieve all the start time, end time and lastChangesInMin
    public ArrayList<RetrieveTiming> startEndTimeChecking(final ArrayList<Cart> cartList){

        final ArrayList<RetrieveTiming> retrieveTimings = new ArrayList<>();

        DatabaseReference drStartEndTime = FirebaseDatabase.getInstance().getReference().child("menu").child("school");
        drStartEndTime.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (Cart i : cartList){
                    String startTime = dataSnapshot.child(i.getSchool()).child("stall").child(i.getStallId()+"").child("startTime").getValue().toString();
                    String endTime = dataSnapshot.child(i.getSchool()).child("stall").child(i.getStallId()+"").child("endTime").getValue().toString();
                    int lastChangesInMin = Integer.parseInt(dataSnapshot.child(i.getSchool()).child("stall").child(i.getStallId()+"").child("food").child(i.getFoodId()+"").child("lastChanges").getValue().toString());
                    retrieveTimings.add(new RetrieveTiming(startTime, endTime, lastChangesInMin));
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return retrieveTimings;
    }
    //endregion




}