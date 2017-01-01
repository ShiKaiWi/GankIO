package com.example.xkwei.gankio.models;

/**
 * Created by xkwei on 30/12/2016.
 */

public class Article {
    private String mId;
    private String mDescription;
    private String mImageUrl;
    private String mUrl;
    private String mCreateDate;
    private String mPublishDate;
    private String mAuthor;
    private String mType;

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
