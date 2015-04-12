package com.appspot.shiloh_ranch.fragments.sermons;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Sermon;
import com.appspot.shiloh_ranch.database.Database;
import com.appspot.shiloh_ranch.fragments.IContentFragment;
import com.appspot.shiloh_ranch.fragments.sermons.audio.SermonService;

import java.util.List;

/**
 * A fragment representing a list of Sermons.
 * <p/>
 * <p/>
 */
public class SermonsFragment extends IContentFragment implements AdapterView.OnItemClickListener, View.OnClickListener {

    private static final int PLAY_STATE = 0;
    private static final int PAUSE_STATE = 1;

    private View mEmptyView;
    private ListView mPostList;
    private SermonAdapter mAdapter;
    private AnimationDrawable[] mAnimations;
    private BroadcastReceiver mReceiver;
    private View mPlaybackView;
    private ImageButton mPlayPauseButton;
    private View mBufferingSpinner;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SermonsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = new Intent(getActivity(), SermonService.class);
        getActivity().startService(startIntent);
        mAnimations = new AnimationDrawable[2];
        mAnimations[0] = (AnimationDrawable) getResources().getDrawable(R.drawable.play_pause_animation);
        mAnimations[1] = (AnimationDrawable) getResources().getDrawable(R.drawable.pause_play_animation);
        mAnimations[0].setOneShot(true);
        mAnimations[1].setOneShot(true);
        createReceiver();
    }

    private void createReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SermonService.ACTION_PLAY);
        filter.addAction(SermonService.ACTION_PAUSE);
        filter.addAction(SermonService.ACTION_STOP);
        filter.addAction(SermonService.ACTION_PREPARED);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null)
                    return;
                String action = intent.getAction();
                if (action.equalsIgnoreCase(SermonService.ACTION_PLAY)) {
                    if (!mPlayPauseButton.getTag().equals(PLAY_STATE)) {
                        togglePlayPauseButton();
                    }
                } else if (action.equalsIgnoreCase(SermonService.ACTION_PAUSE)) {
                    if (mPlayPauseButton.getTag().equals(PLAY_STATE)) {
                        togglePlayPauseButton();
                    }
                } else if (action.equalsIgnoreCase(SermonService.ACTION_STOP)) {
                    if (mPlayPauseButton.getTag().equals(PLAY_STATE)) {
                        togglePlayPauseButton();
                    }
                    mPlaybackView.setVisibility(View.GONE);
                } else if (action.equalsIgnoreCase(SermonService.ACTION_PREPARED)) {
                    mBufferingSpinner.setVisibility(View.GONE);
                    mPlayPauseButton.setVisibility(View.VISIBLE);
                }
            }
        };
        getActivity().registerReceiver(mReceiver, filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sermons, container, false);
        mPostList = (ListView) rootView.findViewById(R.id.sermon_list);
        mPostList.setOnItemClickListener(this);
        mEmptyView = rootView.findViewById(R.id.empty);
        mPlaybackView = rootView.findViewById(R.id.sermon_playback_view);
        mPlayPauseButton = (ImageButton) mPlaybackView.findViewById(R.id.play_pause_button);
        mBufferingSpinner = mPlaybackView.findViewById(R.id.buffering);
        mPlayPauseButton.setTag(PAUSE_STATE);
        mPlayPauseButton.setOnClickListener(this);
        mPlaybackView.setVisibility(View.GONE);
        refresh();
        return rootView;
    }

    @Override
    public String getTitle() {
        return "Sermons";
    }

    @Override
    public void refresh() {
        Database db = Database.getDatabase(getActivity());
        List<Sermon> sermons = db.getAllSermons();
        mPostList.setVisibility(!sermons.isEmpty() ? View.VISIBLE : View.GONE);
        mEmptyView.setVisibility(!sermons.isEmpty() ? View.GONE : View.VISIBLE);
        if (!sermons.isEmpty()) {
            mAdapter = new SermonAdapter(getActivity(), sermons);
            mPostList.setAdapter(mAdapter);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Sermon sermon = mAdapter.getItem(position);
        mAdapter.bindSermonToView(position, mPlaybackView);
        if (mPlaybackView.getVisibility() == View.GONE) {
            mBufferingSpinner.setVisibility(View.VISIBLE);
            mPlayPauseButton.setVisibility(View.GONE);
            mPlaybackView.setVisibility(View.VISIBLE);
        } else {
            mBufferingSpinner.setVisibility(View.VISIBLE);
            mPlayPauseButton.setVisibility(View.GONE);
            if (mPlayPauseButton.getTag().equals(PLAY_STATE)) {
                togglePlayPauseButton();
            }
        }
        String key = sermon.getEntityKey();
        String audioLink = sermon.getAudioLink();
        String title = mAdapter.getSermonDisplayTitle(sermon);
        String subtitle = mAdapter.getSermonDisplaySubtitle(sermon);
        Intent intent = SermonService.createChangeIntent(key, audioLink, title, subtitle);
        getActivity().sendBroadcast(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (mPlayPauseButton.getTag().equals(PLAY_STATE)) {
            intent = SermonService.createPauseIntent();
        } else {
            intent = SermonService.createPlayIntent();
        }
        getActivity().sendBroadcast(intent);
        togglePlayPauseButton();
    }

    public void togglePlayPauseButton() {
        final ImageButton playPauseButton = mPlayPauseButton;
        final int animationNumber = (((int) playPauseButton.getTag()) + 1) % 2;
        playPauseButton.setTag(animationNumber);
        ReplayableAnimationDrawable animation = new ReplayableAnimationDrawable(mAnimations[animationNumber]) {
            @Override
            public void onAnimationFinish() {
                super.onAnimationFinish();
                int finalDrawable = animationNumber == 0 ? R.drawable.ic_play_pause_8 : R.drawable.ic_play_pause_0;
                playPauseButton.setImageDrawable(getResources().getDrawable(finalDrawable));
            }
        };
        playPauseButton.setImageDrawable(animation);
        animation.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
    }


}
