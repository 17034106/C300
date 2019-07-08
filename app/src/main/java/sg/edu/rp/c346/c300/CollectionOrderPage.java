package sg.edu.rp.c346.c300;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.adapter.Collection2Adapter;
import sg.edu.rp.c346.c300.adapter.CollectionAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;

public class CollectionOrderPage extends AppCompatActivity {

    DatabaseReference drTC;

//    TextView displayNoCollection;

    ListView collectionListView;
    CollectionAdapter collectionAdapter;
    ArrayList<Collection> collectionList = new ArrayList<>();
    ArrayList<Collection> arrangeCollectionList = new ArrayList<>();

    Collection2Adapter collection2Adapter;
    ArrayList<Collection> readyCollectionList = new ArrayList<>();
    ArrayList<Collection> preparingCollectionList = new ArrayList<>();
    ArrayList<Collection> purchasedCollectionList = new ArrayList<>();
    ArrayList<Object> overallCollectionList = new ArrayList<>();



    static CollectionOrderPage thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_order_page);

        thisActivity = this;

//        displayNoCollection = findViewById(R.id.displayNoCollection);


        drTC = FirebaseDatabase.getInstance().getReference().child("tc").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("order");
        drTC.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfOrder = Integer.parseInt(dataSnapshot.child("numOfOrder").getValue().toString());
                collectionList.clear();
                for (int i =0; i<numOfOrder;i++){
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
                    String stallUID = dataSnapshot.child(Integer.toString(i)).child("stallUID").getValue().toString();
                    String status= dataSnapshot.child(Integer.toString(i)).child("status").getValue().toString();
                    String image= dataSnapshot.child(Integer.toString(i)).child("imageurl").getValue().toString();
                    String school= dataSnapshot.child(Integer.toString(i)).child("school").getValue().toString();



                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    addOnList.clear();
                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                    for (int h =0; h<numOfAddOn;h++){
                        String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                        double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                        AddOn addOn = new AddOn(addOnName, addOnPrice);
                        addOnList.add(addOn);
                    }

                    Collection collection = new Collection(foodName, foodPrice, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList,additionalNote,lastChanges,lastChangesInMin, tId, startTime, endTime, customerUID, stallUID, status, image,school);
                    collectionList.add(collection);
                }

                //region re-arrange the order based on the datetimeOrder (oldest should be at the top)
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
                while (collectionList.size()!=0){
                    int oldestIndex =0;
                    for (int h =0; h<collectionList.size();h++){
                        if (MainpageActivity.convertStringToDate(collectionList.get(oldestIndex).getDateTimeOrder(),"dd/MM/yyyy hh:mm a").compareTo(MainpageActivity.convertStringToDate(collectionList.get(h).getDateTimeOrder(),"dd/MM/yyyy hh:mm a"))>0){
                            oldestIndex = h;
                        }
                    }

                    ArrayList<Integer> repeatedList = new ArrayList<>();
                    repeatedList.clear();
                    for (int i =0; i<collectionList.size();i++){
                        if (collectionList.get(oldestIndex).getDateTimeOrder().compareTo(collectionList.get(i).getDateTimeOrder())==0){

                            arrangeCollectionList.add(collectionList.get(i));
                            repeatedList.add(i);
                        }
                    }


                    for (int i = repeatedList.size()-1; i>-1; i--){
                        collectionList.remove(collectionList.get(repeatedList.get(i)));
                    }


                }
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
                //endregion


                //region split everything based on the status
                for(int i=0; i<arrangeCollectionList.size();i++){
                    if (arrangeCollectionList.get(i).getStatus().equals("purchased")){
                        purchasedCollectionList.add(arrangeCollectionList.get(i));
//                        arrangeCollectionList.remove(arrangeCollectionList.get(i));
                    }
                    else if (arrangeCollectionList.get(i).getStatus().equals("preparing")){
                        preparingCollectionList.add(arrangeCollectionList.get(i));
//                        arrangeCollectionList.remove(arrangeCollectionList.get(i));
                    }
                    else if (arrangeCollectionList.get(i).getStatus().equals("ready")){
                        readyCollectionList.add(arrangeCollectionList.get(i));
//                        arrangeCollectionList.remove(arrangeCollectionList.get(i));
                    }
                }
//                Log.d("What is size arrange","What is the size of the ready: "+readyCollectionList.size());
//                Log.d("What is size arrange","What is the size of the preparing: "+preparingCollectionList.size());
//                Log.d("What is size arrange","What is the size of the purchased: "+purchasedCollectionList.size());


                overallCollectionList.add(new String("Ready"));
                for (Collection i : readyCollectionList){
                    overallCollectionList.add(i);
                }

                overallCollectionList.add(new String("Preparing"));
                for (Collection i : preparingCollectionList){
                    overallCollectionList.add(i);
                }

                overallCollectionList.add(new String("Purchased"));
                for (Collection i : purchasedCollectionList){
                    overallCollectionList.add(i);
                }


                //endregion





                collectionListView = findViewById(R.id.collectionOrderListView);
                collection2Adapter = new Collection2Adapter(CollectionOrderPage.this, overallCollectionList);
                collectionListView.setAdapter(collection2Adapter);




                //region if no order then display "No collection of order"
//                if (arrangeCollectionList.size()==0){
//                    displayNoCollection.setVisibility(TextView.VISIBLE);
//                }
//                else{
//                    displayNoCollection.setVisibility(TextView.INVISIBLE);
//                }
                //endregion


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public static CollectionOrderPage getInstance(){
        return thisActivity;
    }
}
