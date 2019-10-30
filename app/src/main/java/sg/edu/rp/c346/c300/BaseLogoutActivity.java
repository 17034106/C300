package sg.edu.rp.c346.c300;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class BaseLogoutActivity extends AppCompatActivity implements LogoutListenter{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyLogoutApp) getApplication()).registerSessionListener(this);
        ((MyLogoutApp) getApplication()).startUserSession();

    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ((MyLogoutApp) getApplication()).onUserInteracted();
    }

    @Override
    public void onSessionLogout() {

        startActivity(new Intent(this, loginpage.class));
        finish();
    }
}
