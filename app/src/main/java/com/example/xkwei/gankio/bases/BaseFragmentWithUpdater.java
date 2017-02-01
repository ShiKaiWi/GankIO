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
import android.widget.Toast;

import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.services.GankIODataService;

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
    private static Toast mToast;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mUpdateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleReceivedBroadCast(intent);
                mSwipeRefreshLayout.setRefreshing(false);
                setIsFetching(false);
            }
        };
        mIntentFilter = new IntentFilter(GankIODataService.CONNECT_ERROR);
        updateIntentFilter();
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = super.onCreateView(lif,container,savedInstanceState);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG,"onRefresh");
                if(isFetching()){
                    mSwipeRefreshLayout.setRefreshing(false);
                    return;
                }
                fetchingData(REFRESHING);
            }
        });
        if(mToast==null)
            mToast = Toast.makeText(getActivity(),R.string.no_connectivity,Toast.LENGTH_LONG);
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


    protected void handleReceivedBroadCast(Intent intent){
        Log.i(TAG,"got the broadcast");
        String result = intent.getAction();
        if(result == GankIODataService.CONNECT_ERROR){
            Log.i(TAG,"cannot connect to the internet");
            if(mToast.getView()!=null){
                if(!mToast.getView().isShown()){
                    mToast.show();
                }
            }else{
                mToast.show();
            }
        }
    };
    protected abstract void updateIntentFilter();
}
