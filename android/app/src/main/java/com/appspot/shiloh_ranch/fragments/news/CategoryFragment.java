package com.appspot.shiloh_ranch.fragments.news;


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
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {


    private static final String CATEGORY_KEY = "CATEGORY_KEY";
    private Database mDatabase;
    private List<Post> mPosts;
    private String mCategoryKey;
    private CardListView mPostList;
    private View mEmptyView;

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
        Bundle args = getArguments();
        mCategoryKey = args.getString(CATEGORY_KEY);
        mDatabase = Database.getDatabase(getActivity());
        if (mCategoryKey == null) {
            mPosts = mDatabase.getAllPosts();
        } else {
            mPosts = mDatabase.getAllPosts(mCategoryKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        mPostList = (CardListView) rootView.findViewById(R.id.list_posts);
        mEmptyView = rootView.findViewById(R.id.empty);
        mPostList.setVisibility(mPosts.isEmpty() ? View.GONE : View.VISIBLE);
        if (!mPosts.isEmpty()) {
            mEmptyView.setVisibility(View.GONE);
            ArrayList<Card> cards = getPostsAsCards();
            mPostList.setAdapter(new CardArrayAdapter(getActivity(),cards));
        }
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
