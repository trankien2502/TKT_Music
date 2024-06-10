package com.example.music;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyApplication extends Application {

    // Firebase url
    public static final String FIREBASE_URL = "https://musicbasic-27309-default-rtdb.firebaseio.com";

    public static final String CHANNEL_ID = "channel_music_basic_id";
    private static final String CHANNEL_NAME = "channel_music_basic_name";
    private FirebaseDatabase mFirebaseDatabase;

    public static MyApplication get(Context context) {
        return (MyApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mFirebaseDatabase = FirebaseDatabase.getInstance(FIREBASE_URL);
        createChannelNotification();
    }

    private void createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_MIN);
            channel.setSound(null, null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    public DatabaseReference getSongsDatabaseReference() {
        return mFirebaseDatabase.getReference("/songs");
    }

    public DatabaseReference getFeedbackDatabaseReference() {
        return mFirebaseDatabase.getReference("/feedback");
    }

    public DatabaseReference getCountViewDatabaseReference(int songId) {
        String songIds = String.valueOf(songId);
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/songs/" + songIds + "/count");
    }
    public DatabaseReference getArtistsDatabaseReference(){
        return mFirebaseDatabase.getReference("/artist");
    }
    public DatabaseReference getPlaylistsDatabaseReference(){
        return mFirebaseDatabase.getReference("/playlist");
    }
    public DatabaseReference getUserDatabaseReference(){
        return mFirebaseDatabase.getReference("/user");
    }
}
