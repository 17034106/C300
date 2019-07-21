package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import sg.edu.rp.c346.c300.IndividualCollectionOrder;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Collection;

public class Collection2Adapter extends BaseAdapter {

    ArrayList<Object> list;
    private static final int COLLECTION_ITEM=0;
    private static final int HEADER=1;
    private LayoutInflater inflater;
    private Context context;
    public static ArrayList<AddOn> addOnListIndividual = new ArrayList<>(); //use for intent the addOnList to IndividualCollectionOrder class;


    public Collection2Adapter(Context context, ArrayList<Object> list) {
        this.list = list;
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof Collection){
            return COLLECTION_ITEM;
        }
        else{
            return  HEADER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            switch ( (getItemViewType(position))){
                case COLLECTION_ITEM:
                    convertView = inflater.inflate(R.layout.collection_all_list_view_item,null);
                    break;

                case HEADER:
                    convertView = inflater.inflate(R.layout.collection_all_list_view_header,null);
                    break;
            }
        }

        switch ( (getItemViewType(position))){
            case COLLECTION_ITEM:

                TextView tvTID = convertView.findViewById(R.id.tvCollectionTID);
                TextView collectionStallName = convertView.findViewById(R.id.CollectionStallName);
                ImageView collectionFoodImage = convertView.findViewById(R.id.CollectionfoodImage);
                TextView tvCollectionFoodName = convertView.findViewById(R.id.CollectionfoodName);
                TextView tvCollectionFoodPrice = convertView.findViewById(R.id.CollectionfoodPrice);
                TextView tvCollectionOrderTime = convertView.findViewById(R.id.CollectionOrderTiming);
                final TextView cartQuantity = convertView.findViewById(R.id.CollectionQuantity);
                TextView addon = convertView.findViewById(R.id.tvCollectionAddOn);
                TextView additionalNote = convertView.findViewById(R.id.tvCollectionAdditionalNote);
                final TextView totalPriceIndividual = convertView.findViewById(R.id.CollectionTotalPriceIndividual);
                TextView tvStartTime = convertView.findViewById(R.id.tvStartTime);
                TextView tvEndTime = convertView.findViewById(R.id.tvEndTime);
                TextView tvLastChanges = convertView.findViewById(R.id.CollectionlastChanges);
                TextView tvSchool = convertView.findViewById(R.id.CollectionSchool);

                final Collection collection = (Collection) list.get(position);
                ArrayList<AddOn> addOnThing = ((Collection)list.get(position)).getAddOn();

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
                tvLastChanges.setText("Last Editable: "+collection.getLastChanges());
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
                        intent.putExtra("lastChangesInMin", collection.getLastChangesInMin());
                        intent.putExtra("tId", collection.gettId());
                        intent.putExtra("startTime", collection.getStartTime());
                        intent.putExtra("endTime", collection.getEndTime());
                        intent.putExtra("status", collection.getStatus());
                        intent.putExtra("image", collection.getImage());
                        intent.putExtra("customerUID", collection.getCustomerUID());
                        intent.putExtra("stallUID", collection.getStallUID());
                        intent.putExtra("school", collection.getSchool());
                        addOnListIndividual = collection.getAddOn();
                        context.startActivity(intent);
                    }
                });

                break;

            case HEADER:
                TextView title = convertView.findViewById(R.id.itemListViewHeader);
                title.setText(((String)list.get(position)));
                break;
        }

        return convertView;


    }
}
