package com.example.xkwei.gankio.bases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xkwei on 15/01/2017.
 */

public abstract class BaseFragmentWithUpdater extends BaseFragment {
    protected BroadcastReceiver mUpdateReceiver;
    protected LocalBroadcastManager mLocalBroadcastManager;
    protected int mPageNumber;
    protected IntentFilter mIntentFilter;
    protected boolean mIsLoadingMore;
    protected static final int REFRESHING=0x100;
    protected static final int LOADING_MORE=0x101;
    private static final String TAG = "BaseFragmentWithUpdater";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG,"got the broadcast");
                handleReceivedBroadCast(intent);
                mSwipeRefreshLayout.setRefreshing(false);
                mIsRefreshing = true;
            }
        };
        setIntentFilter();
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = super.onCreateView(lif,container,savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isFetching()){
                    mSwipeRefreshLayout.setRefreshing(false);
                    return;
                }
                fetchingData(REFRESHING);
            }
        });
        return v;
    }


    protected void setIsFetching(boolean isFetching){
        mIsLoadingMore = isFetching;
        mIsRefreshing = isFetching;
    }
    @Override
    public void onPause(){
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,mIntentFilter);
    }

    protected abstract void fetchingData(int requestCode);

    protected boolean isFetching(){
        return mIsRefreshing||mIsLoadingMore;
    }


    protected abstract void handleReceivedBroadCast(Intent intent);
    protected abstract void setIntentFilter();
}
