package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.adapter.CollectionAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Cart;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.Food;

public class CollectionOrderPage extends AppCompatActivity {

    DatabaseReference drTC;

    TextView displayNoCollection;

    ListView collectionListView;
    CollectionAdapter collectionAdapter;
    ArrayList<Collection> collectionList = new ArrayList<>();
    ArrayList<Collection> arrangeCollectionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_order_page);

        displayNoCollection = findViewById(R.id.displayNoCollection);


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
                    String tId = dataSnapshot.child(Integer.toString(i)).child("tId").getValue().toString();
                    String startTime = dataSnapshot.child(Integer.toString(i)).child("startTime").getValue().toString();
                    String endTime = dataSnapshot.child(Integer.toString(i)).child("endTime").getValue().toString();
                    Log.d("-=-=-=-=-=-=-=-=", "4554-=-=-=-=-=-=-=-=-=-: "+lastChanges);

                    ArrayList<AddOn> addOnList = new ArrayList<>();
                    addOnList.clear();
                    int numOfAddOn = Integer.parseInt(dataSnapshot.child(Integer.toString(i)).child("addOn").child("numOfAddOn").getValue().toString());
                    for (int h =0; h<numOfAddOn;h++){
                        String addOnName = dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("name").getValue().toString();
                        double addOnPrice = Double.parseDouble(dataSnapshot.child(Integer.toString(i)).child("addOn").child(Integer.toString(h)).child("price").getValue().toString());
                        AddOn addOn = new AddOn(addOnName, addOnPrice);
                        addOnList.add(addOn);
                    }

                    Collection collection = new Collection(foodName, foodPrice, dateTimeOrder, quantity, stallName, stallId, foodId, totalPrice, addOnList,additionalNote,lastChanges,tId, startTime, endTime);
                    collectionList.add(collection);
                    Log.d("jjgjhjhjhjhj", "kjkjbkjdabfkda What is TID: "+tId);
                }

                //region re-arrange the order based on the datetimeOrder (oldest should be at the top)
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
                while (collectionList.size()!=0){
                    int oldestIndex =0;
                    for (int h =0; h<collectionList.size();h++){
                        if (MainpageActivity.convertStringToDate(collectionList.get(oldestIndex).getDateTimeOrder(),"dd/MM/yyyy hh:mm a").compareTo(MainpageActivity.convertStringToDate(collectionList.get(h).getDateTimeOrder(),"dd/MM/yyyy hh:mm a"))>0){
                            oldestIndex = h;
                            Log.d("what is h", "What is h: "+h);
                        }
                    }

                    ArrayList<Integer> repeatedList = new ArrayList<>();
                    repeatedList.clear();
                    for (int i =0; i<collectionList.size();i++){
                        Log.d("What is oldest", "What is the oldest: "+oldestIndex);
                        if (collectionList.get(oldestIndex).getDateTimeOrder().compareTo(collectionList.get(i).getDateTimeOrder())==0){
                            Log.d("What is oldestIndex: ","What is the oldestIndex: "+collectionList.get(oldestIndex).getDateTimeOrder());
                            Log.d("What is newIndex: ","What is the newIndex: "+collectionList.get(i).getDateTimeOrder());
                            arrangeCollectionList.add(collectionList.get(i));
                            repeatedList.add(i);
                        }
                    }

                    Log.d("What is the size", "What is the size of the collectionList Before: "+collectionList.size());
                    Log.d("What is repeatedList", "What is repeatedList: "+repeatedList);
                    for (int i = repeatedList.size()-1; i>-1; i--){
                        Log.d("Remove which one:", "Your remove which one: "+repeatedList.get(i));
                        collectionList.remove(collectionList.get(repeatedList.get(i)));
                        Log.d("What is the size", "What is the size of the collectionList After: "+collectionList.size());
                    }


                }
                //-----------------------------------------------------------------------------------------------------------------------------------------------------------------------
                //endregion

                collectionListView = findViewById(R.id.collectionOrderListView);
                collectionAdapter = new CollectionAdapter(CollectionOrderPage.this, arrangeCollectionList);
                collectionListView.setAdapter(collectionAdapter);




                if (arrangeCollectionList.size()==0){
                    displayNoCollection.setVisibility(TextView.VISIBLE);
                }
                else{
                    displayNoCollection.setVisibility(TextView.INVISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


















    }
}
