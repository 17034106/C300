package sg.edu.rp.c346.c300.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.app.FragmentManager;

import android.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.edu.rp.c346.c300.CartDisplay;
import sg.edu.rp.c346.c300.EmergencyWalletNotificationMain;
import sg.edu.rp.c346.c300.TestingParentEmergencyWallet;
import sg.edu.rp.c346.c300.TestingParentMain;
import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.R;

public class MainpageActivity extends AppCompatActivity {

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;


    public static ArrayList<Integer> jurongPointDenyList = new ArrayList<>();
    public static ArrayList<Integer> republicPolytechnicDenyList = new ArrayList<>();
    public static ArrayList<Integer> singaporePolytechnicDenyList = new ArrayList<>();


    //Firebase Authentication
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    DatabaseReference databaseReference;

    public static String school; // use for getting the school to IndividualEditFoodDisplay
    String name;
    double balance;

    //Showing the loading
    static ProgressDialog dialog;


    public static int count=0; // for push notification customer
    public static ValueEventListener listener;
    public static DatabaseReference drPushNotificationCustomer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        getAllDenyList();

        //Showing the loading
        dialog = new ProgressDialog(this);

        dialog.setMessage("Loading. Please wait...");
        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customer").child(mUser.getUid());



        //retrieve data from firebase
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Customer customer = dataSnapshot.getValue(Customer.class); // using class to get data from firebase

                //Two ways to get the data -First is using child.getvalue -Second is using class.
                name = dataSnapshot.child("customername").getValue().toString();
                school = customer.getCustomerschool();
                balance = customer.getBalance();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainpageActivity.this, databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        changeFragment(findViewById(R.id.btnFoodMenu));


//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Do something after 5s = 5000ms
//
//                //cancel the dialog
//                dialog.dismiss();
//                changeFragment(findViewById(R.id.btnFoodMenu));
//
//            }
//        }, 2000);


        //region push notification for customer
        drPushNotificationCustomer = FirebaseDatabase.getInstance().getReference().child("pushNotificationCustomer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        listener = drPushNotificationCustomer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                if (count !=0) {

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String title = dataSnapshot.child("title").getValue().toString();
                            String body = dataSnapshot.child("body").getValue().toString();
                            String situation = dataSnapshot.child("situation").getValue().toString();
                            pushNotificationNow(title, body, situation, getApplication());
                        }
                    }, 1500);

                }

                count++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //endregion







    }

    //region push notification
    public static void pushNotificationNow(String title, String body, String situation, Context context){

        Class typeOfClass = MainpageActivity.class;

        if (situation.equalsIgnoreCase("emergency")) {
            typeOfClass = EmergencyWalletNotificationMain.class;
        }

        Intent intent = new Intent(context, typeOfClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification.Builder(context.getApplicationContext())
                .setContentTitle(title).setContentText(body)
                .setSmallIcon(R.drawable.bear_without_background)
                .setSound(alarmSound)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();


        //setContentTitle = title
        //setContentText = body
        //setSmallIcon = image icon

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(0,notify);
    }
    //endregion




    // Method to change the fragment such as Food menu, Feed, Notification, Account
    public void changeFragment(View view){

        android.app.Fragment fragment;

        Bundle bundle = new Bundle(); // passing data from activity to fragment
        bundle.putString("school", school);  // passing data from activity to fragment
        bundle.putString("name", name);
        bundle.putDouble("balance", balance);

        if (view == findViewById(R.id.btnFoodMenu)){
            fragment = new FoodMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment).addToBackStack(null);
            ft.commit();
        }
        else if (view == findViewById(R.id.btnFeed)){
            fragment = new FeedMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment).addToBackStack(null);
            ft.commit();
        }
        else if ( view == findViewById(R.id.btnNotification)){
            fragment = new NotificationMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment).addToBackStack(null);
            ft.commit();
        }
        else if (view == findViewById(R.id.btnAccount)){
            fragment = new AccountMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment).addToBackStack(null);
            ft.commit();
        }


    }


    public static Date convertStringToDate(String dateString, String pattern){
        SimpleDateFormat inputFormat = new SimpleDateFormat(pattern);

        Date dateDate = null;

        try{
            dateDate= inputFormat.parse(dateString);
        }
        catch (ParseException e){

        }

        return dateDate;
    }


    public static String convertDateToString(Date dateDate, String pattern){
        SimpleDateFormat inputFormat = new SimpleDateFormat(pattern);

        String dateString =inputFormat.format(dateDate);

        return dateString;
    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        android.app.Fragment fragment = getFragmentManager().findFragmentById(R.id.fragmentMainPage);
        if (fragment instanceof FoodMenu){
            finish();

        }
        // go back to the previous fragment if it is not FoodMenu fragment
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    //region get all the deny list
    public void getAllDenyList(){
        DatabaseReference drDenyList = FirebaseDatabase.getInstance().getReference().child("denyList").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("school");
        drDenyList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                jurongPointDenyList.clear();
                republicPolytechnicDenyList.clear();
                singaporePolytechnicDenyList.clear();
                for (DataSnapshot i : dataSnapshot.getChildren()){
                    if (i.getKey().equals("Jurong Point")){
                        int numOfDeny = Integer.parseInt(i.child("numOfDeny").getValue().toString());
                        for (int h =0; h<numOfDeny;h++){
                            jurongPointDenyList.add(Integer.parseInt(i.child(h+"").getValue().toString()));

                        }
                    }
                    else if (i.getKey().equals("Republic Polytechnic")){
                        int numOfDeny = Integer.parseInt(i.child("numOfDeny").getValue().toString());
                        for (int h =0; h<numOfDeny;h++){
                            republicPolytechnicDenyList.add(Integer.parseInt(i.child(h+"").getValue().toString()));

                        }
                    }
                    else if (i.getKey().equals("Singapore Polytechnic")){
                        int numOfDeny = Integer.parseInt(i.child("numOfDeny").getValue().toString());
                        for (int h =0; h<numOfDeny;h++){
                            singaporePolytechnicDenyList.add(Integer.parseInt(i.child(h+"").getValue().toString()));

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //endregion




}
