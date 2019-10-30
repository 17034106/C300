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

public class TestingParentProfile extends AppCompatActivity {

    Customer customer;

    TextView settingName, settingNRIC, settingDOB, settingPhone, settingSchool, settingEmail, parentName, parentMobile, settingRemind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_profile);


        settingName = findViewById(R.id.settingStudentName);
        settingNRIC = findViewById(R.id.settingStudentNRIC);
        settingDOB = findViewById(R.id.settingStudentDOB);
        settingPhone = findViewById(R.id.settingStudentPhone);
        settingSchool = findViewById(R.id.settingStudentSchool);
        settingEmail = findViewById(R.id.settingStudentEmail);
        parentName = findViewById(R.id.settingParentName);
        parentMobile = findViewById(R.id.settingParentMobile);
        settingRemind = findViewById(R.id.settingRemind);




        findViewById(R.id.settingEditableProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentProfile.this, TestingParentProfileEdit.class);
                intent.putExtra("customer", customer);
                startActivity(intent);
            }
        });


        DatabaseReference drCustomerInfo = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drCustomerInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                customer = dataSnapshot.getValue(Customer.class);

                settingName.setText(customer.getCustomername());
                settingNRIC.setText(customer.getNric());
                settingDOB.setText(customer.getDob());
                settingPhone.setText(customer.getMobile()+"");
                settingSchool.setText(customer.getCustomerschool());
                settingEmail.setText(customer.getEmail());
                parentName.setText(customer.getParent().getParentname());
                parentMobile.setText(customer.getParent().getMobile()+"");
                if (customer.getRemind()!=-1) {
                    settingRemind.setText(String.format("$%.2f",customer.getRemind()));
                }
                else{
                    settingRemind.setText("Is not set");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        findViewById(R.id.settingChangePassword).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentProfile.this, ChangePassword.class);
                startActivity(intent);
                finish();
            }
        });


    }
}
