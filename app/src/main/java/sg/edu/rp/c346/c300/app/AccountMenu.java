package sg.edu.rp.c346.c300.app;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import sg.edu.rp.c346.c300.CartDisplay;
import sg.edu.rp.c346.c300.MainActivity;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.SettingEditablePage;
import sg.edu.rp.c346.c300.SettingPage;


public class AccountMenu extends Fragment {


    TextView accountName, accountBalance;

    Button btnSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_menu, container, false);

        btnSignOut = view.findViewById(R.id.btnSignOut);
        accountName = view.findViewById(R.id.accountName);
        accountBalance = view.findViewById(R.id.accountBalance);


        //region Sign out of the account
        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

            }
        });
        //endregion

        //intent to the cart
        view.findViewById(R.id.accountFloatingButtonCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CartDisplay.class);
                startActivity(intent);
            }
        });

        // go back to the menu page
        view.findViewById(R.id.accountFloatingButtonMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.Fragment fragment = new FoodMenu();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.fragmentMainPage, fragment).addToBackStack(null).commit();
            }
        });




        //region get data from activity to Fragment
        Bundle bundle = this.getArguments();
        if (bundle!=null){
            String name = bundle.getString("name", "fail");
            accountName.setText(name);
            double balance = bundle.getDouble("balance", 0);
            if (balance==0){
                accountBalance.setText(Html.fromHtml(String.format("<b>$%.2f</b>", balance)));
            }
            accountBalance.setText(String.format("$%.2f", balance));

        }
        //endregion



        view.findViewById(R.id.accountSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingPage.class);
                startActivity(intent);
            }
        });




        return view;
    }


}
