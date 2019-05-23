package sg.edu.rp.c346.c300;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.rp.c346.c300.model.Customer;

public class SettingEditablePage extends AppCompatActivity {

    Customer customer;

    TextView settingEditableName, settingEditableNRIC, settingEditableDOB, settingEditablePhone, settingEditableSchool, settingEditableEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_editable_page);


        settingEditableName = findViewById(R.id.settingEditableStudentName);
        settingEditableNRIC = findViewById(R.id.settingEditableStudentNRIC);
        settingEditableDOB = findViewById(R.id.settingEditableStudentDOB);
        settingEditablePhone = findViewById(R.id.settingEditableStudentPhone);
        settingEditableSchool = findViewById(R.id.settingEditableStudentSchool);
        settingEditableEmail = findViewById(R.id.settingEditableStudentEmail);


        DatabaseReference drCustomerInfo = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drCustomerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customer = dataSnapshot.getValue(Customer.class);
                Log.d("DataSnapshot","What about your: "+dataSnapshot);

                Log.d("What is customer", "Tell me what is customer: "+customer);

                settingEditableName.setText(customer.getCustomername());
                settingEditableNRIC.setText(customer.getNric());
                settingEditableDOB.setText(customer.getDob());
                settingEditablePhone.setText(customer.getMobile()+"");
                settingEditableSchool.setText(customer.getCustomerschool());
                settingEditableEmail.setText(customer.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
