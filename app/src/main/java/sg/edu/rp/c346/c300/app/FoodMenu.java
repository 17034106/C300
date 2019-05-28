package sg.edu.rp.c346.c300.app;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.CartDisplay;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.adapter.FoodAdapter;
import sg.edu.rp.c346.c300.model.Food;
import sg.edu.rp.c346.c300.model.ListDetails;
import sg.edu.rp.c346.c300.model.Menu;


public class FoodMenu extends Fragment {

    EditText username;

    GridLayout gridLayout;

    //For new layout
    private ListView listView;
    private ArrayList<Food> foods;
    private FoodAdapter foodAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_food_menu, container, false);

        View v = inflater.inflate(R.layout.fragment_food_menu, container, false); // this will be become the view due to fragment

        username = v.findViewById(R.id.etSearchbar);




        //for displaying out the menu
        listView = v.findViewById(R.id.list_view);
        foods = ListDetails.getFoodList();


        foodAdapter = new FoodAdapter(getActivity(), foods);
        listView.setAdapter(foodAdapter);


        //region Does not work
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Food food = foods.get(position);
                Toast.makeText(getActivity(), "Click Item Name: "+food.getName(), Toast.LENGTH_SHORT).show();
            }
        });
        //endregion


        setListViewHeightBasedOnChildren(listView); //use for listview in scrollview



        v.findViewById(R.id.btnShoppingCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartDisplay.class);
                startActivity(intent);

            }
        });




        return v;

    }

    //Use for listview in scrollview
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

}
