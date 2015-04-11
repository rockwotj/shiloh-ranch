package com.appspot.shiloh_ranch.fragments.sermons.audio;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.appspot.shiloh_ranch.activities.MainActivity;

public class SermonPlayer extends Service {
    private static final String ACTION_PLAY = "com.appspot.shiloh_ranch.sermons.action.PLAY";
    private static final String ACTION_STOP = "com.appspot.shiloh_ranch.sermons.action.STOP";
    private static final String ACTION_PAUSE = "com.appspot.shiloh_ranch.sermons.action.PAUSE";


    public SermonPlayer() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            String url = "http://traffic.libsyn.com/shilohranch/Following_To_Freedom_22215.mp3";
            // assign the song name to songName
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);
            // Notification builder
            startForeground(1, null);
        } else if (intent.getAction().equals(ACTION_STOP)) {
            stopForeground(true);
        } else if (intent.getAction().equals(ACTION_PAUSE)) {

        }
        return 0;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
