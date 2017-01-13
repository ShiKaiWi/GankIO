package com.example.xkwei.gankio.content;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by xkwei on 13/01/2017.
 */

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider{
    public static final String AUTHORITY = "com.example.xkwei.gankio.content.SearchSuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider(){
        setupSuggestions(AUTHORITY,MODE);
    }

}
