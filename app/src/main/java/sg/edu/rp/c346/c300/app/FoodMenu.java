package sg.edu.rp.c346.c300.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;

import sg.edu.rp.c346.c300.BudgetInformation;
import sg.edu.rp.c346.c300.CartDisplay;
import sg.edu.rp.c346.c300.EmergencyWalletAdd;
import sg.edu.rp.c346.c300.ParentAuthentication;
import sg.edu.rp.c346.c300.QrCodeScannerPay;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.TestingParentMain;
import sg.edu.rp.c346.c300.adapter.FoodAdapter;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Budget;
import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.model.Food;
import sg.edu.rp.c346.c300.model.ListDetails;
import sg.edu.rp.c346.c300.model.Menu;
import sg.edu.rp.c346.c300.model.PrePayment;


public class FoodMenu extends Fragment {

    EditText searchBar;

    TextView categoryFoodLeft, categoryDrinkLeft, categoryStationeryLeft, categoryOthersLeft;

    //For new layout
    private ListView listView;
    private FoodAdapter foodAdapter;

    String customerSchool;


    private ArrayList<Food> menuList = new ArrayList<>();


    MaterialBetterSpinner roleFoodSpinner;
    private ArrayList<String> roleFoodList = new ArrayList<>();
    ArrayAdapter<String> roleFoodAdapter;
    int spinnerPositionForSchool =-1; //default value for spinner is the customer's school

    Budget budget; // get the available budget for each category


    public static String changeBudgetTiming; // for checking whether the kid has set the budget or not for the day
    public static boolean allowToChange; // for checking whether the parent allows kid to reallocate the budget

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_food_menu, container, false);

        View v = inflater.inflate(R.layout.fragment_food_menu, container, false); // this will be become the view due to fragment


        listView = v.findViewById(R.id.list_view);
        searchBar = v.findViewById(R.id.etSearchbar);
        roleFoodSpinner = v.findViewById(R.id.spinnerRoleFood);

        categoryFoodLeft = v.findViewById(R.id.categoryFoodLeft);
        categoryDrinkLeft = v.findViewById(R.id.categoryDrinkLeft);
        categoryStationeryLeft = v.findViewById(R.id.categoryStationeryLeft);
        categoryOthersLeft = v.findViewById(R.id.categoryOthersLeft);

        getCategory();



        getAllRoleFood();
        roleFoodSpinner.setText(customerSchool);



        //region searchBar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
                setListViewHeightBasedOnChildren(listView); //use for listview in scrollview
            }
        });


        v.findViewById(R.id.imageCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBar.setText("");
            }
        });
        //endregion




        DatabaseReference drCustomerSchool = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drCustomerSchool.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customerSchool = dataSnapshot.child("customerschool").getValue().toString();

                //for displaying out the menu
//                getFoodList(customerSchool);
                getFoodList(customerSchool);

                //region for spinner

                roleFoodSpinner.setText(customerSchool);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //use the spinner to choose the location menu
        roleFoodSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getFoodList(roleFoodList.get(position));
            }
        });








        v.findViewById(R.id.btnShoppingCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartDisplay.class);
                startActivity(intent);

            }
        });


        v.findViewById(R.id.relative2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BudgetInformation.class));
            }
        });




        return v;

    }

    //region for searchView
    public void filter(String text){
        ArrayList<Food> filteredList = new ArrayList<>();

        for (Food item : menuList) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        foodAdapter.filterList(filteredList);
    }
    //endregion




    //region Use for listview in scrollview
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
    //endregion


    //region get all foods
    public void getFoodList(final String school){


            final DatabaseReference databaseReferenceFood = FirebaseDatabase.getInstance().getReference().child("menu").child("school");

            databaseReferenceFood.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                    String numOfStall = dataSnapshot.child(school).child("stall").child("numOfStall").getValue().toString();
                    int intNumOfStall = Integer.parseInt(numOfStall);


                    menuList.clear();

                    for(int i =0; i<intNumOfStall;i++){

                        boolean isRoleFood = false;

                        for(DataSnapshot d : dataSnapshot.child(school).child("stall").child(i+"").getChildren()){
                            if (d.getKey().equals("food")){
                                isRoleFood = true;
                                break;
                            }
                        }

                        if(isRoleFood) {

                            String num = Integer.toString(i);
                            String numOfFood = dataSnapshot.child(school).child("stall").child(num).child("food").child("numOfFood").getValue().toString();
                            int intNumOfFood = Integer.parseInt(numOfFood);


                            //region for foodList
                            for (int h = 0; h < intNumOfFood; h++) {
                                String StringNumOfFood = Integer.toString(h);
                                String name = dataSnapshot.child(school).child("stall").child(num).child("food").child(StringNumOfFood).child("name").getValue().toString();
                                String price = dataSnapshot.child(school).child("stall").child(num).child("food").child(StringNumOfFood).child("price").getValue().toString();
                                String lastChanges = dataSnapshot.child(school).child("stall").child(num).child("food").child(StringNumOfFood).child("lastChanges").getValue().toString();
                                String stallName = dataSnapshot.child(school).child("stall").child(num).child("stallName").getValue().toString();
                                String startTime = dataSnapshot.child(school).child("stall").child(num).child("startTime").getValue().toString();
                                String endTime = dataSnapshot.child(school).child("stall").child(num).child("endTime").getValue().toString();
                                String image = dataSnapshot.child(school).child("stall").child(num).child("food").child(StringNumOfFood).child("imageurl").getValue().toString();
                                String stallUID = dataSnapshot.child(school).child("stall").child(num).child("stallUID").getValue().toString();
                                int stallId = Integer.parseInt(num);
                                int foodId = h;
                                menuList.add(new Food(name, Double.parseDouble(price), Integer.parseInt(lastChanges), stallName, school, stallId, foodId, startTime, endTime, image, stallUID));
                            }
                            //endregion
                        }

                        foodAdapter = new FoodAdapter(getActivity(), menuList);
                        listView.setAdapter(foodAdapter);
                        foodAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(listView); //use for listview in scrollview


                        MainpageActivity.dialog.dismiss(); // dismiss the dialog from MainpageActivity



                    }








                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




    }
    //endregion


    private void getAllRoleFood(){
        DatabaseReference drRoleFood = FirebaseDatabase.getInstance().getReference().child("roleFood");
        drRoleFood.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfRole = Integer.parseInt(dataSnapshot.child("numOfRole").getValue().toString());
                for (int i =0; i<numOfRole;i++){
                    roleFoodList.add(dataSnapshot.child(i+"").child("name").getValue().toString());
                }

                roleFoodAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,roleFoodList);
                roleFoodSpinner.setAdapter(roleFoodAdapter);
                roleFoodSpinner.setText(customerSchool);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void getCategory() {


        DatabaseReference drBudget = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day");
        drBudget.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Calendar calendar = Calendar.getInstance();
                int day1 = calendar.get(Calendar.DAY_OF_WEEK) - 2;

                if (day1 == -1) {
                    day1 = 6;
                }

                budget = dataSnapshot.child(day1+ "").getValue(Budget.class);


                categoryFoodLeft.setText(String.format("$%.2f", budget.getCategory().getFood().getLeft()));
                categoryDrinkLeft.setText(String.format("$%.2f", budget.getCategory().getDrink().getLeft()));
                categoryStationeryLeft.setText(String.format("$%.2f", budget.getCategory().getStationery().getLeft()));
                categoryOthersLeft.setText(String.format("$%.2f", budget.getCategory().getOthers().getLeft()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //region get the changeBudgetTiming to check whether the kid has set the budget for the day
    public void checkChangeBudgetTiming(){

        DatabaseReference drChangeBudgetTiming = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drChangeBudgetTiming.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                changeBudgetTiming = dataSnapshot.child("changeBudgetTiming").getValue().toString().trim();
                allowToChange = Boolean.parseBoolean(dataSnapshot.child("allowToChange").getValue().toString());

                //region check whether the kid has set the categorization or not
                String todayDate = MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy");

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity()); // for the if condition to display the dialog
                android.support.v7.app.AlertDialog alert1 = builder.create(); // for the if condition to display the dialog

                if (!todayDate.equals(changeBudgetTiming)){

                    builder.setTitle("Categorization");
                    builder.setPositiveButton("Set Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getActivity(), BudgetInformation.class);
                            intent.putExtra("firstTime", true);
                            startActivity(intent);
                        }
                    });
                    builder.setNeutralButton("Parent Account", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent1 = new Intent(getActivity(), ParentAuthentication.class);
                            startActivity(intent1);
                        }
                    });
                    builder.setMessage("Please set the categorization first");
                    builder.setCancelable(false);
                    alert1 = builder.create();
                    alert1.show();


                }
                else{
                    alert1.dismiss();
                }
                //endregion

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //endregion


    @Override
    public void onResume() {
        super.onResume();
        checkChangeBudgetTiming();
    }




}
