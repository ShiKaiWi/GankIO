package com.example.xkwei.gankio.bases;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.example.xkwei.gankio.MainActivity;
import com.example.xkwei.gankio.R;

import io.realm.Realm;

/**
 * Created by xkwei on 02/01/2017.
 */

public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.Adapter mAdapter;
    protected Toolbar mToolbar;
    protected SwipeRefreshLayout mSwipeRefreshLayout;

    protected Realm mRealm;
    protected int mFragmentLayout;
    protected boolean mIsRefreshing;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mToolbar = ((MainActivity)getActivity()).getToolbar();
        mRealm = Realm.getDefaultInstance();
        mIsRefreshing = false;
        setFragmentLayout();
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = lif.inflate(mFragmentLayout,container,false);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.fragment_main_recycler_view);
        setRecyclerViewAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
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
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
//        mSwipeRefreshLayout.setProgressViewEndTarget(true,((AppCompatActivity)getActivity()).getSupportActionBar().getHeight()+mSwipeRefreshLayout.getProgressCircleDiameter());
        mSwipeRefreshLayout.setProgressViewEndTarget(true,150+mSwipeRefreshLayout.getProgressCircleDiameter());

        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        updateRecyclerView();
        mToolbar = ((MainActivity)getActivity()).getToolbar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }

    protected int getFirstVisiblePosition(){
        LinearLayoutManager llm = (LinearLayoutManager) mLayoutManager;
        return llm.findFirstVisibleItemPosition();
    }
    protected int getLastVisiblePosition(){
        LinearLayoutManager llm = (LinearLayoutManager) mLayoutManager;
        return llm.findLastVisibleItemPosition();
    }


    protected void toggleToolbar(boolean shouldBeVisible){
        int deltaY = shouldBeVisible?0:-mToolbar.getHeight();
        mToolbar.animate().translationY(deltaY).setInterpolator(new AccelerateInterpolator(2));
    }


    public void refresh(){
        updateRecyclerView();
    }
    protected abstract void setFragmentLayout();
    protected abstract void setRecyclerViewAdapter();
    protected abstract void updateRecyclerView();

}

