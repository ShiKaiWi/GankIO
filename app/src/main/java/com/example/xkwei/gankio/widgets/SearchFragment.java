package com.example.xkwei.gankio.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.TextView;

import com.example.xkwei.gankio.ArticlePageActivity;
import com.example.xkwei.gankio.MainActivity;
import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.bases.BaseFragmentWithUpdater;
import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.services.GankIODataService;
import com.example.xkwei.gankio.utils.DateUtils;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xkwei on 11/01/2017.
 */

public class SearchFragment extends BaseFragmentWithUpdater {

    private static final String TAG = "SearchFragment";
    private static final String QUERY = "SearchFragmentQuery";
    private String mQuery;

    public static Fragment getInstance(String query){
        Bundle args = new Bundle();
        args.putString(QUERY,query);
        Fragment fg = new SearchFragment();
        fg.setArguments(args);
        return fg;
    }

    public static Fragment getInstance(){
        return getInstance("");
    }

    public String getQuery() {
        return mQuery;
    }

    public void setQuery(String query) {
        mQuery = query;
        fetchingData(REFRESHING);
        updateRecyclerView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mQuery = getArguments().getString(QUERY);
        mIsLoadingMore = mIsRefreshing = false;
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,new IntentFilter(GankIODataService.ACTION_QUERY));
        mPageNumber = 1;
        if(mQuery.length()>0)
            fetchingData(REFRESHING);

    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = super.onCreateView(lif,container,savedInstanceState);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!isFetching()){
                    if(dy<0 && getFirstVisiblePosition()==0){
                        fetchingData(REFRESHING);
                    }
                    else if(dy>0 && getLastVisiblePosition()==mAdapter.getItemCount()-1)
                    {
                        fetchingData(LOADING_MORE);
                    }
                }
            }
        });

        return v;
    }

    @Override
    protected void fetchingData(int requestCode){
        if(isFetching())return;
        Intent i = null;
        toggleToolbar(true);
        if(requestCode==REFRESHING){
            mIsRefreshing = true;
            i = GankIODataService.newIntentForSearch(getActivity(), mQuery);
        }
        else if(requestCode==LOADING_MORE){
            mIsLoadingMore = true;
            i = GankIODataService.newIntentForSearchWithPage(getActivity(), mQuery,++mPageNumber);
        }
        if(null!=i)
            getActivity().startService(i);
    }


    @Override
    protected void handleReceivedBroadCast(Intent intent){
        Log.i(TAG,"got the broadcast");
        String action = intent.getAction();
        if(action==GankIODataService.ACTION_QUERY){
            RealmResults<Article> realmResults = mRealm.where(Article.class).contains("mTags",mQuery).findAllSorted("mDate",Sort.DESCENDING);
            Log.i(TAG,"got " + realmResults.size() + " searching articles");
            updateRecyclerView();
            mSwipeRefreshLayout.setRefreshing(false);
            setIsFetching(false);
        }
    }

    @Override
    protected void updateRecyclerView() {
        RealmResults<Article> items =
                mRealm.where(Article.class).contains("mTags",mQuery).findAllSorted("mDate", Sort.DESCENDING);
        ((ArticleRecyclerViewAdapter)mAdapter).updateData(items);
    }

    @Override
    protected void setIntentFilter(){
        mIntentFilter = new IntentFilter(GankIODataService.ACTION_QUERY);
    }

    @Override
    protected void setFragmentLayout(){
        mFragmentLayout = R.layout.fragment_main;
    }

    @Override
    protected void setRecyclerViewAdapter(){
        mAdapter = new ArticleRecyclerViewAdapter(getActivity(),
                mRealm.where(Article.class).contains("mTags",mQuery).findAllSorted("mDate",Sort.DESCENDING),false);
    }
}
