package com.example.xkwei.gankio.widgets;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.example.xkwei.gankio.MainActivity;
import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.services.GankIODataService;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xkwei on 14/01/2017.
 */

public class CollectionFragment extends Fragment {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter<ArticleHolder> mAdapter;
    private Realm mRealm;
    private boolean mIsRefreshing;
    private GestureDetector mDetector;

    private static final String TAG="CollectionFragment";


    public static Fragment getInstance(){
        return new CollectionFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mToolbar = ((MainActivity)getActivity()).getToolbar();
        mRealm = Realm.getDefaultInstance();
    }


    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_main,container,false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_main_recycler_view);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private final int THRESH_HOLD = 12;
            private boolean isToolBarVisible=true;
            private int deltaY=0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isToolBarVisible && dy>0 || !isToolBarVisible && dy<0){
                    deltaY+=dy;
                }
                if(Math.abs(deltaY)>THRESH_HOLD) {
                    if(getFirstVisiblePosition()!=0) {
                        toggleToolbar(!isToolBarVisible);
                        isToolBarVisible = !isToolBarVisible;
                    }
                    deltaY = 0;
                }
                if(!mIsRefreshing){
                    if(dy<0 && getFirstVisiblePosition()==0){
                        fetchingData();
                    }
                }
            }
        });

        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ArticleRecyclerViewAdapter(getActivity(), mRealm.where(Article.class).equalTo("mIsLiked",true).findAllSorted("mDate", Sort.DESCENDING),false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        updateRecyclerView();
        return v;
    }

    private int getFirstVisiblePosition(){
        LinearLayoutManager llm = (LinearLayoutManager) mLayoutManager;
        return llm.findFirstVisibleItemPosition();
    }

    private void fetchingData(){
        if(mIsRefreshing)return;
        toggleToolbar(true);
        mIsRefreshing = true;
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        RealmResults<Article> items =
                mRealm.where(Article.class).equalTo("mIsLiked",true).findAllSorted("mDate", Sort.DESCENDING);
        ((ArticleRecyclerViewAdapter)mAdapter).updateData(items);
    }

    private void toggleToolbar(boolean shouldBeVisible){
        int deltaY = shouldBeVisible?0:-mToolbar.getHeight();
        mToolbar.animate().translationY(deltaY).setInterpolator(new AccelerateInterpolator(2));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }
}
