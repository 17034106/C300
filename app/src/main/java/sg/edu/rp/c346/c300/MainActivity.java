package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    //Splash Page
    static int splashTimeOut = 4000;

    ProgressBar progressBar;
    TextView tvPercentage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_page);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar = findViewById(R.id.progress_bar);
        tvPercentage = findViewById(R.id.textViewPercentage);

        progressBar.setMax(100);
        progressBar.setScaleY(3f);
        progressAnimation();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                finish();
            }
        }, 2000);


    }


    public void progressAnimation(){
        ProgressBarAnimation anim = new ProgressBarAnimation(this, progressBar, tvPercentage, 0f, 100f);
        anim.setDuration(1200); // Specific time to show the splash page
        progressBar.setAnimation(anim);

    }


}
