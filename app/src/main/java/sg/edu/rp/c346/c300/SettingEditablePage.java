package sg.edu.rp.c346.c300;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;

import sg.edu.rp.c346.c300.model.Customer;

public class SettingEditablePage extends AppCompatActivity {

    Customer customer;

    EditText settingEditableName, settingEditableNRIC, settingEditableDOB, settingEditablePhone, settingEditableEmail;

    MaterialBetterSpinner settingEditableSchool;

    ArrayList<String> schoolList = new ArrayList<>();
    ArrayAdapter<String> schoolAdapter;


    //DatePicker
    DatePickerDialog.OnDateSetListener mDateSetListener;

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


        FirebaseDatabase.getInstance().getReference().child("school").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numOfSchool = Integer.parseInt(dataSnapshot.child("numOfSchool").getValue().toString());
                for (int i=0; i<numOfSchool; i++){
                    schoolList.add(dataSnapshot.child(Integer.toString(i)).child("name").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        schoolAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,schoolList);
        settingEditableSchool.setAdapter(schoolAdapter);


        //Displaying the customer data from firebase
        final DatabaseReference drCustomerInfo = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
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

        //region display the datepicker to input the DOB
        findViewById(R.id.settingEditableDobArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SettingEditablePage.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                settingEditableDOB.setText(dayOfMonth+"/"+month+"/"+year);
            }
        };
        //endregion


        // confirm button to update
        findViewById(R.id.settingConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingEditableName.getText().toString().isEmpty() && settingEditableNRIC.getText().toString().isEmpty()&& settingEditableDOB.getText().toString().isEmpty() && settingEditableSchool.getText().toString().isEmpty()&& settingEditablePhone.getText().toString().isEmpty() && settingEditableEmail.getText().toString().isEmpty()){
                    Toast.makeText(SettingEditablePage.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    drCustomerInfo.child("customername").setValue(settingEditableName.getText().toString().trim());
                    drCustomerInfo.child("nric").setValue(settingEditableNRIC.getText().toString().trim());
                    drCustomerInfo.child("dob").setValue(settingEditableDOB.getText().toString().trim());
                    drCustomerInfo.child("mobile").setValue(Double.parseDouble(settingEditablePhone.getText().toString().trim()));
                    drCustomerInfo.child("customerschool").setValue(settingEditableSchool.getText().toString());
                    drCustomerInfo.child("email").setValue(settingEditableEmail.getText().toString().trim());
                    Toast.makeText(SettingEditablePage.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SettingEditablePage.this, SettingPage.class);
                    startActivity(intent);
                    finish();

                }

            }
        });


    }
}
