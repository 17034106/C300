package sg.edu.rp.c346.c300.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.GoalSaving;

public class GoalSavingAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GoalSaving> goalSavingList;

    public GoalSavingAdapter(Context context, ArrayList<GoalSaving> goalSavingList) {
        this.context = context;
        this.goalSavingList = goalSavingList;
    }

    @Override
    public int getCount() {
        return goalSavingList.size();
    }

    @Override
    public Object getItem(int position) {
        return goalSavingList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = View.inflate(context, R.layout.goal_saving_items, null);
        }

        TextView name = convertView.findViewById(R.id.GoalSavingAllName);
        TextView price = convertView.findViewById(R.id.GoalSavingAllPrice);
        TextView status = convertView.findViewById(R.id.GoalSavingAllStatus);
        TextView num = convertView.findViewById(R.id.GoalSavingAllNo);

        GoalSaving goalSaving = goalSavingList.get(position);

        name.setText("Name: "+goalSaving.getName());
        price.setText(String.format("Price: $%.2f", goalSaving.getPrice()));
        num.setText((position+1)+"");





        return convertView;
    }
}
