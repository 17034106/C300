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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.rp.c346.c300.app.MainpageActivity;


public class TestingParentMain extends AppCompatActivity {

    int count =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing_parent_main);




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


        MainpageActivity.drPushNotificationCustomer.removeEventListener(MainpageActivity.listener);



    }

    //region push notification
    public static void pushNotificationNow(String title, String body, String situation, Context context){

        Class typeOfClass = TestingParentMain.class;

        if (situation.equalsIgnoreCase("emergency")) {
            typeOfClass = TestingParentEmergencyWallet.class;
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
