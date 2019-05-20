package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.Food_display;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.Food;

public class FoodAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<Food> foods;

    public FoodAdapter(Context context, ArrayList<Food> foods) {
        this.context = context;
        this.foods = foods;
    }

    @Override
    public int getCount() {
        return foods.size();
    }

    @Override
    public Object getItem(int position) {
        return foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null){
            convertView = View.inflate(context, R.layout.list_items, null);
        }


//        ImageView images = convertView.findViewById(R.id.foodStallImage);
        TextView name = convertView.findViewById(R.id.foodName);
        TextView price = convertView.findViewById(R.id.foodPrice);
        TextView stallName = convertView.findViewById(R.id.foodStallName);

        final Food food = foods.get(position);

//        images.setImageResource(food.getImageId());
        name.setText("Name: "+food.getName());
        price.setText(String.format("Price: $%.2f",food.getPrice()));
        stallName.setText("Stall: "+food.getStallName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Food_display.class);
                intent.putExtra("foodName", food.getName());
                intent.putExtra("foodPrice", food.getPrice());
                intent.putExtra("stallName", food.getStallName());
                intent.putExtra("lastChanges", food.getLastChanges());
                intent.putExtra("school", food.getSchool());
                intent.putExtra("stallId", food.getStallId());
                intent.putExtra("foodId", food.getFoodId());
                intent.putExtra("startTime",food.getStartTime());
                intent.putExtra("endTime", food.getEndTime());
                context.startActivity(intent);

            }
        });




        return convertView;
    }
}
