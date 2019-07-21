package sg.edu.rp.c346.c300.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.Food_display;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.Food;
import sg.edu.rp.c346.c300.model.ItemOutside;
import sg.edu.rp.c346.c300.model.OutsideSendOrder;

public class EReciptAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<OutsideSendOrder> outsideSendOrderList;


    ArrayList<Integer> jurongPointDenyList = MainpageActivity.jurongPointDenyList;
    ArrayList<Integer> republicPolytechnicDenyList = MainpageActivity.republicPolytechnicDenyList;
    ArrayList<Integer> singaporePolytechnicDenyList = MainpageActivity.singaporePolytechnicDenyList;


    public EReciptAdapter(Context context, ArrayList<OutsideSendOrder> outsideSendOrderList) {
        this.context = context;
        this.outsideSendOrderList = outsideSendOrderList;
    }

    @Override
    public int getCount() {
        return outsideSendOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return outsideSendOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if (convertView == null){
            convertView = View.inflate(context, R.layout.e_receipt, null);
        }


        TextView stallName = convertView.findViewById(R.id.stallName);
        TextView tId = convertView.findViewById(R.id.tId);
        TextView dateOrder = convertView.findViewById(R.id.dateOrder);
        TextView timeOrder = convertView.findViewById(R.id.timeOrder);
        TextView school = convertView.findViewById(R.id.school);
        TextView allName = convertView.findViewById(R.id.AllName);
        TextView allCategory = convertView.findViewById(R.id.AllCategory);
        TextView allQuantity = convertView.findViewById(R.id.AllQuantity);
        TextView allPrice = convertView.findViewById(R.id.AllPrice);
        TextView totalPrice = convertView.findViewById(R.id.TotalPrice);


        OutsideSendOrder outsideSendOrder = outsideSendOrderList.get(position);

        ArrayList<ItemOutside> itemOutsideList = outsideSendOrder.getItem();
        Log.d("Size", "What is the size of item: "+itemOutsideList.size());

        String allNameString = "";
        String allCategoryString = "";
        String allQuantityString = "";
        String allPriceString = "";

        for (ItemOutside i : itemOutsideList){
            allNameString += (i.getName()+"\n");
            allCategoryString += (i.getCategory()+"\n");
            allQuantityString += (i.getQuantity()+"\n");
            allPriceString += String.format("$%.2f\n",i.getPrice());
        }

        stallName.setText(Html.fromHtml("<u>"+outsideSendOrder.getStallName()+"</u>"));
        tId.setText("Transaction ID: "+outsideSendOrder.gettId());
        dateOrder.setText("Date: "+outsideSendOrder.getDateTimeOrder().substring(0,10));
        timeOrder.setText("Time: "+outsideSendOrder.getDateTimeOrder().substring(10));
        school.setText("Location: "+outsideSendOrder.getSchool());
        allName.setText(allNameString);
        allCategory.setText(allCategoryString);
        allQuantity.setText(allQuantityString);
        allPrice.setText(allPriceString);
        totalPrice.setText(String.format("Total Price: $%.2f", outsideSendOrder.getTotalPrice()));



        return convertView;
    }




}
