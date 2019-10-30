package sg.edu.rp.c346.c300;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import sg.edu.rp.c346.c300.app.MainpageActivity;


public class TestingParentMain extends AppCompatActivity {

    int count =0;

    TextView balance, parentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_main);

        parentName = findViewById(R.id.accountName);
        balance = findViewById(R.id.ewallet);


        DatabaseReference dbAccessCustomer = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dbAccessCustomer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double ewallet = Double.parseDouble(dataSnapshot.child("balance").getValue().toString());
                balance.setText("Balance: " + String.format("$%.2f",ewallet));

                parentName.setText(dataSnapshot.child("Parent").child("parentname").getValue().toString());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        findViewById(R.id.xLimit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentMain.this, ParentCategorizationLimit.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.emergencyWalletRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentMain.this, TestingParentEmergencyWallet.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.transactionHistory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentMain.this, TestingParentTransactionHistory.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentMain.this, TestingParentProfile.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.topUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentMain.this, TestingParentTopupBalance.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.denyList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestingParentMain.this, DenyListMain.class);
                intent.putExtra("parent", true);
                startActivity(intent);
            }
        });

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("pushNotificationParent").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
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
                    }, 800);

                }

                count++;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        MainpageActivity.drPushNotificationCustomer.removeEventListener(MainpageActivity.listener); // remove the listener in the kid app


        DatabaseReference drRemind = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drRemind.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double balance = Double.parseDouble(dataSnapshot.child("balance").getValue().toString());
                double remind = Double.parseDouble(dataSnapshot.child("remind").getValue().toString());

                if (balance<remind){
                    DatabaseReference drPushNotificationParent = FirebaseDatabase.getInstance().getReference().child("pushNotificationParent").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    drPushNotificationParent.child("title").setValue("Top Up Balance");
                    drPushNotificationParent.child("body").setValue("Low Balance");
                    drPushNotificationParent.child("situation").setValue("TopUp");
                    drPushNotificationParent.child("numOfRequest").setValue(new Random().nextInt(100));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    //region push notification
    public static void pushNotificationNow(String title, String body, String situation, Context context){

        Class typeOfClass = TestingParentMain.class;

        if (situation.equalsIgnoreCase("emergency")) {
            typeOfClass = TestingParentEmergencyWallet.class;
        }
        else if (situation.equalsIgnoreCase("TopUp")){
            typeOfClass = TestingParentTopupBalance.class;
        }

        Intent intent = new Intent(context, typeOfClass);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notify = new Notification.Builder(context.getApplicationContext()).setContentTitle(title).setContentText(body).setSmallIcon(R.drawable.bear_without_background).setSound(alarmSound).setContentIntent(pendingIntent).setAutoCancel(true).build();
        //setContentTitle = title
        //setContentText = body
        //setSmallIcon = image icon

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(0,notify);
    }
    //endregion


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MainpageActivity.count =0;
        MainpageActivity.drPushNotificationCustomer.addValueEventListener(MainpageActivity.listener);
    }
}
