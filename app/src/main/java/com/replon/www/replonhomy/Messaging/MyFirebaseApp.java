package com.replon.www.replonhomy.Messaging;

import com.google.firebase.database.FirebaseDatabase;

public class MyFirebaseApp extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}