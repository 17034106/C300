package sg.edu.rp.c346.c300;

import android.content.Context;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

public class FingerprintAuthentication extends AppCompatActivity implements FingerPrintAuthCallback {

    FingerPrintAuthHelper mFingerPrintAuthHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fingerprint_authentication);

        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);


        //region  pop up this activity with full screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width), (int)(height));
        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.gravity = Gravity.CENTER;
//        params.x = 0;
//        params.y = -20;
        getWindow().setAttributes(params);
        //endregion


        findViewById(R.id.fingerprintCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        //Device does not have finger print scanner.
        Toast.makeText(this, "No fingerprint scanner found!", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onNoFingerPrintRegistered() {
        //There are no finger prints registered on this device.

        Toast.makeText(this, "No fingerprint register in the setting", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBelowMarshmallow() {
        //Device running below API 23 version of android that does not support finger print authentication.
        Toast.makeText(this, "Your device does not support fingerprint authentication!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
//        Toast.makeText(this, "Authentication Successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(FingerprintAuthentication.this, loginpage.class);
        intent.putExtra("result",true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                //Cannot recognize the fingerprint scanned.
                Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(500);
                Toast.makeText(this, "Invalid Fingerprint", Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                //This is not recoverable error. Try other options for user authentication. like pin, password.
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                //Any recoverable error. Display message to the user.
                break;
        }
    }



}
