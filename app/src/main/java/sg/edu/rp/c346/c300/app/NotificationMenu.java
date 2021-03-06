package sg.edu.rp.c346.c300.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import sg.edu.rp.c346.c300.EmergencyWalletNotificationMain;
import sg.edu.rp.c346.c300.HistoryTransactionQRPayment;
import sg.edu.rp.c346.c300.PreOrderUpdates;
import sg.edu.rp.c346.c300.R;


public class NotificationMenu extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_menu, container, false);


        view.findViewById(R.id.notificationPreOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PreOrderUpdates.class);
                intent.putExtra("page","PreOrder");
                startActivity(intent);
            }
        });


        view.findViewById(R.id.notificationWalkIn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PreOrderUpdates.class);
                intent.putExtra("page","WalkIn");
                startActivity(intent);
            }
        });


        view.findViewById(R.id.notificationOutside).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryTransactionQRPayment.class);
                intent.putExtra("parent",false);
                startActivity(intent);
            }
        });


        view.findViewById(R.id.notificationEWallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EmergencyWalletNotificationMain.class);
                startActivity(intent);
            }
        });



        return view;
    }


}
