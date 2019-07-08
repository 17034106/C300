package sg.edu.rp.c346.c300;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import sg.edu.rp.c346.c300.adapter.PreOrderNotificationAdapter;
import sg.edu.rp.c346.c300.adapter.WalkInNotificationAdapter;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.NotificationHeader;
import sg.edu.rp.c346.c300.model.WalkIn;

public class PreOrderUpdates extends AppCompatActivity {


    private ExpandableListView listView;
    private PreOrderNotificationAdapter preOrderListAdapter;
    private WalkInNotificationAdapter walkInListAdapter;
    private ArrayList<NotificationHeader> listDataHeader;
    private HashMap<NotificationHeader, ArrayList<Collection>> preOrderListHash;
    private HashMap<NotificationHeader, ArrayList<WalkIn>> walkInListHash;

    TextView notificationTitle;

    ArrayList<NotificationHeader> preOrderHeaderList = new ArrayList<>();
    ArrayList<Collection> preOrderContentList = new ArrayList<>();

    ArrayList<NotificationHeader> walkInHeaderList = new ArrayList<>();
    ArrayList<WalkIn> walkInContentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_order_updates);

        //region check which type of notification to display

        String page = getIntent().getStringExtra("page");

        notificationTitle = findViewById(R.id.notificationTitle);

        if (page.equals("PreOrder")) {

            notificationTitle.setText("PreOrder Updates");

            listView = findViewById(R.id.preOrderNotificationExpandableListView);
            initDataForPreOrder();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    preOrderListAdapter = new PreOrderNotificationAdapter(PreOrderUpdates.this, listDataHeader, preOrderListHash);
                    listView.setAdapter(preOrderListAdapter);


                }
            }, 1000);

        }else{

            notificationTitle.setText("Walk-In Order Updates");

            listView = findViewById(R.id.preOrderNotificationExpandableListView);
            initDataForWalkIn();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms

                    walkInListAdapter = new WalkInNotificationAdapter(PreOrderUpdates.this, listDataHeader, walkInListHash);
                    listView.setAdapter(walkInListAdapter);


                }
            }, 1000);
        }
        //endregion

    }

    private void initDataForPreOrder(){
        listDataHeader = new ArrayList<>();
        preOrderListHash = new HashMap<>();
        DatabaseReference drPreOrderNotification = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
        drPreOrderNotification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                preOrderHeaderList.clear();
                preOrderContentList.clear();


                int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString());

                for(int i =numOfPreOrder-1; i>=0;i--){
                    String foodName = dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString();
                    double foodPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("price").getValue().toString());
                    String dateTimeOrder = dataSnapshot.child(Integer.toString(i)).child("dateTimeOrder").getValue().toString();
                    int quantity = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("quantity").getValue().toString());
                    String stallName = dataSnapshot.child(Integer.toString(i)).child("stallName").getValue().toString();
                    int  stallId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("stallId").getValue().toString());
                    int  foodId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("foodId").getValue().toString());
                    double  totalPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("totalPrice").getValue().toString());
                    String  additionalNote = dataSnapshot.child(Integer.toString(i)).child("additionalNote").getValue().toString();
                    String  lastChanges = dataSnapshot.child(Integer.toString(i)).child("lastChanges").getValue().toString();
                    int  lastChangesInMin = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("lastChangesInMin").getValue().toString());
                    String tId = dataSnapshot.child(Integer.toString(i)).child("tId").getValue().toString();
                    String startTime = dataSnapshot.child(Integer.toString(i)).child("startTime").getValue().toString();
                    String endTime = dataSnapshot.child(Integer.toString(i)).child("endTime").getValue().toString();
                    String customerUID= dataSnapshot.child(Integer.toString(i)).child("customerUID").getValue().toString();
                    String stallUID= dataSnapshot.child(Integer.toString(i)).child("stallUID").getValue().toString();
                    String status= dataSnapshot.child(Integer.toString(i)).child("status").getValue().toString();
                    String notificationTiming = dataSnapshot.child(Integer.toString(i)).child("notificationTiming").getValue().toString();
                    String image = dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();
                    String school = dataSnapshot.child(Integer.toString(i)).child("school").getValue().toString();


                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    addOnList.clear();
                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                    for (int h =0; h<numOfAddOn;h++){
                        String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                        double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                        AddOn addOn = new AddOn(addOnName, addOnPrice);
                        addOnList.add(addOn);
                    }

                    NotificationHeader notificationHeader = new NotificationHeader(foodName, status, notificationTiming, image);
                    preOrderHeaderList.add(notificationHeader);

                    Collection collection = new Collection(foodName, foodPrice, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList,additionalNote,lastChanges,lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status, image,school);
                    preOrderContentList.add(collection);
                }

                for (int i =0;i<preOrderHeaderList.size();i++){
                   // listDataHeader.add(new NotificationHeader(headerList.get(i).getTitle(), headerList.get(i).getStatus(), headerList.get(i).getCurrentTime()));
                    listDataHeader.add(preOrderHeaderList.get(i));

                    ArrayList<Collection> stringList = new ArrayList<>();
                    stringList.add(preOrderContentList.get(i));

                    preOrderListHash.put(listDataHeader.get(i), stringList);
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


    private void initDataForWalkIn() {
        listDataHeader = new ArrayList<>();
        walkInListHash = new HashMap<>();
        DatabaseReference drWalkInNotification = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("walkIn");
        drWalkInNotification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                walkInHeaderList.clear();
                walkInContentList.clear();


                int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfWalkIn").getValue().toString());

                for (int i = numOfPreOrder-1; i >=0; i--) {
                    String foodName = dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString();
                    double foodPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("price").getValue().toString());
                    String dateTimeOrder = dataSnapshot.child(Integer.toString(i)).child("dateTimeOrder").getValue().toString();
                    int quantity = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("quantity").getValue().toString());
                    String stallName = dataSnapshot.child(Integer.toString(i)).child("stallName").getValue().toString();
                    int stallId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("stallId").getValue().toString());
                    int foodId = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("foodId").getValue().toString());
                    double totalPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("totalPrice").getValue().toString());
                    String tId = dataSnapshot.child(Integer.toString(i)).child("tId").getValue().toString();
                    String customerUID = dataSnapshot.child(Integer.toString(i)).child("customerUID").getValue().toString();
                    String stallUID = dataSnapshot.child(Integer.toString(i)).child("stallUID").getValue().toString();
                    String status = "Completed";
                    String image = dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();
                    String school = dataSnapshot.child(Integer.toString(i)).child("school").getValue().toString();


                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    addOnList.clear();
                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                    for (int h = 0; h < numOfAddOn; h++) {
                        String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                        double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                        AddOn addOn = new AddOn(addOnName, addOnPrice);
                        addOnList.add(addOn);
                    }

                    NotificationHeader notificationHeader = new NotificationHeader(foodName, status, dateTimeOrder, image);
                    walkInHeaderList.add(notificationHeader);

                    WalkIn walkIn = new WalkIn(foodName, foodPrice, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList, tId, customerUID, stallUID, image, school);
                    walkInContentList.add(walkIn);
                }

                for (int i = 0; i < walkInHeaderList.size(); i++) {
                    listDataHeader.add(walkInHeaderList.get(i));

                    ArrayList<WalkIn> stringList = new ArrayList<>();
                    stringList.add(walkInContentList.get(i));

                    walkInListHash.put(listDataHeader.get(i), stringList);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//
//
//
//
//    }

    }



}
