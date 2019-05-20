package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.AddOn;
import sg.edu.rp.c346.c300.model.Cart;

public class CartAdapter extends BaseAdapter {

    final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseUser mUser = mAuth.getCurrentUser();


    public CartAdapter(Context context, ArrayList<Cart> carts) {
        this.context = context;
        this.carts = carts;
    }

    Context context;
    ArrayList<Cart> carts;

    @Override
    public int getCount() {
        return carts.size();
    }

    @Override
    public Object getItem(int position) {
        return carts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView ==null) {
            convertView = View.inflate(context, R.layout.cart_items, null);
        }

        TextView cartStallName = convertView.findViewById(R.id.CartStallName);
        ImageView cartFoodImage = convertView.findViewById(R.id.CartfoodStallImage);
        TextView tvCartFoodName = convertView.findViewById(R.id.foodName);
        TextView tvCartFoodPrice = convertView.findViewById(R.id.foodPrice);
        TextView tvCartOrderTime = convertView.findViewById(R.id.orderTiming);
        final ElegantNumberButton cartQuantity = convertView.findViewById(R.id.cartQuantity);
        TextView addon = convertView.findViewById(R.id.tvAddOn);
        TextView additionalNote = convertView.findViewById(R.id.tvAdditionalNote);
        final TextView totalPriceIndividual = convertView.findViewById(R.id.totalPriceIndividual);
        TextView tvStartTime = convertView.findViewById(R.id.tvStartTime);
        TextView tvEndTime = convertView.findViewById(R.id.tvEndTime);


        final Cart cart = carts.get(position);
       ArrayList<AddOn> addOnThing = carts.get(position).getAddOnList();

        String addOnValue="";
        ArrayList<AddOn> addOnList = cart.getAddOnList();
        Log.d("what is the size", "457845 What is the size: "+ addOnList.size());
        for (int i =0; i<addOnThing.size();i++){
            addOnValue += String.format("%-30s$%.2f\n", addOnThing.get(i).getName().trim(), addOnThing.get(i).getPrice());

        }

        String inputPattern = "HHmm";
        String outputPattern = "h:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        String startTimeDisplay=null;
        String endTimeDisplay =null;

        try{
            Date startTimeDate = inputFormat.parse(cart.getStartTime());
            Date endTimeDate = inputFormat.parse(cart.getEndTime());
            startTimeDisplay = outputFormat.format(startTimeDate);
            endTimeDisplay = outputFormat.format(endTimeDate);
        }
        catch (ParseException e){

        }

        tvStartTime.setText(startTimeDisplay);
        tvEndTime.setText(endTimeDisplay);


        cartStallName.setText(cart.getStallName());
        cartFoodImage.setImageResource(R.drawable.fishball);
        tvCartFoodName.setText(cart.getName());
        tvCartFoodPrice.setText(String.format("$%.2f", cart.getPrice()));
        tvCartOrderTime.setText(cart.getDateTimeOrder());
        cartQuantity.setNumber(Integer.toString(cart.getQuantity()));
        totalPriceIndividual.setText(String.format("$%.2f", cart.getTotalPrice()));
        additionalNote.setText(cart.getAdditionalNote());

        if (addOnValue.isEmpty()){
            addon.setText("No Add On");
        }
        else{
            addon.setText(addOnValue.trim());
        }

        cartQuantity.setOnClickListener(new ElegantNumberButton.OnClickListener() {
            @Override
            public void onClick(View view) {
//                cart.setQuantity(Integer.parseInt(cartQuantity.getNumber()));
//                carts.get(position).setQuantity(Integer.parseInt(cartQuantity.getNumber()));

                final DatabaseReference databaseReferenceCart = FirebaseDatabase.getInstance().getReference().child("cart").child(mUser.getUid()).child(Integer.toString(position)).child("quantity");
                databaseReferenceCart.setValue(cartQuantity.getNumber());

                double individual = cart.getTotalPrice() / cart.getQuantity();
                totalPriceIndividual.setText(String.format("$%.2f", individual * Integer.parseInt(cartQuantity.getNumber())));
                FirebaseDatabase.getInstance().getReference().child("cart").child(mUser.getUid()).child(Integer.toString(position)).child("totalPrice").setValue(individual * Integer.parseInt(cartQuantity.getNumber()));


            }
        });




        return convertView;
    }
}
