package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;

import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.Collection;
import sg.edu.rp.c346.c300.model.NotificationContentPreOrder;
import sg.edu.rp.c346.c300.model.NotificationHeader;

public class PreOrderNotificationAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<NotificationHeader> listDataHeader;
    private HashMap<NotificationHeader, ArrayList<Collection>> listHashMap;

    public PreOrderNotificationAdapter(Context context, ArrayList<NotificationHeader> listDataHeader, HashMap<NotificationHeader, ArrayList<Collection>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if ((convertView == null)){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pre_order_updates_list_group, null);
        }

        TextView title = convertView.findViewById(R.id.notificationTitle);
        title.setTypeface(null, Typeface.BOLD);
        TextView content = convertView.findViewById(R.id.notificationContent);
        TextView timing = convertView.findViewById(R.id.notificationCurrentTiming);
        ImageView image = convertView.findViewById(R.id.notificationImage);

        final NotificationHeader notificationHeader = listDataHeader.get(groupPosition);


        title.setText(notificationHeader.getTitle());
        content.setText(notificationHeader.getStatus());
        timing.setText(notificationHeader.getCurrentTime());
        Glide.with(context).load(notificationHeader.getImage()).centerCrop().into(image);


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Collection childText = (Collection) getChild(groupPosition, childPosition);
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.pre_order_updates_list_items, null);
        }

        TextView tvTid = convertView.findViewById(R.id.tID);
        TextView tvQuantity = convertView.findViewById(R.id.quantity);
        TextView tvAddOn = convertView.findViewById(R.id.addOn);
        TextView tvAdditionalNotes= convertView.findViewById(R.id.additionalNotes);
        TextView tvPrice = convertView.findViewById(R.id.price);
        TextView tvOrderTiming = convertView.findViewById(R.id.orderTiming);


        tvTid.setText("TID: "+childText.gettId());
        tvQuantity.setText("Quantity: "+childText.getQuantity());
        tvPrice.setText(String.format("Price: $%.2f", childText.getTotalPrice()));
        tvOrderTiming.setText("Order Timing: "+childText.getDateTimeOrder());


        if (childText.getAddOnList().size()==0){
            tvAddOn.setText(String.format("Add On: \n     No add on"));
        }
        else{
            String addOnString = "Add On:\n";

            for(int i =0; i<childText.getAddOnList().size();i++){
                addOnString +=("     "+childText.getAddOnList().get(i).getName() +"\n");
            }
            tvAddOn.setText(addOnString);
        }

        if (childText.getAdditionalNote().isEmpty()){
            tvAdditionalNotes.setText("Additional Notes:\n     No Additional Notes");
        }
        else{
            tvAdditionalNotes.setText("Additional Notes:\n     "+childText.getAdditionalNote());
        }



        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "I will see this "+childText, Toast.LENGTH_SHORT).show();
            }
        });


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
