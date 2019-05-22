package sg.edu.rp.c346.c300;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.Customer;

public class RegisterAccount extends AppCompatActivity {

    EditText etName,etNRIC, etDob, etPhone, etEmail, etPassword1, etPassword2;
    TextView tvMember;
    CircularProgressButton btnRegister;

    MaterialBetterSpinner etSchool;

    ArrayList<String> schoolList = new ArrayList<>();
    ArrayAdapter<String> schoolAdapter;



    //Firebase Authentication
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;


    //DatePicker
    DatePickerDialog.OnDateSetListener mDateSetListener;
    ImageView ivDobArrow;

    //Showing the loading
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        etName = findViewById(R.id.etName);
        etNRIC = findViewById(R.id.etNRIC);
        etDob = findViewById(R.id.etDob);
        etSchool = findViewById(R.id.etSchool);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword1 = findViewById(R.id.etPassword1);
        etPassword2 = findViewById(R.id.etPassword2);
        tvMember = findViewById(R.id.tvMember);
        btnRegister = findViewById(R.id.btn_upload);
        ivDobArrow = findViewById(R.id.ivDobArrow);

        //Showing the loading
        dialog = new ProgressDialog(this);

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
        etSchool.setAdapter(schoolAdapter);






        //region DatePicker
        ivDobArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(RegisterAccount.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable((Color.TRANSPARENT)));
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                etDob.setText(dayOfMonth+"/"+month+"/"+year);
            }
        };

        //endregion


        mAuth = FirebaseAuth.getInstance();

        // Check whether there is any login user.
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Toast.makeText(RegisterAccount.this, "Registration Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterAccount.this, MainpageActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        // Press the register button to register the user for authentication.
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Showing the loading
                dialog.setMessage("Registering. Please wait...");
                dialog.show();

                final String nric = etNRIC.getText().toString().trim();
                final String email = etEmail.getText().toString().trim();
                final String password1 = etPassword1.getText().toString().trim();
                final String password2 = etPassword2.getText().toString().trim();
                final String name = etName.getText().toString().trim();
                final String dob = etDob.getText().toString();
                final String school = etSchool.getText().toString();



                // Check all fields are filled in
                if (!nric.isEmpty() && !email.isEmpty() && !password1.isEmpty() && !password2.isEmpty() && !name.isEmpty() && !dob.isEmpty() && !school.isEmpty()&& !etPhone.getText().toString().isEmpty()){  // Check whether the user have enter nric or not

                    //Check whether the user enters the same passwords
                    if (password1.equals(password2)){


                        final int phone = Integer.parseInt(etPhone.getText().toString()); // convert String phone to int phone


                        Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("NRIC").equalTo(nric);  // Query to check how many that "username" value have been registered in the database.
                        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                //if there is more than one, then the nric have already registered before.
                                if (dataSnapshot.getChildrenCount()>0){
                                    Toast.makeText(RegisterAccount.this, "Duplicate NRIC", Toast.LENGTH_LONG).show();
                                    dialog.dismiss(); //cancel the dialog
                                }

                                // if not, we can just continue to register the user with that username.
                                else{

                                    mAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(RegisterAccount.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            //if the register is failed, this toast will be trigger
                                            if (!task.isSuccessful()){

                                                if (task.getException() instanceof FirebaseAuthUserCollisionException){
                                                    Toast.makeText(RegisterAccount.this, "You have already registered", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss(); //cancel the dialog
                                                }
                                                else {
                                                    Toast.makeText(RegisterAccount.this, "Sign up error", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss(); //cancel the dialog
                                                }
                                            }
                                            // we will create database for this registering user
                                            else{
                                                // get the id that have to random generated for this user which can be found in Authentication tab, under "User UID"
                                                String userId = mAuth.getCurrentUser().getUid();



//                                                //Storing all the information that we want to save by using HashMap (Used to be -1)
//                                                Map newPost = new HashMap();
//                                                newPost.put("Name", name);
//                                                newPost.put("NRIC", nric);
//                                                newPost.put("DOB", dob);
//                                                newPost.put("Phone Number",phone);
//                                                newPost.put("School", school);


                                                //Create object for the new user
                                                Customer customer = new Customer(name, nric, dob, phone, school, email, password1);


//                                                // get to the directory of the database. (Used to be -1)
//                                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


                                                //store the object into the database
                                                FirebaseDatabase.getInstance().getReference().child("Customer").child(userId).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            Toast.makeText(RegisterAccount.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss(); //cancel the dialog
                                                        }
                                                        else{
                                                            Toast.makeText(RegisterAccount.this, "Registration Failed. Please Try Again", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss(); //cancel the dialog
                                                        }
                                                    }
                                                });

//                                                //Set all the information in the HashMap to the user's database (Used to be -1)
//                                                current_user_db.setValue(newPost);
                                            }
                                        }
                                    });

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                    else{ //if both passwords are not the same
                        Toast.makeText(RegisterAccount.this, "Password Mismatched", Toast.LENGTH_LONG).show();
                        dialog.dismiss(); //cancel the dialog
                    }


                }

                else{ // if the user did not fill in all fields
                    Toast.makeText(RegisterAccount.this, "Please fill up all fields", Toast.LENGTH_LONG).show();
                    dialog.dismiss(); // cancel the dialog
                }






            }
        });







        tvMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterAccount.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mAuth.removeAuthStateListener(firebaseAuthListener);
    }



}
