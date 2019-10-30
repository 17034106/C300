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
import java.util.regex.Pattern;

import sg.edu.rp.c346.c300.model.Customer;

public class TestingParentProfileEdit extends AppCompatActivity {

    Customer customer;

    EditText settingEditableName, settingEditableDOB, settingEditablePhone, settingParentName, settingParentMobile, settingRemind;

    TextView settingEditableEmail, settingEditableNRIC;

    MaterialBetterSpinner settingEditableSchool;

    ArrayList<String> schoolList = new ArrayList<>();
    ArrayAdapter<String> schoolAdapter;


    //DatePicker
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_profile_edit);

        customer = (Customer) getIntent().getSerializableExtra("customer");

        settingEditableName = findViewById(R.id.settingEditableStudentName);
        settingEditableNRIC = findViewById(R.id.settingEditableStudentNRIC);
        settingEditableDOB = findViewById(R.id.settingEditableStudentDOB);
        settingEditablePhone = findViewById(R.id.settingEditableStudentPhone);
        settingEditableSchool = findViewById(R.id.settingEditableStudentSchool);
        settingEditableEmail = findViewById(R.id.settingEditableStudentEmail);
        settingParentName = findViewById(R.id.settingEditableParentName);
        settingParentMobile = findViewById(R.id.settingEditableParentMobile);
        settingRemind = findViewById(R.id.settingRemind);


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


        //Displaying the customer data

        settingEditableName.setText(customer.getCustomername());
        settingEditableNRIC.setText(customer.getNric());
        settingEditableDOB.setText(customer.getDob());
        settingEditablePhone.setText(customer.getMobile()+"");
        settingEditableSchool.setText(customer.getCustomerschool());
        settingEditableEmail.setText(customer.getEmail());
        settingParentName.setText(customer.getParent().getParentname());
        settingParentMobile.setText(customer.getParent().getMobile()+"");
        if (customer.getRemind()!=-1) {
            settingRemind.setText(customer.getRemind() + "");
        }
        else{
            settingRemind.setText("Is not set");
        }

        //region display the datepicker to input the DOB
        findViewById(R.id.settingEditableDobArrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(TestingParentProfileEdit.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                settingEditableDOB.setText(String.format("%02d/%02d/%d",dayOfMonth,month,year));
            }
        };
        //endregion


        // confirm button to update
        findViewById(R.id.settingConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingEditableName.getText().toString().isEmpty() && settingEditableNRIC.getText().toString().isEmpty()&& settingEditableDOB.getText().toString().isEmpty() && settingEditableSchool.getText().toString().isEmpty()&& settingEditablePhone.getText().toString().isEmpty() && settingEditableEmail.getText().toString().isEmpty()){
                    Toast.makeText(TestingParentProfileEdit.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
                else{

                    String patternMobile = "[89][0-9]{7}";
                    if (Pattern.matches(patternMobile, settingEditablePhone.getText().toString())) {

                        if (Pattern.matches(patternMobile, settingParentMobile.getText().toString())) {

                            final DatabaseReference drCustomerInfo = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            drCustomerInfo.child("customername").setValue(settingEditableName.getText().toString().trim());
                            drCustomerInfo.child("nric").setValue(settingEditableNRIC.getText().toString().trim());
                            drCustomerInfo.child("dob").setValue(settingEditableDOB.getText().toString().trim());
                            drCustomerInfo.child("mobile").setValue(Double.parseDouble(settingEditablePhone.getText().toString().trim()));
                            drCustomerInfo.child("customerschool").setValue(settingEditableSchool.getText().toString());
                            drCustomerInfo.child("Parent").child("parentname").setValue(settingParentName.getText().toString());
                            drCustomerInfo.child("Parent").child("mobile").setValue(Double.parseDouble(settingParentMobile.getText().toString()));

                            boolean isDouble = true;
                            try
                            {
                                Double.parseDouble(settingRemind.getText().toString());
                            }
                            catch(NumberFormatException e)
                            {
                                //not a double
                                isDouble = false;
                            }


                            if (isDouble){
                                drCustomerInfo.child("remind").setValue(Double.parseDouble(settingRemind.getText().toString().trim()));
                            }



                            Toast.makeText(TestingParentProfileEdit.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TestingParentProfileEdit.this, TestingParentProfile.class);
                            startActivity(intent);
                            finish();

                        }
                        else{settingParentMobile.setError("Invalid Mobile Number");
                        }

                    }
                    else{
                        settingEditablePhone.setError("Invalid Mobile Number");
                    }

                }

            }
        });


    }

    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
