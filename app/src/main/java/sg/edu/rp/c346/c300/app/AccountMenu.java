package sg.edu.rp.c346.c300.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import sg.edu.rp.c346.c300.MainActivity;
import sg.edu.rp.c346.c300.R;


public class AccountMenu extends Fragment {

    Button btnSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_menu, container, false);

        btnSignOut = view.findViewById(R.id.btnSignOut);


        // Sign out of the account
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });


        return view;
    }


}
