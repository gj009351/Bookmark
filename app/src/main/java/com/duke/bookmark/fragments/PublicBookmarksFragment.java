package com.duke.bookmark.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.duke.bookmark.FirebaseFragment;
import com.duke.bookmark.R;
import com.duke.bookmark.Utilities;
import com.duke.bookmark.activities.MainActivity;
import com.duke.bookmark.adapters.PublicAdapter;
import com.duke.bookmark.models.PublicBookmark;
import com.duke.bookmark.models.User;
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator;

/**
 * Created by Matteo on 03/08/2015.
 */
public class PublicBookmarksFragment extends Fragment {

    public static final String EXTRA_TAG_OF_BOOKMARKS = "EXTRA_TAG_OF_BOOKMARKS";

    public static final String SAVED_BOOKMARKS_LIST = "SAVED_BOOKMARKS_LIST";
    public static final String SAVED_BOOKMARKS_MAP = "SAVED_BOOKMARKS_MAP";

    private Firebase mFirebase;
    private Firebase mFirebaseListenerRef;
    private User mUser;
    private PublicAdapter mAdapter;

    private Context mContext;
    private AppCompatActivity mActivity;

    private String mTagOfBookmark;

    private boolean mIsViewVisible;

    private List<PublicBookmark> mBookmarksList;
    private Map<String, PublicBookmark> mBookmarksMap;

    @BindView(R.id.recyclerview_public_bookmarks) RecyclerView mRecyclerView;
    @BindView(R.id.scrollview_no_connection) ScrollView mScrollViewNoConnection;
    @BindView(R.id.srl)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = (AppCompatActivity) mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFirebase();
        handleSavedInstanceState(savedInstanceState);
    }

    private void setupFirebase() {
        FirebaseFragment firebaseFragment;
        firebaseFragment = Utilities.setupFirebase(mActivity.getSupportFragmentManager());
        mFirebase = firebaseFragment.getFirebase();
        mUser = firebaseFragment.getUser();
    }

    private void handleSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null
                && savedInstanceState.containsKey(SAVED_BOOKMARKS_LIST)
                && savedInstanceState.containsKey(SAVED_BOOKMARKS_MAP)) {
            mBookmarksList = Parcels.unwrap(savedInstanceState.getParcelable(SAVED_BOOKMARKS_LIST));
            mBookmarksMap = Parcels.unwrap(savedInstanceState.getParcelable(SAVED_BOOKMARKS_MAP));
            mAdapter = new PublicAdapter(mBookmarksList, mContext, MainActivity.TAG);
            mAdapter.onRestoreSavedInstance(savedInstanceState);
        } else {
            mBookmarksList = new ArrayList<PublicBookmark>();
            mBookmarksMap = new HashMap<String, PublicBookmark>();
            mAdapter = new PublicAdapter(mBookmarksList, mContext, MainActivity.TAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_bookmarks, container, false);
        ButterKnife.bind(this, view);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                handleViewCheckingConnection();
            }
        });

        handleViewCheckingConnection();

        return view;
    }

    private void handleViewCheckingConnection() {
        if (Utilities.isConnected(mContext)) {
            if (!mIsViewVisible) {
                showRecyclerView();
            }
        } else {
            if (mIsViewVisible) {
                hideRecyclerView();
            }
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void showRecyclerView() {
        setupRecyclerView();
        handleArguments();
        mScrollViewNoConnection.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mSwipeRefreshLayout.setEnabled(false);
        mIsViewVisible = true;
    }

    public void hideRecyclerView() {
        try {
            mFirebase.removeEventListener(publicBookmarkListener);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        initializeAdapter();
        setupRecyclerView();
        mScrollViewNoConnection.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mSwipeRefreshLayout.setEnabled(true);
        mIsViewVisible = false;
    }

    private void initializeAdapter() {
        mBookmarksMap.clear();
        mBookmarksList.clear();
        mAdapter = new PublicAdapter(mBookmarksList, mContext, MainActivity.TAG);
    }

    private void setupRecyclerView() {
        mRecyclerView.clearOnScrollListeners();
        if (!Utilities.isTablet(mContext)) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        } else {
            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        }
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new FadeInDownAnimator());
    }

    private void handleArguments() {
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(EXTRA_TAG_OF_BOOKMARKS)) {
            mTagOfBookmark = bundle.getString(EXTRA_TAG_OF_BOOKMARKS);
            mFirebaseListenerRef = mFirebase
                    .child(FirebaseFragment.PATH_PUBLIC_TAGS)
                    .child(mTagOfBookmark);
            mFirebaseListenerRef.addChildEventListener(publicBookmarkListener);
        } else {
            String order = Utilities.getListOrder(mContext);
            mFirebaseListenerRef = mFirebase.child(FirebaseFragment.PATH_PUBLIC_BOOKMARKS);
            mFirebaseListenerRef.orderByChild(order).addChildEventListener(publicBookmarkListener);
        }
    }

    ChildEventListener publicBookmarkListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
            String key = dataSnapshot.getKey();
            Object value = dataSnapshot.getValue();

            if (!mBookmarksMap.containsKey(key)) {
                PublicBookmark publicBookmark = new PublicBookmark(dataSnapshot);
                mBookmarksMap.put(key, publicBookmark);
                mBookmarksList.add(0, publicBookmark);
                mAdapter.notifyItemInserted(0);
                mAdapter.refreshExpandedCard();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            Object value = dataSnapshot.getValue();

            if (mBookmarksMap.containsKey(key)) {
                PublicBookmark oldPublicBookmark = mBookmarksMap.get(key);
                PublicBookmark newPublicBookmark = new PublicBookmark(dataSnapshot);
                int position = mBookmarksList.indexOf(oldPublicBookmark);
                mBookmarksMap.put(key, newPublicBookmark);
                mBookmarksList.set(position, newPublicBookmark);
                mAdapter.notifyItemChanged(position);
                mAdapter.updateExpandedBookmark();
                mAdapter.refreshExpandedCard();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            Object value = dataSnapshot.getValue();

            if (mBookmarksMap.containsKey(key)) {
                PublicBookmark publicBookmark = mBookmarksMap.get(key);
                int position = mBookmarksList.indexOf(publicBookmark);
                mBookmarksMap.remove(key);
                mBookmarksList.remove(publicBookmark);
                mAdapter.notifyItemRemoved(position);
                mAdapter.refreshExpandedCard();
            }
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        handleViewCheckingConnection();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_BOOKMARKS_LIST, Parcels.wrap(mBookmarksList));
        outState.putParcelable(SAVED_BOOKMARKS_MAP, Parcels.wrap(mBookmarksMap));
        mAdapter.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFirebaseListenerRef.removeEventListener(publicBookmarkListener);
    }
}
