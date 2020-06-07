package com.duke.bookmark.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.duke.bookmark.FirebaseFragment;
import com.duke.bookmark.R;
import com.duke.bookmark.Utilities;
import com.duke.bookmark.activities.BaseActivity;
import com.duke.bookmark.activities.PrivateTagDetailActivity;
import com.duke.bookmark.activities.PublicTagDetailActivity;
import com.duke.bookmark.models.User;
import com.duke.bookmark.views.CollectionItem;
import com.duke.bookmark.views.CollectionPicker;
import com.duke.bookmark.views.OnCollectionItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by Matteo on 13/07/2015.
 */
public class TagsFragment extends DialogFragment {

    public static final String EXTRA_FROM_PUBLIC = "EXTRA_FROM_PUBLIC";

    private Firebase mFirebase;
    private User mUser;

    private Context mContext;
    private BaseActivity mActivity;

    private boolean mFromPublic;

    private List<CollectionItem> mItemsList = new ArrayList<>();

    @BindView(R.id.collection_picker) CollectionPicker mCollectionPicker;

    @BindString(R.string.sb_no_tags) String mStringNoTags;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = (BaseActivity) mContext;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        super.onCreate(savedInstanceState);

        setupFirebase();

        handleArguments();
    }

    private void setupFirebase() {
        FirebaseFragment firebaseFragment;
        firebaseFragment = Utilities.setupFirebase(mActivity.getSupportFragmentManager());
        mFirebase = firebaseFragment.getFirebase();
        mUser = firebaseFragment.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show_tags, container, false);
        ButterKnife.bind(this, view);
        setupCollectionPicker();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        return view;
    }

    private void handleArguments() {
        Bundle bundle = this.getArguments();
        mFromPublic = bundle != null && bundle.getBoolean(EXTRA_FROM_PUBLIC, false);

        if (mFromPublic) {
            mFirebase.child(FirebaseFragment.PATH_PUBLIC_TAGS).orderByValue()
                    .addListenerForSingleValueEvent(tagsListener);
        } else {
            mFirebase.child(FirebaseFragment.PATH_PRIVATE_TAGS).child(mUser.id)
                    .addListenerForSingleValueEvent(tagsListener);
        }
    }

    ValueEventListener tagsListener = (new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            if (!snapshot.exists()) {
                mActivity.showSnackbar(mStringNoTags);
                dismiss();
            }
            for (DataSnapshot child : snapshot.getChildren()) {
                String tagName;
                if (mFromPublic) {
                    tagName = child.getKey() + " (" + child.getValue() + ")";
                } else {
                    tagName = child.getKey();
                }
                mItemsList.add(new CollectionItem(child.getKey(), tagName));
            }
            if (mCollectionPicker != null) {
                setupCollectionPicker();
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
        }
    });

    private void setupCollectionPicker() {
        mCollectionPicker.setItems(mItemsList);
        mCollectionPicker.drawItemView();
        mCollectionPicker.setOnItemClickListener(new OnCollectionItemClickListener() {
            @Override
            public void onClick(CollectionItem collectionItem, int position) {
                startTagDetailActivity(collectionItem.id);
                dismiss();
            }
        });
    }

    private void startTagDetailActivity(String tag) {
        if (mFromPublic) {
            Intent intentWithToken = new Intent(mContext, PublicTagDetailActivity.class);
            intentWithToken.putExtra(PublicTagDetailActivity.INTENT_TAG_NAME, tag);
            startActivity(intentWithToken);
        } else {
            Intent intentWithToken = new Intent(mContext, PrivateTagDetailActivity.class);
            intentWithToken.putExtra(PrivateTagDetailActivity.INTENT_TAG_NAME, tag);
            startActivity(intentWithToken);
        }
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }
}