package com.example.xkwei.gankio.widgets;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xkwei.gankio.ArticlePageActivity;
import com.example.xkwei.gankio.R;
import com.example.xkwei.gankio.models.Article;
import com.example.xkwei.gankio.utils.DateUtils;

import io.realm.Realm;

/**
 * Created by xkwei on 14/01/2017.
 */


public class ArticleHolder extends RecyclerView.ViewHolder{

    private final static String TAG="ArticleHolder";
    final TextView mAuthor,mDate,mDescription;
    final ImageView mLikeIcon;
    private Article mArticle;
    private Context mContext;

    public ArticleHolder(View v, Context context){
        super(v);
        mContext = context;
        mAuthor = (TextView) v.findViewById(R.id.article_fragment_main_recycler_view_item_author);
        mDate = (TextView) v.findViewById(R.id.article_fragment_main_recycler_view_item_date);
        mDescription = (TextView) v.findViewById(R.id.article_fragment_main_recycler_view_item_description);
        mLikeIcon = (ImageView) v.findViewById(R.id.article_fragment_main_recycler_view_item_like);

    }

    public void bindArticleItem(Article article){
        mArticle = article;
        mAuthor.setText(article.getAuthor());
        mDate.setText(DateUtils.dateToString(article.getDate()));
        mDescription.setText(article.getDescription());

        if(mArticle.isLiked()){
            mLikeIcon.setImageResource(R.drawable.like_filled);
        }
        else{
            mLikeIcon.setImageResource(R.drawable.like);
        }

        mDescription.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = ArticlePageActivity.newIntent(mContext, Uri.parse(mArticle.getUrl()));
                mContext.startActivity(i);
            }
        });
        mLikeIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mArticle.isLiked()){
                    mLikeIcon.setImageResource(R.drawable.like);
                }
                else{
                    mLikeIcon.setImageResource(R.drawable.like_filled);
                }
                final String id = mArticle.getId();
                Realm.getDefaultInstance().executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm bgRealm) {
                        Article article = bgRealm.where(Article.class).equalTo("mId",id).findFirst();
                        article.setLiked(!article.isLiked());
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        // Transaction was a success.
                        Log.i(TAG,"article is liked:"+mArticle.isLiked());
                    }
                }, new Realm.Transaction.OnError() {
                    @Override
                    public void onError(Throwable error) {
                        // Transaction failed and was automatically canceled.
                        Log.i(TAG,"article is liked:"+mArticle.isLiked()+" because of error:"+error);
                    }
                });
            }
        });


    }
}
