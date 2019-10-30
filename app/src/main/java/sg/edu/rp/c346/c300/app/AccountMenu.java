package sg.edu.rp.c346.c300.app;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import sg.edu.rp.c346.c300.BudgetInformation;
import sg.edu.rp.c346.c300.CartDisplay;
import sg.edu.rp.c346.c300.CollectionOrderPage;
import sg.edu.rp.c346.c300.DenyListMain;
import sg.edu.rp.c346.c300.EmergencyWallet;
import sg.edu.rp.c346.c300.GoalSavingAll;
import sg.edu.rp.c346.c300.HistoryTransactionMainPage;
import sg.edu.rp.c346.c300.MainActivity;
import sg.edu.rp.c346.c300.ParentAuthentication;
import sg.edu.rp.c346.c300.QrCodePay;
import sg.edu.rp.c346.c300.QrCodeScannerPay;
import sg.edu.rp.c346.c300.R;
import sg.edu.rp.c346.c300.SettingEditablePage;
import sg.edu.rp.c346.c300.SettingPage;
import sg.edu.rp.c346.c300.StatisticMain;
import sg.edu.rp.c346.c300.TestingParentMain;
import sg.edu.rp.c346.c300.model.Customer;


public class AccountMenu extends Fragment {


    TextView accountName, accountBalance;

    Button btnSignOut;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
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




//        //region get data from activity to Fragment
//        Bundle bundle = this.getArguments();
//        if (bundle!=null){
//            String name = bundle.getString("name", "fail");
//            accountName.setText(name);
//            double balance = bundle.getDouble("balance", 0);
//            if (balance==0){
//                accountBalance.setText(Html.fromHtml(String.format("<b>$%.2f</b>", balance)));
//            }
//            accountBalance.setText(String.format("$%.2f", balance));
//
//        }
//        //endregion

        //region retrieving customer info
        DatabaseReference drCustomer = FirebaseDatabase.getInstance().getReference().child("Customer").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        drCustomer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Customer customer = dataSnapshot.getValue(Customer.class);
                accountName.setText(customer.getCustomername());

                accountBalance.setText(Html.fromHtml(String.format("<b>$%.2f</b>", customer.getBalance())));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //endregion






        //Intent to show the information of the customer
        view.findViewById(R.id.accountSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingPage.class);
                startActivity(intent);
            }
        });


        //intent to show the collection order
        view.findViewById(R.id.accountPreOrderMade).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CollectionOrderPage.class);
                startActivity(intent);
            }
        });


        view.findViewById(R.id.accountHistoryTransaction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HistoryTransactionMainPage.class);
                startActivity(intent);
            }
        });



        view.findViewById(R.id.accountQrScanner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QrCodeScannerPay.class);
                startActivity(intent);
            }
        });



        view.findViewById(R.id.accountGoalSaving).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoalSavingAll.class);
                startActivity(intent);
            }
        });


        view.findViewById(R.id.accountBudgetSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), BudgetInformation.class);
                intent.putExtra("firstTime",false);
                startActivity(intent);
            }
        });

        view.findViewById(R.id.accountEmergencyWallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EmergencyWallet.class);
                startActivity(intent);
            }
        });



        view.findViewById(R.id.accountParent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ParentAuthentication.class);
                startActivity(intent);
            }
        });


        view.findViewById(R.id.accountDenyList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DenyListMain.class);
                intent.putExtra("parent", false);
                startActivity(intent);
            }
        });


        view.findViewById(R.id.accountStatistic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), StatisticMain.class);
                startActivity(intent);
            }
        });


        return view;
    }


}
