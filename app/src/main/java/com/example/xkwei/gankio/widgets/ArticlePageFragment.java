package com.example.xkwei.gankio.widgets;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.xkwei.gankio.R;

/**
 * Created by xkwei on 03/01/2017.
 */

public class ArticlePageFragment extends Fragment {
    private static final String ARG_URI="Article_Page_Fragment_Article_Uri";
    private Uri mUri;
    private WebView mWebView;
    public static ArticlePageFragment newInstance(Uri uri){
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI,uri);
        ArticlePageFragment apf = new ArticlePageFragment();
        apf.setArguments(args);
        return apf;
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(LayoutInflater lif, ViewGroup vg,Bundle savedInstanceState){
        View v = lif.inflate(R.layout.fragment_article_page,vg,false);
        mWebView = (WebView) v.findViewById(R.id.article_page_fragment_webview);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView wv,String url){
                String protocol = url.split(":")[0];
                if(protocol.equals("https")||protocol.equals("http"))
                    return false;
                else{
                    Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    if(i.resolveActivity(getActivity().getPackageManager())!=null) {
                        startActivity(i);
                        return true;
                    }
                    return false;
                }
            }
        });
        mWebView.loadUrl(mUri.toString());
        return v;
    }

    public WebView getWebView() {
        return mWebView;
    }
}
