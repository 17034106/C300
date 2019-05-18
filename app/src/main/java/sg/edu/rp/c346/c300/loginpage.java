package sg.edu.rp.c346.c300;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
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
    CheckBox rememberMe;

    // For AutoComplete (Display all username)
    ArrayList<String> usernameList = new ArrayList<String>(); // for AutoComplete (Display all username)


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


        //Showing the loading
        dialog = new ProgressDialog(this);


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
        rememberMe = findViewById(R.id.rememberMe);
        //endregion

        mAuth = FirebaseAuth.getInstance();


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

                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()){

                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(loginpage.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(loginpage.this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
                                dialog.dismiss(); //cancel the dialog
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
