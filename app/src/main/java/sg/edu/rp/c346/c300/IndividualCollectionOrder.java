package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.adapter.CollectionAdapter;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.AddOn;

public class IndividualCollectionOrder extends AppCompatActivity {


    TextView tvTID, tvFoodName, tvFoodPrice, tvStallName, tvFoodStallOperation, tvLastChange, tvQuantity, tvAddOn, tvAdditionalNotes;
    ArrayList<AddOn> addOnListIndividual;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_collection_order);


        tvTID = findViewById(R.id.tvIndividualTID);
        tvFoodName = findViewById(R.id.tvIndividualFoodName);
        tvFoodPrice = findViewById(R.id.tvIndividualFoodPrice);
        tvStallName = findViewById(R.id.tvIndividualFoodStallName);
        tvFoodStallOperation = findViewById(R.id.tvIndividualFoodStallDuration);
        tvLastChange = findViewById(R.id.tvIndividualLastChanges);
        tvQuantity = findViewById(R.id.IndividualQuantityDisplay);
        tvAddOn = findViewById(R.id.tvIndividualAddOn);
        tvAdditionalNotes = findViewById(R.id.tvIndividualAddtionalNotes);



        final Intent intentReceive = getIntent();

        tvTID.setText(intentReceive.getStringExtra("tId"));
        tvFoodName.setText(intentReceive.getStringExtra("foodName"));
        tvFoodPrice.setText(String.format("$%.2f", intentReceive.getDoubleExtra("foodPrice",0)));
        tvStallName.setText(String.format("Stall: %s",intentReceive.getStringExtra("stallName")));

        Date stallStartOperationDate = MainpageActivity.convertStringToDate(intentReceive.getStringExtra("startTime"), "HHmm");
        Date stallEndOperationDate = MainpageActivity.convertStringToDate(intentReceive.getStringExtra("endTime"), "HHmm");

        tvFoodStallOperation.setText(String.format("Working from %s to %s", MainpageActivity.convertDateToString(stallStartOperationDate, "hh:mm a"),MainpageActivity.convertDateToString(stallEndOperationDate, "hh:mm a") ));
        String lastChangeDisplay = "Changes can only be made before <b>"+intentReceive.getStringExtra("lastChanges")+"</b>";
        tvLastChange.setText(Html.fromHtml(lastChangeDisplay));
        tvQuantity.setText(String.format("x%d", intentReceive.getIntExtra("quantity", 0)));
        if (intentReceive.getStringExtra("additionalNote").isEmpty()){
            tvAdditionalNotes.setText("No Additional Notes");
        }
        else{
            tvAdditionalNotes.setText(intentReceive.getStringExtra("additionalNote"));
        }


        addOnListIndividual = CollectionAdapter.addOnListIndividual;
        String addOnString ="";
        for (AddOn i: addOnListIndividual){
            addOnString+=String.format("%-70s +$%.2f\n", i.getName(), i.getPrice());
        }

        if (addOnString.isEmpty()){
            tvAddOn.setText("No Add On");
        }
        else {
            tvAddOn.setText(addOnString.trim());
        }


        findViewById(R.id.IndividualEditOrderBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndividualCollectionOrder.this, IndividualEditFoodDisplay.class);
                intent.putExtra("tId",intentReceive.getStringExtra("tId") );
                intent.putExtra("foodName", intentReceive.getStringExtra("foodName"));
                intent.putExtra("foodPrice", intentReceive.getDoubleExtra("foodPrice",0));
                intent.putExtra("stallName", intentReceive.getStringExtra("stallName"));
                intent.putExtra("stallId", intentReceive.getIntExtra("stallId", 0));
                intent.putExtra("startTime", intentReceive.getStringExtra("startTime"));
                intent.putExtra("endTime", intentReceive.getStringExtra("endTime"));
                intent.putExtra("quantity",intentReceive.getIntExtra("quantity", 0));
                intent.putExtra("additionalNote",intentReceive.getStringExtra("additionalNote"));
                startActivity(intent);
            }
        });

    }
}
