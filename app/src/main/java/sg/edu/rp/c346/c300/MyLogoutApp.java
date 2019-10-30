package sg.edu.rp.c346.c300;

import android.app.Application;

import java.util.Timer;
import java.util.TimerTask;

public class MyLogoutApp extends Application {


    private LogoutListenter listener;

    private Timer timer;

    public void startUserSession() {
        cancelTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listener.onSessionLogout();

            }
        }, 300000); // 5min = 300000
    }

    private void cancelTimer(){
        if (timer!=null){
            timer.cancel();
        }
    }

    public void registerSessionListener(LogoutListenter listener) {
        this.listener = listener;
    }

    public void onUserInteracted() {

        startUserSession();
    }

}
