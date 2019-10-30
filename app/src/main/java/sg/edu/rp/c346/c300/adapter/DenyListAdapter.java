package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.EWallet;
import sg.edu.rp.c346.c300.model.Food;

public class DenyListAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<Integer> stallIdList;
    private ArrayList<String> stallNameList;
    private String location;

    public DenyListAdapter(Context context, ArrayList<Integer> stallIdList, ArrayList<String> stallNameList, String location) {
        this.context = context;
        this.stallIdList = stallIdList;
        this.stallNameList = stallNameList;
        this.location = location;
    }

    @Override
    public int getCount() {
        return stallIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return stallIdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = View.inflate(context, R.layout.deny_list_item, null);
        }

        int stallID = stallIdList.get(position);
        String stallName = stallNameList.get(position);

        TextView denyListNo = convertView.findViewById(R.id.denyListNo);
        TextView denyListStallName = convertView.findViewById(R.id.denylistStallName);
        TextView denyListStallLocation = convertView.findViewById(R.id.denyListStallLocation);

        denyListNo.setText((position+1)+"");
        denyListStallName.setText("Stall:\n"+stallName);
        denyListStallLocation.setText("Location:\n"+location);


        return convertView;
    }
}
