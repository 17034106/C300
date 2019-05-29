package sg.edu.rp.c346.c300.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.IndividualCollectionOrder;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Cart;
import sg.edu.rp.c346.c300.model.Collection;

public class CollectionAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Collection> collectionList;

    public static ArrayList<AddOn> addOnListIndividual = new ArrayList<>(); //use for intent the addOnList to IndividualCollectionOrder class;

    public CollectionAdapter(Context context, ArrayList<Collection> collectionList) {
        this.context = context;
        this.collectionList = collectionList;
    }

    @Override
    public int getCount() {
        return collectionList.size();
    }

    @Override
    public Object getItem(int position) {
        return collectionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = View.inflate(context, R.layout.collection_items, null);
        }


        TextView tvTID = convertView.findViewById(R.id.tvCollectionTID);
        TextView cartStallName = convertView.findViewById(R.id.CollectionStallName);
        ImageView cartFoodImage = convertView.findViewById(R.id.CollectionfoodStallImage);
        TextView tvCartFoodName = convertView.findViewById(R.id.CollectionfoodName);
        TextView tvCartFoodPrice = convertView.findViewById(R.id.CollectionfoodPrice);
        TextView tvCartOrderTime = convertView.findViewById(R.id.CollectionOrderTiming);
        final TextView cartQuantity = convertView.findViewById(R.id.CollectionQuantity);
        TextView addon = convertView.findViewById(R.id.tvCollectionAddOn);
        TextView additionalNote = convertView.findViewById(R.id.tvCollectionAdditionalNote);
        final TextView totalPriceIndividual = convertView.findViewById(R.id.CollectionTotalPriceIndividual);
        TextView tvStartTime = convertView.findViewById(R.id.tvStartTime);
        TextView tvEndTime = convertView.findViewById(R.id.tvEndTime);
        TextView tvLastChanges = convertView.findViewById(R.id.CollectionlastChanges);


        final Collection collection = collectionList.get(position);
        ArrayList<AddOn> addOnThing = collectionList.get(position).getAddOnList();

        String addOnValue="";
        final ArrayList<AddOn> addOnList = collection.getAddOnList();
        Log.d("what is the size", "457845 What is the size: "+ addOnList.size());
        for (int i =0; i<addOnThing.size();i++){
            addOnValue += String.format("%-30s$%.2f\n", addOnThing.get(i).getName().trim(), addOnThing.get(i).getPrice());

        }

        String inputPattern = "HHmm";
        String outputPattern = "h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);



        tvTID.setText(collection.gettId());
        cartStallName.setText(collection.getStallName());
        cartFoodImage.setImageResource(R.drawable.fishball);
        tvCartFoodName.setText(collection.getName());
        tvCartFoodPrice.setText(String.format("$%.2f", collection.getPrice()));
        tvCartOrderTime.setText("Collection Time: "+collection.getDateTimeOrder());
        tvLastChanges.setText("Last Editable: "+collection.getLastChanges());
        cartQuantity.setText(String.format("x%d",collection.getQuantity()));
        totalPriceIndividual.setText(String.format("$%.2f", collection.getTotalPrice()));



        if (addOnValue.isEmpty()){
            addon.setText("No Add On");
        }
        else{
            addon.setText(addOnValue.trim());
        }

        if (collection.getAdditionalNote().isEmpty()){
            additionalNote.setText("No Additional Notes");
        }
        else{
            additionalNote.setText(collection.getAdditionalNote());

        }


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, IndividualCollectionOrder.class);
                intent.putExtra("foodName", collection.getName());
                intent.putExtra("foodPrice", collection.getPrice());
                intent.putExtra("dateTimeOrder", collection.getDateTimeOrder());
                intent.putExtra("quantity", collection.getQuantity());
                intent.putExtra("stallName", collection.getStallName());
                intent.putExtra("stallId", collection.getStallId());
                intent.putExtra("foodId", collection.getFoodId());
                intent.putExtra("totalPrice", collection.getTotalPrice());
                intent.putExtra("additionalNote", collection.getAdditionalNote());
                intent.putExtra("lastChanges", collection.getLastChanges());
                intent.putExtra("tId", collection.gettId());
                intent.putExtra("startTime", collection.getStartTime());
                intent.putExtra("endTime", collection.getEndTime());
                addOnListIndividual = collection.getAddOnList();
                context.startActivity(intent);
            }
        });




        return convertView;
    }
}
