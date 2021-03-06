package com.example.xkwei.gankio.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by xkwei on 30/12/2016.
 */

public class Article extends RealmObject{
    @PrimaryKey
    private String mId;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    @Ignore
    private String mCreateDate;
    @Ignore
    private String mPublishDate;

    private Date mDate;
    private String mAuthor;
    private String mType;
    @Ignore
    private String mTitle;
    private String mTags;
    private boolean mIsLiked;

    public boolean isLiked() {
        return mIsLiked;
    }

    public void setLiked(boolean liked) {
        mIsLiked = liked;
    }

    public Article(){}
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(){
        String[] temp = mUrl.split("/");
        mTitle = temp[temp.length-1];
    }
    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getTags() {
        return mTags;
    }

    public void addTag(String tag){
        if(null!= mTags)
            mTags += tag;
        else
            mTags = tag;
    }
    public Article(String id) {
        mId = id;
    }

    public String getId() {
        return mId;
    }

    public String getDescription() {

        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(String createDate) {
        mCreateDate = createDate;
    }

    public String getPublishDate() {
        return mPublishDate;
    }

    public void setPublishDate(String publishDate) {
        mPublishDate = publishDate;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }
}
