package com.example.xkwei.gankio.widgets;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xkwei.gankio.ArticlePageActivity;
import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.services.GankIODataService;
import com.example.xkwei.gankio.utils.Constants;
import com.example.xkwei.gankio.utils.DateUtils;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by xkwei on 01/01/2017.
 */

public class MainFragment extends Fragment {
    private static final int REFRESHING= 0 ;
    private static final int LOADING_MORE= 1;

    private static final String TAG = "MainFragment";
    private UpdateReceiver mUpdateReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Realm mRealm;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mPageNumber;

    public static final String CATEGORY="MianFragment_Category_Index";
    public static Fragment getInstance(){
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mIsLoadingMore = mIsRefreshing = false;
        mRealm = Realm.getDefaultInstance();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mUpdateReceiver = new UpdateReceiver();
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,new IntentFilter(GankIODataService.ACTION_UPDATE_DATA));
        mPageNumber = 1;
        fetchingData(REFRESHING);
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup container, Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_main,container,false);
        mRecyclerView = (RecyclerView)v.findViewById(R.id.fragment_main_recycler_view);
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

        mLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new ArticleRecyclerViewAdapter(getActivity(), mRealm.where(Article.class).findAllSorted("mDate", Sort.DESCENDING));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    private void fetchingData(int requestCode){
        if(isFetching())return;
        Intent i = null;
        if(requestCode==REFRESHING){
            mIsRefreshing = true;
            i = GankIODataService.newIntentWithType(getActivity(), Constants.ANDROID,1);
        }
        else if(requestCode==LOADING_MORE){
            mIsLoadingMore = true;
            i = GankIODataService.newIntentWithType(getActivity(), Constants.ANDROID,++mPageNumber);
        }
        if(null!=i)
            getActivity().startService(i);
    }

    private int getFirstVisiblePosition(){
        LinearLayoutManager llm = (LinearLayoutManager) mLayoutManager;
        return llm.findFirstVisibleItemPosition();
    }
    private int getLastVisiblePosition(){
        LinearLayoutManager llm = (LinearLayoutManager) mLayoutManager;
        return llm.findLastVisibleItemPosition();
    }
    @Override
    public void onPause(){
        super.onPause();
        mLocalBroadcastManager.unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,new IntentFilter(GankIODataService.ACTION_UPDATE_DATA));
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }


    private boolean isFetching(){
        return mIsRefreshing||mIsLoadingMore;
    }
    private void setIsFetching(boolean isFetcing){
        mIsLoadingMore = isFetcing;
        mIsRefreshing = isFetcing;
    }
    private class UpdateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            Log.i(TAG,"got the broadcast");
            RealmResults<Article> realmResults = mRealm.where(Article.class).findAll();
            Log.i(TAG,"got "+realmResults.size()+" articles");
            updateRecyclerView();
            setIsFetching(false);
        }
    }

    private class ArticleHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mAuthor,mDate,mDescription;
        Article mArticle;

        public ArticleHolder(View v){
            super(v);
            mAuthor = (TextView) v.findViewById(R.id.article_fragment_main_recyclerview_item_author);
            mDate = (TextView) v.findViewById(R.id.article_fragment_main_recyclerview_item_date);
            mDescription = (TextView) v.findViewById(R.id.article_fragment_main_recyclerview_item_description);
            v.setOnClickListener(this);
        }

        public void bindArticleItem(Article article){
            mArticle = article;
            mAuthor.setText(article.getAuthor());
            mDate.setText(DateUtils.dateToString(article.getDate()));
            mDescription.setText(article.getDescription());
        }

        @Override
        public void onClick(View v){
            Intent i = ArticlePageActivity.newIntent(getActivity(), Uri.parse(mArticle.getUrl()));
            startActivity(i);
        }
    }

    private class ArticleRecyclerViewAdapter extends RealmRecyclerViewAdapter<Article,ArticleHolder>{
        @Override
        public ArticleHolder onCreateViewHolder(ViewGroup vg,int viewType){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.article_fragment_main_recyclerview_item,vg,false);
            return new ArticleHolder(v);
        }

        @Override
        public void onBindViewHolder(ArticleHolder ahd,int position){
            Article article = getData().get(position);
            ahd.bindArticleItem(article);
        }

        public ArticleRecyclerViewAdapter(Context context, OrderedRealmCollection<Article> orc){
            super(context,orc,true);
        }
    }

    private void updateRecyclerView() {
        ((ArticleRecyclerViewAdapter)mAdapter).updateData(mRealm.where(Article.class).findAllSorted("mDate", Sort.DESCENDING));
    }

}
