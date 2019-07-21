package sg.edu.rp.c346.c300.app;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.zxing.qrcode.encoder.QRCode;

import sg.edu.rp.c346.c300.QrCodeScannerPay;
import sg.edu.rp.c346.c300.R;


public class FeedMenu extends Fragment {

    TextView usernameFeed;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_feed_menu, container, false);

        View v = inflater.inflate(R.layout.fragment_feed_menu, container, false); // this will be become the view due to fragment



        return v;
    }



}
