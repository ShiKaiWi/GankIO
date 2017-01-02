package com.example.xkwei.gankio;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by xkwei on 02/01/2017.
 */

public class App extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .name("gankio.realm")
                .build());
    }
}
