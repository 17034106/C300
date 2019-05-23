package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.rp.c346.c300.model.Customer;

public class SettingPage extends AppCompatActivity {

    Customer customer;

    TextView settingName, settingNRIC, settingDOB, settingPhone, settingSchool, settingEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_page);

        settingName = findViewById(R.id.settingStudentName);
        settingNRIC = findViewById(R.id.settingStudentNRIC);
        settingDOB = findViewById(R.id.settingStudentDOB);
        settingPhone = findViewById(R.id.settingStudentPhone);
        settingSchool = findViewById(R.id.settingStudentSchool);
        settingEmail = findViewById(R.id.settingStudentEmail);




        findViewById(R.id.settingEditableProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingPage.this, SettingEditablePage.class);

                startActivity(intent);
                finish();
            }
        });


        DatabaseReference drCustomerInfo = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drCustomerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customer = dataSnapshot.getValue(Customer.class);
                Log.d("DataSnapshot","What about your: "+dataSnapshot);

                Log.d("What is customer", "Tell me what is customer: "+customer);

                settingName.setText(customer.getCustomername());
                settingNRIC.setText(customer.getNric());
                settingDOB.setText(customer.getDob());
                settingPhone.setText(customer.getMobile()+"");
                settingSchool.setText(customer.getCustomerschool());
                settingEmail.setText(customer.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }
}
