package com.appspot.shiloh_ranch.fragments.sermons.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by rockwotj on 4/11/2015.
 * <p/>
 * This is a wrapper for the media player class to provide playback via HTTP Streaming
 */
public class AudioStreamPlayer {
    private final Context mContext;
    private MediaPlayer mMediaPlayer = null;
    private WifiManager.WifiLock mWifiLock;

    AudioStreamPlayer(Context context, String url) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(url);
        } catch (IOException e) {
            Log.e("SRCC", "Cannot find url: " + url, e);
        }
        mMediaPlayer.prepareAsync(); // prepare async to not block main thread
        mMediaPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
        mWifiLock = ((WifiManager) mContext.getSystemService(Context.WIFI_SERVICE))
                .createWifiLock(WifiManager.WIFI_MODE_FULL, "sermon-lock");

        mWifiLock.acquire();
        mWifiLock.release();
    }

}
