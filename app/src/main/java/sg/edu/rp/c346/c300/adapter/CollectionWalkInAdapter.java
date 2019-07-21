package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.TestingParentTransactionPreOrderIndividual;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.WalkIn;

public class CollectionWalkInAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<WalkIn> collectionList;




    public static ArrayList<AddOn> addOnListIndividual = new ArrayList<>(); //use for intent the addOnList to IndividualCollectionOrder class;

    public CollectionWalkInAdapter(Context context, ArrayList<WalkIn> collectionList) {
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
            convertView = View.inflate(context, R.layout.collection_item_walk_in, null);
        }


        TextView tvTID = convertView.findViewById(R.id.tvCollectionTID);
        TextView collectionStallName = convertView.findViewById(R.id.CollectionStallName);
        ImageView collectionFoodImage = convertView.findViewById(R.id.CollectionfoodImage);
        TextView tvCollectionFoodName = convertView.findViewById(R.id.CollectionfoodName);
        TextView tvCollectionFoodPrice = convertView.findViewById(R.id.CollectionfoodPrice);
        TextView tvCollectionOrderTime = convertView.findViewById(R.id.CollectionOrderTiming);
        final TextView cartQuantity = convertView.findViewById(R.id.CollectionQuantity);
        TextView addon = convertView.findViewById(R.id.tvCollectionAddOn);
        final TextView totalPriceIndividual = convertView.findViewById(R.id.CollectionTotalPriceIndividual);
        TextView tvSchool = convertView.findViewById(R.id.CollectionSchool);


        final WalkIn collection = collectionList.get(position);
        ArrayList<AddOn> addOnThing = collectionList.get(position).getAddOn();

        String addOnValue="";
        final ArrayList<AddOn> addOnList = collection.getAddOn();
        Log.d("what is the size", "457845 What is the size: "+ addOnList.size());
        for (int i =0; i<addOnThing.size();i++){
            addOnValue += String.format("%-30s$%.2f\n", addOnThing.get(i).getName().trim(), addOnThing.get(i).getPrice());

        }

        String inputPattern = "HHmm";
        String outputPattern = "h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);



        tvTID.setText(collection.gettId());
        collectionStallName.setText(collection.getStallName());
        collectionFoodImage.setImageResource(R.drawable.fishball);
        tvCollectionFoodName.setText(collection.getName());
        tvCollectionFoodPrice.setText(String.format("$%.2f", collection.getPrice()));
        tvCollectionOrderTime.setText("Collection Time: "+collection.getDateTimeOrder());
        cartQuantity.setText(String.format("x%d",collection.getQuantity()));
        totalPriceIndividual.setText(String.format("$%.2f", collection.getTotalPrice()));
        Glide.with(context).load(collection.getImage()).centerCrop().into(collectionFoodImage);
        tvSchool.setText("Location: "+collection.getSchool());


        if (addOnValue.isEmpty()){
            addon.setText("No Add On");
        }
        else{
            addon.setText(addOnValue.trim());
        }



//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(context, TestingParentTransactionPreOrderIndividual.class);
//                intent.putExtra("item", collection);
//                context.startActivity(intent);
//
//            }
//        });



//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(context, IndividualCollectionOrder.class);
//                intent.putExtra("foodName", collection.getName());
//                intent.putExtra("foodPrice", collection.getPrice());
//                intent.putExtra("dateTimeOrder", collection.getDateTimeOrder());
//                intent.putExtra("quantity", collection.getQuantity());
//                intent.putExtra("stallName", collection.getStallName());
//                intent.putExtra("stallId", collection.getStallId());
//                intent.putExtra("foodId", collection.getFoodId());
//                intent.putExtra("totalPrice", collection.getTotalPrice());
//                intent.putExtra("additionalNote", collection.getAdditionalNote());
//                intent.putExtra("lastChanges", collection.getLastChanges());
//                intent.putExtra("lastChangesInMin", collection.getLastChangesInMin());
//                Log.d("CollectionAdapter", "What is the lastChangesInMin: "+collection.getLastChangesInMin());
//                intent.putExtra("tId", collection.gettId());
//                intent.putExtra("startTime", collection.getStartTime());
//                intent.putExtra("endTime", collection.getEndTime());
//                intent.putExtra("status", collection.getStatus());
//                intent.putExtra("image", collection.getImage());
//                addOnListIndividual = collection.getAddOn();
//                context.startActivity(intent);
//            }
//        });










        return convertView;
    }



}
