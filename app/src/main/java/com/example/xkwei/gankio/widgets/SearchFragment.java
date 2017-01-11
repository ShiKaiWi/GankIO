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
import android.support.v7.widget.SearchView;
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
 * Created by xkwei on 11/01/2017.
 */

public class SearchFragment extends Fragment {
    private static final int REFRESHING= 0 ;
    private static final int LOADING_MORE= 1;

    private static final String TAG = "SearchFragment";
    private static final String QUERY = "SearchFragmentQuery";
    private SearchFragment.UpdateReceiver mUpdateReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;
    private Realm mRealm;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private boolean mIsRefreshing;
    private boolean mIsLoadingMore;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mPageNumber;
    private Toolbar mToolbar;
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
        updateRecyclerView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mQuery = getArguments().getString(QUERY);
        mIsLoadingMore = mIsRefreshing = false;
        mRealm = Realm.getDefaultInstance();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
        mUpdateReceiver = new SearchFragment.UpdateReceiver();
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,new IntentFilter(GankIODataService.ACTION_QUERY));
        mPageNumber = 1;
        if(mQuery.length()>0)
            fetchingData(REFRESHING);
        mToolbar = ((MainActivity)getActivity()).getToolbar();
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
                        hideOrShowToolbar(!isToolBarVisible);
                        isToolBarVisible = !isToolBarVisible;
                    }
                    deltaY = 0;
                }
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
        mAdapter = new SearchFragment.ArticleRecyclerViewAdapter(getActivity(), mRealm.where(Article.class).findAllSorted("mDate", Sort.DESCENDING));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        return v;
    }

    private void fetchingData(int requestCode){
        if(isFetching())return;
        Intent i = null;
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
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(GankIODataService.ACTION_QUERY);
        mLocalBroadcastManager.registerReceiver(mUpdateReceiver,intentFilter);
        mToolbar = ((MainActivity)getActivity()).getToolbar();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRealm.close();
    }


    private boolean isFetching(){
        return mIsRefreshing||mIsLoadingMore;
    }

    private void setIsFetching(boolean isFetching){
        mIsLoadingMore = isFetching;
        mIsRefreshing = isFetching;
    }

    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Log.i(TAG,"got the broadcast");
            String action = intent.getAction();
            if(action==GankIODataService.ACTION_QUERY){
                RealmResults<Article> realmResults = mRealm.where(Article.class).contains("mTags",mQuery).findAllSorted("mDate",Sort.DESCENDING);
                Log.i(TAG,"got " + realmResults.size() + " searching articles");
                updateRecyclerView();
                setIsFetching(false);
            }
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

    private class ArticleRecyclerViewAdapter extends RealmRecyclerViewAdapter<Article,SearchFragment.ArticleHolder> {
        @Override
        public SearchFragment.ArticleHolder onCreateViewHolder(ViewGroup vg, int viewType){
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.article_fragment_main_recyclerview_item,vg,false);
            return new SearchFragment.ArticleHolder(v);
        }

        @Override
        public void onBindViewHolder(SearchFragment.ArticleHolder ahd, int position){
            Article article = getData().get(position);
            ahd.bindArticleItem(article);
        }

        public ArticleRecyclerViewAdapter(Context context, OrderedRealmCollection<Article> orc){
            super(context,orc,true);
        }
    }

    private void updateRecyclerView() {
        RealmResults<Article> items =
                mRealm.where(Article.class).contains("mTags",mQuery).findAllSorted("mDate", Sort.DESCENDING);
        ((SearchFragment.ArticleRecyclerViewAdapter)mAdapter).updateData(items);
    }

    private void hideOrShowToolbar(boolean shouldBeVisible){
        int deltaY = shouldBeVisible?0:-mToolbar.getHeight();
        mToolbar.animate().translationY(deltaY).setInterpolator(new AccelerateInterpolator(2));
    }
}
