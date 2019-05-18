package sg.edu.rp.c346.c300.app;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.model.School;

public class MainpageActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;



    //Firebase Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference databaseReference;

    String school; // example
    String name;

    //Showing the loading
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);


        //Showing the loading
        dialog = new ProgressDialog(this);

        dialog.setMessage("Loading. Please wait...");
        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customer").child(mUser.getUid());

        DatabaseReference databaseReferenceFood = FirebaseDatabase.getInstance().getReference().child("preorder");


        //retrieve data from firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Customer customer = dataSnapshot.getValue(Customer.class);

                //Two ways to get the data -First is using child.getvalue -Second is using class.
                name = dataSnapshot.child("customername").getValue().toString();
                school = customer.getCustomerschool();
                Log.d("Name JSON", dataSnapshot.getValue().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainpageActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        changeFragment(findViewById(R.id.btnFoodMenu));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms

                //cancel the dialog
                dialog.dismiss();
                changeFragment(findViewById(R.id.btnFoodMenu));

            }
        }, 2000);



    }


    // Method to change the fragment such as Food menu, Feed, Notification, Account
    public void changeFragment(View view){

        android.app.Fragment fragment;

        Bundle bundle = new Bundle(); // passing data from activity to fragment
        bundle.putString("school", school);  // passing data from activity to fragment
        bundle.putString("name", name);

        if (view == findViewById(R.id.btnFoodMenu)){
            fragment = new FoodMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment);
            ft.commit();
        }
        else if (view == findViewById(R.id.btnFeed)){
            fragment = new FeedMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment);
            ft.commit();
        }
        else if ( view == findViewById(R.id.btnNotification)){
            fragment = new NotificationMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment);
            ft.commit();
        }
        else if (view == findViewById(R.id.btnAccount)){
            fragment = new AccountMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment);
            ft.commit();
        }


    }

    @Override
    protected void onStart() {
        super.onStart();



    }
}
