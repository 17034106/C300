package sg.edu.rp.c346.c300.app;

import android.app.AlarmManager;
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
import java.util.Calendar;
import java.util.Date;

import sg.edu.rp.c346.c300.BaseLogoutActivity;
import sg.edu.rp.c346.c300.CartDisplay;
import sg.edu.rp.c346.c300.EmergencyWalletNotificationMain;
import sg.edu.rp.c346.c300.PaymentAlarm;
import sg.edu.rp.c346.c300.TestingParentEmergencyWallet;
import sg.edu.rp.c346.c300.TestingParentMain;
import sg.edu.rp.c346.c300.model.Budget;
import sg.edu.rp.c346.c300.model.Customer;
import sg.edu.rp.c346.c300.R;

public class MainpageActivity extends BaseLogoutActivity {

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

        count =0;

        getAllDenyList();
        setBackToDefault();//set the budget back to default
        prepaymentAlarm(); // set an AlarmManager for the prepayment

        //Showing the loading
        dialog = new ProgressDialog(this);

        dialog.setMessage("Loading. Please wait...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Customer").child(mUser.getUid());



        //retrieve data from firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
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
//        bundle.putString("school", school);  // passing data from activity to fragment
//        bundle.putString("name", name);
//        bundle.putDouble("balance", balance);

        if (view == findViewById(R.id.btnFoodMenu)){
            fragment = new FoodMenu();
            fragment.setArguments(bundle);  // passing data from activity to fragment
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragmentMainPage, fragment).addToBackStack(null);
            ft.commit();
        }
//        else if (view == findViewById(R.id.btnFeed)){
//            fragment = new FeedMenu();
//            fragment.setArguments(bundle);  // passing data from activity to fragment
//            FragmentManager fm = getFragmentManager();
//            FragmentTransaction ft = fm.beginTransaction();
//            ft.replace(R.id.fragmentMainPage, fragment).addToBackStack(null);
//            ft.commit();
//        }
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



    private void setBackToDefault(){
        Calendar calendar = Calendar.getInstance();
        int day1 = calendar.get(Calendar.DAY_OF_WEEK)-2;

        if (day1==-1){
            day1 = 6;
        }
        int day2 = day1-1;
        if (day2==-1){
            day2=6;
        }

        final int dayYesterday = day2;

        DatabaseReference drBudgetSaving = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drBudgetSaving.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String changeBudgetTiming = dataSnapshot.child("changeBudgetTiming").getValue().toString().trim();
                String todayDate = MainpageActivity.convertDateToString(Calendar.getInstance().getTime(), "dd/MM/yyyy");

                if (!changeBudgetTiming.equals(todayDate)) {

                    Budget budgetSaving = dataSnapshot.child("day").child(dayYesterday + "").getValue(Budget.class);

                    Calendar currentDateCalendar = Calendar.getInstance();
                    currentDateCalendar.add(Calendar.DATE, -1);
                    Date previousDayDate = currentDateCalendar.getTime();

                    double totalSavingForYesterday = 0;

                    totalSavingForYesterday += budgetSaving.getCategory().getFood().getLeft();
                    totalSavingForYesterday += budgetSaving.getCategory().getDrink().getLeft();
                    totalSavingForYesterday += budgetSaving.getCategory().getStationery().getLeft();
                    totalSavingForYesterday += budgetSaving.getCategory().getOthers().getLeft();

                    System.out.println((MainpageActivity.convertDateToString(previousDayDate, "dd/MM/yyyy")) + "_+_+_+_+_+_+__++_+_+_+_++_+_+");

                    updateCustomerGraph((MainpageActivity.convertDateToString(previousDayDate, "dd/MM/yyyy")), totalSavingForYesterday, FirebaseAuth.getInstance().getCurrentUser().getUid(), "saving");


                    double charity = budgetSaving.getCategory().getCharity().getLeft();
                    updateCustomerGraph((MainpageActivity.convertDateToString(previousDayDate, "dd/MM/yyyy")), charity,  FirebaseAuth.getInstance().getCurrentUser().getUid(), "charity");



                }


                DatabaseReference drBudget = FirebaseDatabase.getInstance().getReference().child("budget").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("day");
                drBudget.child(dayYesterday+"").child("changedAllowance").setValue(-1);
                drBudget.child(dayYesterday+"").child("category").child("food").child("changedValueMax").setValue(-1);
                drBudget.child(dayYesterday+"").child("category").child("food").child("changedValueMin").setValue(-1);

                drBudget.child(dayYesterday+"").child("category").child("drink").child("changedValueMax").setValue(-1);
                drBudget.child(dayYesterday+"").child("category").child("drink").child("changedValueMin").setValue(-1);

                drBudget.child(dayYesterday+"").child("category").child("stationery").child("changedValueMax").setValue(-1);
                drBudget.child(dayYesterday+"").child("category").child("stationery").child("changedValueMin").setValue(-1);

                drBudget.child(dayYesterday+"").child("category").child("charity").child("changedValueMax").setValue(-1);
                drBudget.child(dayYesterday+"").child("category").child("charity").child("changedValueMin").setValue(-1);

                drBudget.child(dayYesterday+"").child("category").child("others").child("changedValueMax").setValue(-1);
                drBudget.child(dayYesterday+"").child("category").child("others").child("changedValueMin").setValue(-1);


                drBudget.child(dayYesterday+"").child("category").child("food").child("left").setValue(0);
                drBudget.child(dayYesterday+"").child("category").child("drink").child("left").setValue(0);
                drBudget.child(dayYesterday+"").child("category").child("stationery").child("left").setValue(0);
                drBudget.child(dayYesterday+"").child("category").child("others").child("left").setValue(0);
                drBudget.child(dayYesterday+"").child("category").child("charity").child("left").setValue(0);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }




    //update the customer spending graph
    private void updateCustomerGraph(final String date1, final double price1, final String customerUID1,final String css){
        final String cssPlusS = css + "s";

//        //Get the price
        final DatabaseReference dbAccessGraph = FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css);
        dbAccessGraph.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                int day = Integer.valueOf(date1.substring(0, 2));
                int month = Integer.valueOf(date1.substring(3, 5));
                int year = Integer.valueOf(date1.substring(6, 10));
                String yearString = String.valueOf(year);


                Calendar c = Calendar.getInstance();
                c.set(Calendar.YEAR, year);
                c.set(Calendar.DAY_OF_MONTH, day);
                c.set(Calendar.MONTH, month - 1);

                System.out.println("Month: " + new SimpleDateFormat("MMMM").format(c.getTime()));
                System.out.println("Day: " + c.get(Calendar.DAY_OF_MONTH));

                int numOfYears = Integer.valueOf(dataSnapshot.child("year").child("numOfYears").getValue().toString());

                //Check HO dates against Graph Dates then setValue of the profits from HO into Graph date's profit

                for (int a = 0; a < numOfYears; a++) {
                    if (yearString.equals(dataSnapshot.child("year").child(String.valueOf(a)).child("year").getValue().toString())) {


                        String fbDate = dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                .child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH) - 1))
                                .child("date").getValue().toString();


                        System.out.println("Input Date: " + date1.substring(0,10));
                        System.out.println("Firebase Date: " + fbDate);
                        if (date1.substring(0,10).equals(fbDate)) {
                            System.out.println("Matched!");
                            double currentProfit = 0;
                            currentProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH) - 1))
                                    .child(cssPlusS).getValue().toString());

                            double newProfit = currentProfit + price1;
                            System.out.println("New Profit: " + newProfit);

                            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css).child("year")
                                    .child(String.valueOf(a)).child("months").child(String.valueOf(c.get(Calendar.MONTH))).child("dates").child(String.valueOf(c.get(Calendar.DAY_OF_MONTH)-1)).child(cssPlusS).setValue(newProfit);


                            //Update Month Profit
                            double currentMonthProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child(cssPlusS).getValue().toString());

                            double newMonthProfit = currentMonthProfit + price1;

                            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css).child("year").child(String.valueOf(a)).child("months")
                                    .child(String.valueOf(c.get(Calendar.MONTH))).child(cssPlusS).setValue(newMonthProfit);

                            //Update Year Profit
                            double currentYearProfit = Double.parseDouble(dataSnapshot.child("year").child(String.valueOf(a)).child(cssPlusS).getValue().toString());

                            double newYearProfit = currentYearProfit + price1;

                            FirebaseDatabase.getInstance().getReference().child("graphCustomer").child(customerUID1).child(css).child("year").child(String.valueOf(a)).child(cssPlusS).setValue(newYearProfit);

                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void prepaymentAlarm(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    23,
                    58,
                    40);


        setAlarm(calendar.getTimeInMillis());



    }


    private void setAlarm(long time){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, PaymentAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}
