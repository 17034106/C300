package sg.edu.rp.c346.c300.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.IndividualCollectionOrder;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.TestingParentTransactionPreOrderAll;
import sg.edu.rp.c346.c300.TestingParentTransactionPreOrderIndividual;
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


        final Collection collection = collectionList.get(position);
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

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                setDeny(collection);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure to deny kid to purchase any item from this stall?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

            }
        });



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

    private void setDeny(final Collection item){
        final DatabaseReference drDenyList = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school").child(item.getSchool());
        drDenyList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfDeny = Integer.parseInt(dataSnapshot.child("numOfDeny").getValue().toString());

                for (int i =0; i<numOfDeny;i++){
                    if (Integer.parseInt(dataSnapshot.child(i+"").getValue().toString()) == (item.getStallId())){
                        Toast.makeText(context, "Already Denied", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                drDenyList.child(numOfDeny+"").setValue(item.getStallId());
                drDenyList.child("numOfDeny").setValue(numOfDeny+1);
                Toast.makeText(context, "Denied Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


}
