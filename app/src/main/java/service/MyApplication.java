package service;

import android.support.multidex.MultiDexApplication;
import com.onesignal.OneSignal;

/**
 * Created by Macbook on 30/11/16.
 */
public class MyApplication extends MultiDexApplication {

    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        OneSignal.startInit(this).setNotificationOpenedHandler(new NotificationsOpenHandler()).init();
        mInstance = this;
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
