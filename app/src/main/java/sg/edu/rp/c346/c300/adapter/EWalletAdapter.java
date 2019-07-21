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
import sg.edu.rp.c346.c300.model.EWallet;
import sg.edu.rp.c346.c300.model.Food;
import sg.edu.rp.c346.c300.model.GoalSaving;

public class EWalletAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<EWallet> eWalletList;

    public EWalletAdapter(Context context, ArrayList<EWallet> eWalletList) {
        this.context = context;
        this.eWalletList = eWalletList;
    }

    @Override
    public int getCount() {
        return eWalletList.size();
    }

    @Override
    public Object getItem(int position) {
        return eWalletList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = View.inflate(context, R.layout.ewallet_item, null);
        }

        EWallet eWallet = eWalletList.get(position);

        TextView category = convertView.findViewById(R.id.EWalletAllCategory);
        TextView amount = convertView.findViewById(R.id.EWalletAllAmount);
        TextView comment = convertView.findViewById(R.id.EWalletAllComment);
        TextView status = convertView.findViewById(R.id.EWalletAllStatus);
        TextView num = convertView.findViewById(R.id.EWalletAllNo);


        String categoryString  = eWallet.getCategory().substring(0,1).toUpperCase()+eWallet.getCategory().substring(1).toLowerCase();

        category.setText("Category: "+categoryString);
        amount.setText(String.format("Amount: $%.2f", eWallet.getAmount()));
        comment.setText(String.format("Comment: %s", eWallet.getComment()));
        status.setText(String.format("Status: %s", eWallet.getStatus()));
        num.setText((position+1)+"");




        return convertView;
    }
}
