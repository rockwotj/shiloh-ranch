package com.appspot.shiloh_ranch.fragments.sermons.audio;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Sermon;

import java.io.IOException;

public class SermonService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    public static final String ACTION_PLAY = "com.appspot.shiloh_ranch.sermons.action.PLAY";
    public static final String ACTION_STOP = "com.appspot.shiloh_ranch.sermons.action.STOP";
    public static final String ACTION_CHANGE = "com.appspot.shiloh_ranch.sermons.action.CHANGE";
    public static final String ACTION_PAUSE = "com.appspot.shiloh_ranch.sermons.action.PAUSE";
    public static final String ACTION_PREPARED = "com.appspot.shiloh_ranch.sermons.action.PREPARED";

    private Sermon mCurrentSermon;
    private BroadcastReceiver mReceiver;
    private boolean mPlaybackReady;
    private MediaPlayer mMediaPlayer;

    // TODO: Handle audio focus & using wifi locks & and other fancy stuff

    public static Intent createChangeIntent(String entityKey, String audioLink, String title, String subtitle) {
        Intent intent = new Intent(SermonService.ACTION_CHANGE);
        intent.putExtra("KEY", entityKey);
        intent.putExtra("LINK", audioLink);
        intent.putExtra("TITLE", title);
        intent.putExtra("SUBTITLE", subtitle);
        return intent;
    }

    public static Intent createPlayIntent() {
        return new Intent(SermonService.ACTION_PLAY);
    }

    public static Intent createPauseIntent() {
        return new Intent(SermonService.ACTION_PAUSE);
    }

    private void handleIntent(Intent intent) {
        if (intent == null || intent.getAction() == null)
            return;
        String action = intent.getAction();
        if (action.equalsIgnoreCase(ACTION_PLAY)) {
            if (!mPlaybackReady) {
                Toast.makeText(this, getString(R.string.sermon_buffering), Toast.LENGTH_SHORT).show();
                return;
            }
            mMediaPlayer.start();
            updateNotification();
        } else if (action.equalsIgnoreCase(ACTION_PAUSE)) {
            mMediaPlayer.pause();
            updateNotification();
        } else if (action.equalsIgnoreCase(ACTION_CHANGE)) {
            mCurrentSermon = new Sermon();
            String key = intent.getStringExtra("KEY");
            if (key.equals(mCurrentSermon.getEntityKey())) {
                return;
            }
            mCurrentSermon.setEntityKey(key);
            mCurrentSermon.setTitle(Html.fromHtml(intent.getStringExtra("TITLE")).toString());
            mCurrentSermon.setAudioLink(intent.getStringExtra("LINK"));
            mCurrentSermon.setDate(intent.getStringExtra("SUBTITLE"));
            mPlaybackReady = false;
            if (mMediaPlayer == null) {
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.setOnCompletionListener(this);
                mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            } else {
                mMediaPlayer.reset();
            }
            try {
                mMediaPlayer.setDataSource(mCurrentSermon.getAudioLink());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("SRCC", "Error loading sermon", e);
                Toast.makeText(this, getString(R.string.sermon_load_error), Toast.LENGTH_SHORT).show();
            }
            mMediaPlayer.prepareAsync();
            updateNotification();
        } else if (action.equalsIgnoreCase(ACTION_STOP)) {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            stopForeground(true);
        }
    }

    private void updateNotification() {
        PendingIntent playPauseIntent = PendingIntent.getBroadcast(this, 0,
                mMediaPlayer.isPlaying() ? createPauseIntent() : createPlayIntent(),
                PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_audio))
                .setTicker(getString(R.string.playing_colon) + mCurrentSermon.getTitle())
                .setContentTitle(mCurrentSermon.getTitle())
                .setContentText(mCurrentSermon.getDate())
                .setShowWhen(false)
                .addAction(mMediaPlayer.isPlaying() ? R.drawable.ic_stat_pause : R.drawable.ic_stat_play,
                        mMediaPlayer.isPlaying() ? getString(R.string.pause) : getString(R.string.play),
                        playPauseIntent)
                .addAction(R.drawable.ic_stat_stop, getString(R.string.stop), stopIntent);
        startForeground(1, builder.build());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("SRCC", "Created SermonService!");
        mCurrentSermon = new Sermon();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_PLAY);
        filter.addAction(ACTION_PAUSE);
        filter.addAction(ACTION_STOP);
        filter.addAction(ACTION_CHANGE);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleIntent(intent);
            }
        };
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        }
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        sendBroadcast(new Intent(ACTION_STOP));
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        sendBroadcast(new Intent(ACTION_STOP));
        mp.reset();
        Log.e("SRCC", "MEDIA PLAYER ERROR, what:" + what + ", extra: " + extra);
        Toast.makeText(this, getString(R.string.sermon_play_error), Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mPlaybackReady = true;
        this.sendBroadcast(new Intent(ACTION_PREPARED));
    }


}
