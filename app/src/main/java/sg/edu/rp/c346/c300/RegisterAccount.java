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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import sg.edu.rp.c346.c300.app.MainpageActivity;
import sg.edu.rp.c346.c300.model.Budget;
import sg.edu.rp.c346.c300.model.Category;
import sg.edu.rp.c346.c300.model.Charity;
import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.model.Drink;
import sg.edu.rp.c346.c300.model.FoodBudget;
import sg.edu.rp.c346.c300.model.Others;
import sg.edu.rp.c346.c300.model.Parent;
import sg.edu.rp.c346.c300.model.Stationery;

public class RegisterAccount extends AppCompatActivity {

    EditText etName,etNRIC, etPhone, etEmail, etPassword1, etPassword2, etParentName, etParentMobile, etParentPassword1, etParentPassword2;
    TextView etDob;
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
        etParentName = findViewById(R.id.etParentName);
        etParentMobile = findViewById(R.id.etParentMobile);
        etParentPassword1 = findViewById(R.id.etParentPassword);
        etParentPassword2 = findViewById(R.id.etParentPassword2);

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
                etDob.setText(String.format("%02d/%02d/%d",dayOfMonth,month,year));
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

                final String parentName = etParentName.getText().toString();
                final String parentPassword1 = etParentPassword1.getText().toString();
                String parentPassword2 = etParentPassword2.getText().toString();




                // Check all fields are filled in
                if (!nric.isEmpty() && !email.isEmpty() && !password1.isEmpty()
                        && !password2.isEmpty() && !name.isEmpty() && !dob.isEmpty()
                        && !school.isEmpty()&& !etPhone.getText().toString().isEmpty()
                        && !parentName.isEmpty() && !parentPassword1.isEmpty()
                        && !parentPassword2.isEmpty()){  // Check whether the user have enter nric or not

                    //validate the NRIC format
                    String patternNric = "[STFG][0-9]{7}[A-Z]";
                    if (Pattern.matches(patternNric, nric)) {


                        String patternMobile = "[89][0-9]{7}";
                        if (Pattern.matches(patternMobile, etPhone.getText().toString())) {

                            if (Pattern.matches(patternMobile, etParentMobile.getText().toString())) {


                                //Check whether the user enters the same passwords
                                if (password1.equals(password2)) {

                                    if (parentPassword1.equals(parentPassword2)) {

                                        boolean meetRequirementKid;
                                        boolean meetRequirementParent;

                                        if (password1.matches(".*[0-9]{1,}.*") && password1.matches(".*[A-Z]{1,}.*") && password1.matches(".*[@#$!%\\^\\*]{1,}.*") && password1.length() >= 6 && password1.length() <= 40) {
                                            meetRequirementKid = true;
                                        } else {
                                            meetRequirementKid = false;
                                        }


                                        if (parentPassword1.matches(".*[0-9]{1,}.*") && parentPassword1.matches(".*[A-Z]{1,}.*") && parentPassword1.matches(".*[@#$]{1,}.*") && parentPassword1.length() >= 6 && parentPassword1.length() <= 40) {
                                            meetRequirementParent = true;
                                        } else {
                                            meetRequirementParent = false;
                                        }

                                        if (meetRequirementKid) {

                                            if (meetRequirementParent) {

                                                final int phone = Integer.parseInt(etPhone.getText().toString()); // convert String phone to int phone
                                                final int parentMobile = Integer.parseInt(etParentMobile.getText().toString());

                                                Query usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("NRIC").equalTo(nric);  // Query to check how many that "username" value have been registered in the database.
                                                usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                                        //if there is more than one, then the nric have already registered before.
                                                        if (dataSnapshot.getChildrenCount() > 0) {
                                                            Toast.makeText(RegisterAccount.this, "Duplicate NRIC", Toast.LENGTH_LONG).show();
                                                            dialog.dismiss(); //cancel the dialog
                                                        }

                                                        // if not, we can just continue to register the user with that username.
                                                        else {

                                                            mAuth.createUserWithEmailAndPassword(email, password1).addOnCompleteListener(RegisterAccount.this, new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    //if the register is failed, this toast will be trigger
                                                                    if (!task.isSuccessful()) {

                                                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                                                            Toast.makeText(RegisterAccount.this, "You have already registered", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss(); //cancel the dialog
                                                                        } else {
                                                                            Toast.makeText(RegisterAccount.this, "Sign up error", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss(); //cancel the dialog
                                                                        }
                                                                    }
                                                                    // we will create database for this registering user
                                                                    else {
                                                                        // get the id that have to random generated for this user which can be found in Authentication tab, under "User UID"
                                                                        final String userId = mAuth.getCurrentUser().getUid();


//                                                //Storing all the information that we want to save by using HashMap (Used to be -1)
//                                                Map newPost = new HashMap();
//                                                newPost.put("Name", name);
//                                                newPost.put("NRIC", nric);
//                                                newPost.put("DOB", dob);
//                                                newPost.put("Phone Number",phone);
//                                                newPost.put("School", school);


                                                                        //Create object for the new user
                                                                        Parent parent = new Parent(parentName, parentMobile, parentPassword1);
                                                                        Customer customer = new Customer(name, nric, dob, phone, school, email, password1, 0, 0, 0, -1,parent);


//                                                // get to the directory of the database. (Used to be -1)
//                                                DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);


                                                                        //store the object into the database
                                                                        FirebaseDatabase.getInstance().getReference().child("Customer").child(userId).setValue(customer).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    createNecessaryFieldInFirebase(userId);
                                                                                    Toast.makeText(RegisterAccount.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                                                                    dialog.dismiss(); //cancel the dialog
                                                                                    Intent intent = new Intent(RegisterAccount.this, loginpage.class);
                                                                                    startActivity(intent);
                                                                                    finish();
                                                                                } else {
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


                                            } else {
                                                etParentPassword1.setError("Length is between 7 and 40. Must contain at least one digit, one upper and lower case, one special characters(@, #, $, !, %, ^, *)");
                                                dialog.dismiss(); //cancel the dialog
                                            }

                                        } else {
                                            etPassword1.setError("Length is between 7 and 40. Must contain at least one digit, one upper and lower case, one special characters(@, #, $, !, %, ^, *)");
                                            dialog.dismiss(); //cancel the dialog
                                        }

                                    } else {
                                        Toast.makeText(RegisterAccount.this, "Parent's Password Mismatched", Toast.LENGTH_LONG).show();
                                        etParentPassword1.setError("Password Mismatched");
                                        dialog.dismiss(); //cancel the dialog
                                    }

                                } else { //if both passwords are not the same
                                    Toast.makeText(RegisterAccount.this, "Kid's Password Mismatched", Toast.LENGTH_LONG).show();
                                    etPassword1.setError("Password Mismatched");
                                    dialog.dismiss(); //cancel the dialog
                                }


                            }
                            else{
                                etParentMobile.setError("Invalid Mobile Number");
                                dialog.dismiss(); //cancel the dialog
                            }

                        }
                        else{
                            etPhone.setError("Invalid Mobile Number");
                            dialog.dismiss(); //cancel the dialog
                        }


                    }
                    else{
                        etNRIC.setError("Invalid NRIC");
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


//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        mAuth.addAuthStateListener(firebaseAuthListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//
//        mAuth.removeAuthStateListener(firebaseAuthListener);
//    }



    //Create all the necessary fields in the firebase
    private void createNecessaryFieldInFirebase(String customerUID){

//        - Register new user:
//        push notification customer
//        push notification parent
//        Cart
//        Budget
//        eWalletRequestCustomer
//        GoalSaving
//        Notification
//        PostPayment
//        PrePayment
//        TC
//        HC
//        deny list
//        deny list Request
//        creditCardInformation



        DatabaseReference drCreateFirebase = FirebaseDatabase.getInstance().getReference();


        //region push notification customer
        drCreateFirebase.child("pushNotificationCustomer").child(customerUID).child("body").setValue("null");
        drCreateFirebase.child("pushNotificationCustomer").child(customerUID).child("numOfRequest").setValue(0);
        drCreateFirebase.child("pushNotificationCustomer").child(customerUID).child("situation").setValue("null");
        drCreateFirebase.child("pushNotificationCustomer").child(customerUID).child("title").setValue("null");



        //endregion




        //region push notification parent
        drCreateFirebase.child("pushNotificationParent").child(customerUID).child("body").setValue("null");
        drCreateFirebase.child("pushNotificationParent").child(customerUID).child("numOfRequest").setValue(0);
        drCreateFirebase.child("pushNotificationParent").child(customerUID).child("situation").setValue("null");
        drCreateFirebase.child("pushNotificationParent").child(customerUID).child("title").setValue("null");

        //endregion


        //region cart

        drCreateFirebase.child("cart").child(customerUID).child("numOfCartFood").setValue(0);

        //endregion



        //region budget

        drCreateFirebase.child("budget").child(customerUID).child("allowToChange").setValue(true);
        drCreateFirebase.child("budget").child(customerUID).child("changeBudgetTiming").setValue(MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy"));



        FoodBudget foodBudget = new FoodBudget(-1,-1,0,0,0,0);
        Drink drink = new Drink(-1,-1,0,0,0,0);
        Stationery stationery = new Stationery(-1,-1,0,0,0,0);
        Charity charity = new Charity(-1,-1,0,0,0,0);
        Others others = new Others(-1,-1,0,0,0,0);

        Category category = new Category(foodBudget, drink, stationery, charity, others);
        Budget budget = new Budget(0,-1,category, MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy"));

        drCreateFirebase.child("budget").child(customerUID).child("day").child("0").setValue(budget);
        drCreateFirebase.child("budget").child(customerUID).child("day").child("1").setValue(budget);
        drCreateFirebase.child("budget").child(customerUID).child("day").child("2").setValue(budget);
        drCreateFirebase.child("budget").child(customerUID).child("day").child("3").setValue(budget);
        drCreateFirebase.child("budget").child(customerUID).child("day").child("4").setValue(budget);
        drCreateFirebase.child("budget").child(customerUID).child("day").child("5").setValue(budget);
        drCreateFirebase.child("budget").child(customerUID).child("day").child("6").setValue(budget);

        drCreateFirebase.child("budget").child(customerUID).child("day").child("numOfDays").setValue(7);


        //endregion



        //region eWalletRequestCustomer

        drCreateFirebase.child("eWalletRequestCustomer").child(customerUID).child("accepted").child("numOfRequest").setValue(0);
        drCreateFirebase.child("eWalletRequestCustomer").child(customerUID).child("rejected").child("numOfRequest").setValue(0);
        drCreateFirebase.child("eWalletRequestCustomer").child(customerUID).child("pending").child("numOfRequest").setValue(0);

        //endregion



        //region goalSaving
        drCreateFirebase.child("goalSaving").child(customerUID).child("numOfGoal").setValue(0);
        //endregion



        //region notification
        drCreateFirebase.child("notification").child(customerUID).child("outside").child("numOfOutside").setValue(0);
        drCreateFirebase.child("notification").child(customerUID).child("preOrder").child("numOfPreOrder").setValue(0);
        drCreateFirebase.child("notification").child(customerUID).child("walkIn").child("numOfWalkIn").setValue(0);
        //endregion



        //region postPayment
        drCreateFirebase.child("postPayment").child(customerUID).child("numOfPostPayment").setValue(0);
        //endregion



        //region prepayment
        drCreateFirebase.child("prePayment").child(customerUID).child("numOfPrePayment").setValue(0);
        //endregion



        //region TC
        drCreateFirebase.child("tc").child(customerUID).child("order").child("numOfOrder").setValue(0);
        //endregion


        //region HC
        drCreateFirebase.child("hc").child(customerUID).child("outside").child("numOfOutside").setValue(0);
        drCreateFirebase.child("hc").child(customerUID).child("preOrder").child("numOfPreOrder").setValue(0);
        drCreateFirebase.child("hc").child(customerUID).child("walkIn").child("numOfWalkIn").setValue(0);

        //endregion


        //region Deny list
        drCreateFirebase.child("denyList").child(customerUID).child("school").child("Jurong Point").child("numOfDeny").setValue(0);
        drCreateFirebase.child("denyList").child(customerUID).child("school").child("Republic Polytechnic").child("numOfDeny").setValue(0);
        drCreateFirebase.child("denyList").child(customerUID).child("school").child("Singapore Polytechnic").child("numOfDeny").setValue(0);

        //endregion


        //region Deny list Request
        drCreateFirebase.child("denyListRequest").child(customerUID).child("school").child("Jurong Point").child("numOfDeny").setValue(0);
        drCreateFirebase.child("denyListRequest").child(customerUID).child("school").child("Republic Polytechnic").child("numOfDeny").setValue(0);
        drCreateFirebase.child("denyListRequest").child(customerUID).child("school").child("Singapore Polytechnic").child("numOfDeny").setValue(0);

        //endregion


        //region creditCardInformation
        drCreateFirebase.child("creditCardCustomer").child(customerUID).child("numOfTopUp").setValue(0);

        //



        //region customer graph
        //Customer Graph
        Calendar cal = Calendar.getInstance();

        //Create -----------------Saving-------------------

        //Create Blank Year Saving
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("savings").setValue(0);
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("year").setValue(2019);
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("numOfYears").setValue(1);


        //Create Blank Year for Spending
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("spendings").setValue(0);
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("year").setValue(2019);
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("numOfYears").setValue(1);

        //Create Blank Year for Charity
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("charitys").setValue(0);
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("year").setValue(2019);
        FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("numOfYears").setValue(1);

        //Create Blank All Months
        for(int i = 0 ; i < 12;i++){
            SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy");
            cal.set(Calendar.YEAR, 2019);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.MONTH, i);
            int maxDaysinMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            //Saving
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("months").child(String.valueOf(i)).child("monthName").setValue(new SimpleDateFormat("MMMM").format(cal.getTime()));
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("months").child(String.valueOf(i)).child("savings").setValue(0);
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("months").child("numOfMonths").setValue(12);

            //Spending
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("months").child(String.valueOf(i)).child("monthName").setValue(new SimpleDateFormat("MMMM").format(cal.getTime()));
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("months").child(String.valueOf(i)).child("spendings").setValue(0);
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("months").child("numOfMonths").setValue(12);

            //Charity
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("months").child(String.valueOf(i)).child("monthName").setValue(new SimpleDateFormat("MMMM").format(cal.getTime()));
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("months").child(String.valueOf(i)).child("charitys").setValue(0);
            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("months").child("numOfMonths").setValue(12);
            //Create all Blank Dates under the Month
            for(int a =0; a < maxDaysinMonth;a++){


                //Saving
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child(String.valueOf(a)).child("date").setValue(fmt.format(cal.getTime()));
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child(String.valueOf(a)).child("savings").setValue(0);
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("saving").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child("numOfDays").setValue(maxDaysinMonth);

                //Spending
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child(String.valueOf(a)).child("date").setValue(fmt.format(cal.getTime()));
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child(String.valueOf(a)).child("spendings").setValue(0);
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("spending").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child("numOfDays").setValue(maxDaysinMonth);

                //Charity
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child(String.valueOf(a)).child("date").setValue(fmt.format(cal.getTime()));
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child(String.valueOf(a)).child("charitys").setValue(0);
                FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID).child("charity").child("year").child("0").child("months").child(String.valueOf(i)).child("dates").child("numOfDays").setValue(maxDaysinMonth);

                cal.add(Calendar.DAY_OF_MONTH,1);
            }

        }

        //endregion



    }

}
