package service;

import android.content.Context;

import cache.DatabaseHandler;
import models.Config;

/**
 * Created by Macbook on 30/11/16.
 */
public class AppSetting {

    DatabaseHandler db;
    Context context;

    public AppSetting(Context context){

        this.context = context;
        db = new DatabaseHandler(context);

    }

    public Config getConfig() {
        return db.getConfig();
    }

    public void setConfig(Config config) {
        db.addConfig(config);
        return;
    }

    public void setDistanceConfig(String configDistance) {
        db.updateDistanceConfig(configDistance);
        return;
    }

    public void setTemperatureConfig(String configTemp) {
        db.updateTempConfig(configTemp);
        return;
    }

    public void setNotificationsConfig(String config) {
        db.updateNotificationConfig(config);
        return;
    }

    public void setUpdateLastOfferIDConfig(String config) {
        db.setUpdateLastOfferIDConfig(config);
        return;
    }

}
