package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.CreditCard;

public class TopUpAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<CreditCard> topUpList;
    private String location;

    public TopUpAdapter(Context context,ArrayList<CreditCard> topUpList) {
        this.context = context;
        this.topUpList = topUpList;
    }

    @Override
    public int getCount() {
        return topUpList.size();
    }

    @Override
    public Object getItem(int position) {
        return topUpList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = View.inflate(context, R.layout.top_up_item, null);
        }

        CreditCard topUp = topUpList.get(position);

        TextView dateTime = convertView.findViewById(R.id.dateTime);
        TextView amount = convertView.findViewById(R.id.amount);


        dateTime.setText(topUp.getDateTime());
        amount.setText(String.format("$%.2f", topUp.getAmount()));



        return convertView;
    }
}
