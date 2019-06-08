package sg.edu.rp.c346.c300;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

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
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.NotificationHeader;

public class PreOrderUpdates extends AppCompatActivity {


    private ExpandableListView listView;
    private PreOrderNotificationAdapter listAdapter;
    private ArrayList<NotificationHeader> listDataHeader;
    private HashMap<NotificationHeader, ArrayList<Collection>> listHash;

    ArrayList<NotificationHeader> headerList = new ArrayList<>();
    ArrayList<Collection> contentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_order_updates);

        listView = findViewById(R.id.preOrderNotificationExpandableListView);
        initData();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms

                listAdapter = new PreOrderNotificationAdapter(PreOrderUpdates.this, listDataHeader, listHash);
                listView.setAdapter(listAdapter);

                Log.d("What is listHash", "What is listHash99: "+listHash);
            }
        }, 1000);

    }

    private void initData(){
        listDataHeader = new ArrayList<>();
        listHash = new HashMap<>();
        DatabaseReference drPreOrderNotification = FirebaseDatabase.getInstance().getReference().child("notification").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("preOrder");
        drPreOrderNotification.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                headerList.clear();
                contentList.clear();

                Log.d("nlabnfngfd", "-----------------------------------------------------------------------------------------------fghhfng bvnhgghngngh---");

                int numOfPreOrder = Integer.parseInt(dataSnapshot.child("numOfPreOrder").getValue().toString());

                for(int i =0; i<numOfPreOrder;i++){
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
                    String status= dataSnapshot.child(Integer.toString(i)).child("status").getValue().toString();
                    String notificationTiming = dataSnapshot.child(Integer.toString(i)).child("notificationTiming").getValue().toString();
                    String image = dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();


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
                    headerList.add(notificationHeader);

                    Collection collection = new Collection(foodName, foodPrice, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList,additionalNote,lastChanges,lastChangesInMin, tId, startTime, endTime, customerUID, status, image);
                    contentList.add(collection);
                }

                for (int i =0;i<headerList.size();i++){
                   // listDataHeader.add(new NotificationHeader(headerList.get(i).getTitle(), headerList.get(i).getStatus(), headerList.get(i).getCurrentTime()));
                    listDataHeader.add(headerList.get(i));

                    ArrayList<Collection> stringList = new ArrayList<>();
                    stringList.add(contentList.get(i));

                    listHash.put(listDataHeader.get(i), stringList);
                }


//                listDataHeader.add(new NotificationHeader("EDMTDev", "Purchased", Calendar.getInstance().getTime()+""));
//                listDataHeader.add(new NotificationHeader("Android", "Preparing", Calendar.getInstance().getTime()+""));
//                listDataHeader.add(new NotificationHeader("Xamarin", "Serving", Calendar.getInstance().getTime()+""));

                ArrayList<String> edmtDev = new ArrayList<>();
                edmtDev.add("This is Expandable ListView");

                ArrayList<String> androidStudio = new ArrayList<>();
                androidStudio.add("Expandable ListView");
                androidStudio.add("Google Map");
                androidStudio.add("Chat Application");
                androidStudio.add("Firebase");


                ArrayList<String> xamarin = new ArrayList<>();
                xamarin.add("xamarin Expandable ListView");
                xamarin.add("xamarin Google Map");
                xamarin.add("xamarin Chat Application");
                xamarin.add("xamarin Firebase");

                ArrayList<String> uwp = new ArrayList<>();
                uwp.add("uwp Expandable ListView");
                uwp.add("uwp Google Map");
                uwp.add("uwp Chat Application");
                uwp.add("uwp Firebase");



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }

}
