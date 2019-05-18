package sg.edu.rp.c346.c300.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.R;

public class ListDetails {





    public static String school ="";

    public static final ArrayList<Food> menuList = new ArrayList<>();

    public static final ArrayList<AddOn> addOnList = new ArrayList<>();



    public static ArrayList<Food> getFoodList(){



        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final FirebaseUser mUser = mAuth.getCurrentUser();


        if (mUser!=null){
            final DatabaseReference databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child("Customer").child(mUser.getUid());


            databaseReferenceUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Customer customer = dataSnapshot.getValue(Customer.class);
                    school = customer.getCustomerschool().toString();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



            final DatabaseReference databaseReferenceFood = FirebaseDatabase.getInstance().getReference().child("menu").child("school");

            databaseReferenceFood.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    String numOfSchool = dataSnapshot.child("numOfSchool").getValue().toString();
                    Log.d("What is the numOFSchool","What is the numOfSchool"+numOfSchool);
                    int intNumOfSchool = Integer.parseInt(numOfSchool);


                    String numOfStall = dataSnapshot.child(school).child("stall").child("numOfStall").getValue().toString();
                    Log.d("What is the numOfStall", "What is the numOfStall: "+numOfStall);
                    int intNumOfStall = Integer.parseInt(numOfStall);
                    Log.d("What is the numOfStall", "What is the intnumOfStall: "+intNumOfStall);

                    menuList.clear();

                    for(int i =0; i<intNumOfStall;i++){
                        String num= Integer.toString(i);
                        Log.d("What is num", "What is the num for the stall: "+num);
                        Log.d("What is the stall name", "What is the stall name: "+ dataSnapshot.child("0").child("stall").child(num).child("stallName").getValue());

                        String numOfFood =  dataSnapshot.child(school).child("stall").child(num).child("food").child("numOfFood").getValue().toString();
                        int intNumOfFood = Integer.parseInt(numOfFood);

                      //region for menuList
//                        Food food = new Food(R.drawable.fishball,dataSnapshot.child("0").child("stall").child(num).child("stallName").getValue().toString(), 2.5);
//                        menuList.add(food);
                       //endregion


                        //region for foodList
                        for (int h =0; h<intNumOfFood;h++){
                            String StringNumOfFood = Integer.toString(h);
                            String name = dataSnapshot.child(school).child("stall").child(num).child("food").child(StringNumOfFood).child("name").getValue().toString();
                            String price = dataSnapshot.child(school).child("stall").child(num).child("food").child(StringNumOfFood).child("price").getValue().toString();
                            String lastChanges = dataSnapshot.child(school).child("stall").child(num).child("food").child(StringNumOfFood).child("lastChanges").getValue().toString();
                            String stallName = dataSnapshot.child(school).child("stall").child(num).child("StallName").getValue().toString();
                            int stallId = Integer.parseInt(num);
                            int foodId = h;
                            //Food food = dataSnapshot.child("0").child("stall").child(num).child("food").child(StringNumOfFood).getValue(Food.class);
                            menuList.add(new Food(name, Double.parseDouble(price), Integer.parseInt(lastChanges),stallName, school,stallId,foodId));
                        }
                        //endregion





                    }


                    //region For json (useless)
//                    for (DataSnapshot ds: dataSnapshot.getChildren()){
//
//                        Log.d("Firebase Database ds", "What is ds: "+ds.getValue().toString());
//
//                            Object object = dataSnapshot.getValue(Object.class);
//                            String json = gson.toJson(object);
//                            Menu menu = gson.fromJson(json, Menu.class);
//                            //menuList.add(menu);
//
//                            Log.d("Tell me the menu", "What is the menu"+menu);
//                            Log.d("Firebase Database ds", "What is the json: "+json);
//
//                            //menuList.add(ds.getValue(Menu.class));
//                            //menuList.add(gson.fromJson(ds.getValue().toString(), Menu.class));
//                    }

                    //endregion

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



        }

        //Log.d(":::::what is menulist", "----------What is the overall menuList: "+menuList.get(0).getName());
        return menuList;



    }



    public static ArrayList<String> getMenuList(){

        return null;
    }



}
