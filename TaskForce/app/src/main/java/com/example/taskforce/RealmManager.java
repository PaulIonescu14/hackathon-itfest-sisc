package com.example.taskforce;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmManager extends Application {
    private static Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeRealm();
    }

    public static void initializeRealm() {
        if (realm == null || realm.isClosed()) {
            RealmConfiguration config = new RealmConfiguration.Builder()
                    .name("taskforce.realm")
                    .schemaVersion(1)
                    .allowWritesOnUiThread(true)
                    .allowQueriesOnUiThread(true)
                    .deleteRealmIfMigrationNeeded() // For development only
                    .build();

            realm = Realm.getInstance(config);
        }
    }

    public static Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            initializeRealm();
        }
        return realm;
    }

    public static void closeRealm() {
        if (realm != null && !realm.isClosed()) {
            realm.close();
            realm = null;
        }
    }

    public static void saveUser(final String email, final String password) {
        Realm realm = getRealm();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                User user = new User(email, password);
                bgRealm.copyToRealm(user);
            }
        });
    }

    public static java.util.List<User> getAllUsers() {
        Realm realm = getRealm();
        return realm.where(User.class).findAll();
    }

    public static User getUserByEmail(String email) {
        Realm realm = getRealm();
        return realm.where(User.class).equalTo("email", email).findFirst();
    }
}