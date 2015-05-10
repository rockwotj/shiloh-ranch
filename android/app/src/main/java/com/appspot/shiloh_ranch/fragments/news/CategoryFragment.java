package com.appspot.shiloh_ranch.fragments.news;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appspot.shiloh_ranch.R;
import com.appspot.shiloh_ranch.api.model.Post;
import com.appspot.shiloh_ranch.database.Database;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {


    private static final String CATEGORY_KEY = "CATEGORY_KEY";
    private List<Post> mPosts;

    public CategoryFragment() {
        // Required empty public constructor
    }

    public static CategoryFragment createFragment(String categoryKey) {
        Bundle args = new Bundle();
        args.putString(CATEGORY_KEY, categoryKey);
        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        final CardListView postList = (CardListView) rootView.findViewById(R.id.list_posts);
        final View loadingSpinner = rootView.findViewById(R.id.loading_spinner);
        final View emptyView = rootView.findViewById(R.id.empty);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                Bundle args = getArguments();
                String categoryKey = args.getString(CATEGORY_KEY);
                Database database = Database.getDatabase(getActivity());
                if (categoryKey == null) {
                    mPosts = database.getAllPosts();
                } else {
                    mPosts = database.getAllPosts(categoryKey);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadingSpinner.setVisibility(View.GONE);
                postList.setVisibility(mPosts.isEmpty() ? View.GONE : View.VISIBLE);
                if (!mPosts.isEmpty()) {
                    emptyView.setVisibility(View.GONE);
                    ArrayList<Card> cards = getPostsAsCards();
                    postList.setAdapter(new CardArrayAdapter(getActivity(), cards));
                } else {
                    emptyView.setVisibility(View.VISIBLE);
                }
            }
        }.execute();
        return rootView;
    }

    private ArrayList<Card> getPostsAsCards() {
        ArrayList<Card> cards = new ArrayList<>();
        for (Post p : mPosts) {
            cards.add(bindPostToCard(p));
        }
        return cards;
    }

    private Card bindPostToCard(Post post) {
        Card card = new Card(getActivity());
        NewsCardHeader header = new NewsCardHeader(getActivity(), post);
        header.setTitle(post.getTitle());
        header.setButtonExpandVisible(true);
        card.addCardHeader(header);
        card.setOnClickListener(new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                card.doToogleExpand();
            }
        });
        card.addCardExpand(new NewsCardBody(getActivity(), post.getContent()));
        card.setSwipeable(false);
        return card;
    }


}
