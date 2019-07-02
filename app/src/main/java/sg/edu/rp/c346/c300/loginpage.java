package sg.edu.rp.c346.c300;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Arrays;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import sg.edu.rp.c346.c300.app.MainpageActivity;

public class loginpage extends AppCompatActivity {

    AutoCompleteTextView etEmail;
    EditText etPassword;
    CircularProgressButton btnLogin;
    TextView registerAccount;

    ImageView fingerprintLogin;
    String correctEmail; // save the correct email
    String correctPassword; // save the correct password
//    String retrieveEmail; // retrieve the email when the app is re-opened
//    String retrievePassword; // retrieve the password when the app is re-opened
    boolean resultOfFingerprintAuthentication; //get the result of the fingerprint authentication

    // For AutoComplete (Display all username)
    ArrayList<String> usernameList = new ArrayList<String>(); // for AutoComplete (Display all username)

    SharedPreferences prefSaved;
    SharedPreferences.Editor prefEditSaved;
    SharedPreferences prefRetrieve;


    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener firebaseAuthListener;

    int backButtonCount; // exit the app when pressed twice


    //Showing the loading
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mAuth = FirebaseAuth.getInstance();



        //obtain an instance of the SharedPreferences
        prefSaved = PreferenceManager.getDefaultSharedPreferences(loginpage.this);
        //obtain an instance of the sharedPreference Editor for update later
        prefEditSaved = prefSaved.edit();


        //Obtain an instance of the SharedPreferences
        prefRetrieve = PreferenceManager.getDefaultSharedPreferences(loginpage.this);


        correctEmail= prefRetrieve.getString("correctEmail", "");
        correctPassword= prefRetrieve.getString("correctPassword", "");


        //Showing the loading
        dialog = new ProgressDialog(this);

        resultOfFingerprintAuthentication = getIntent().getBooleanExtra("result",false);
        if (resultOfFingerprintAuthentication){
            Log.d("What is the email","What is the correctEmail: "+correctEmail);
            Log.d("What is the email","What is the correctPassword: "+correctPassword);
            if (!correctEmail.isEmpty() && !correctPassword.isEmpty()) {
                mAuth.signInWithEmailAndPassword(correctEmail, correctPassword).addOnCompleteListener(loginpage.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(loginpage.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                            dialog.dismiss(); //cancel the dialog
                        } else {

                            mAuth.addAuthStateListener(firebaseAuthListener);


                            //Add the key-value pair
                            prefEditSaved.putString("correctEmail", correctEmail);
                            prefEditSaved.putString("correctPassword", correctPassword);

                            //Call commit() method to save the changes into the SharedPreferences
                            prefEditSaved.commit();

                        }
                    }
                });
            }
        }



//        //region Set the animation background
//        RelativeLayout relativeLayout = findViewById(R.id.layout);
//        AnimationDrawable animationDrawable = (AnimationDrawable) relativeLayout.getBackground();
//        animationDrawable.setEnterFadeDuration(2000);
//        animationDrawable.setExitFadeDuration(4000);
//        animationDrawable.start();
//        //endregion



        //region  bind the views
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPasswordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        registerAccount = findViewById(R.id.tvRegister);
        fingerprintLogin = findViewById(R.id.fingerprintLogin);
        //endregion



        //region For AutoComplete (Display all username)
        usernameList.addAll(Arrays.asList("Andrew", "Ben", "Cai", "Darius", "Elex"));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.select_dialog_item, usernameList);
        etEmail.setThreshold(1);
        etEmail.setAdapter(adapter);
        //endregion



        // Check whether there is any login user.
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user !=null){
                    Intent intent = new Intent(loginpage.this, MainpageActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };




        // Press the register button to login the user for authentication.
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Showing the loading
                dialog.setMessage("Logging. Please wait...");
                dialog.show();

                final String email = etEmail.getText().toString();
                final String password = etPassword.getText().toString();

                mAuth.addAuthStateListener(firebaseAuthListener);
                if (!email.isEmpty() && !password.isEmpty()){

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(loginpage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(loginpage.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                dialog.dismiss(); //cancel the dialog
                            }
                            else{
                                correctEmail = email;
                                correctPassword = password;


                                //Add the key-value pair
                                prefEditSaved.putString("correctEmail", correctEmail);
                                prefEditSaved.putString("correctPassword", correctPassword);

                                //Call commit() method to save the changes into the SharedPreferences
                                prefEditSaved.commit();

                            }
                        }
                    });


                }
                else {
                    Toast.makeText(loginpage.this, "Please fill up all fields", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); //cancel the dialog
                }


            }
        });




        registerAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(loginpage.this, RegisterAccount.class);
                startActivity(intent);
            }
        });


        fingerprintLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(correctEmail.equals("") || correctPassword.equals("") ){
                    Toast.makeText(loginpage.this, "Please login through email and password for the first time login", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(loginpage.this, FingerprintAuthentication.class);
                    startActivity(intent);
                }

            }
        });





    }

    @Override
    protected void onPause() {
        super.onPause();



        //Add the key-value pair
        prefEditSaved.putString("correctEmail", correctEmail);
        prefEditSaved.putString("correctPassword", correctPassword);

        //Call commit() method to save the changes into the SharedPreferences
        prefEditSaved.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();



        //Add the key-value pair
        prefEditSaved.putString("correctEmail", correctEmail);
        prefEditSaved.putString("correctPassword", correctPassword);

        //Call commit() method to save the changes into the SharedPreferences
        prefEditSaved.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();



        //Retrieve the saved data with the key from the SharedPreferences Object
        correctEmail = prefRetrieve.getString("correctEmail", "");
        correctPassword= prefRetrieve.getString("correctPassword", "");


    }

    @Override
    protected void onStart() {
        super.onStart();

        //Retrieve the saved data with the key from the SharedPreferences Object
        correctEmail = prefRetrieve.getString("correctEmail", "");
        correctPassword = prefRetrieve.getString("correctPassword", "");
    }




    //detect whether there is any successful login user
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

}
