package sg.edu.rp.c346.c300.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.Food_display;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.TestingParentTransactionPreOrderIndividual;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.Food;

public class FoodAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<Food> foods;


    ArrayList<Integer> jurongPointDenyList = MainpageActivity.jurongPointDenyList;
    ArrayList<Integer> republicPolytechnicDenyList = MainpageActivity.republicPolytechnicDenyList;
    ArrayList<Integer> singaporePolytechnicDenyList = MainpageActivity.singaporePolytechnicDenyList;


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
        ImageView image = convertView.findViewById(R.id.foodStallImage);

        final Food food = foods.get(position);

//        images.setImageResource(food.getImageId());
        name.setText("Name: "+food.getName());
        price.setText(String.format("Price: $%.2f",food.getPrice()));
        stallName.setText("Stall: "+food.getStallName());
        Glide.with(context).load(food.getImage()).centerCrop().into(image);


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean allow = true;
                if (food.getSchool().equals("Jurong Point")){
                    for (Integer i : jurongPointDenyList){
                        if (i == food.getStallId()){
                            allow = false;
                            break;
                        }
                    }
                }
                else if (food.getSchool().equals("Republic Polytechnic")){
                    for (Integer i : republicPolytechnicDenyList){
                        if (i == food.getStallId()){
                            allow = false;
                            break;
                        }
                    }
                }
                else if (food.getSchool().equals("Singapore Polytechnic")){
                    for (Integer i : singaporePolytechnicDenyList){
                        if (i == food.getStallId()){
                            allow = false;
                            break;
                        }
                    }
                }



                if (allow) { // if the user is not deny
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
                    intent.putExtra("stallId", food.getStallId());
                    intent.putExtra("image", food.getImage());
                    intent.putExtra("stallUID", food.getStallUID());
                    context.startActivity(intent);

                }
                else{// if the user is deny
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    break;

                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("Purchase from this stall is denied\nPlease contact your parent").setPositiveButton("Okay", dialogClickListener)
                            .show();
                }



            }
        });





        return convertView;
    }


    public void filterList(ArrayList<Food> filteredList) {
        foods = filteredList;
        notifyDataSetChanged();
    }

}
