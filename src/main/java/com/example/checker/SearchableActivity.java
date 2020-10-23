package com.example.checker;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ArrayAdapter;

import java.lang.String;

import java.util.ArrayList;

public class SearchableActivity extends ListActivity {
    private static ArrayList<String> queryResults = new ArrayList<String>(){};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        handleIntent(getIntent());


        //create an ArrayAdapter to bind queryResults data to the ListView element in this Activity
        ArrayAdapter<String> listDataAdapter = new ArrayAdapter<String>(this, R.layout.search_result_layout, R.id.searchResultTextView, queryResults);
        this.setListAdapter(listDataAdapter);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            handleQuery(query);
        }
    }

    private void handleQuery(String query){
        //first, reset the queryResults ArrayList
        queryResults.clear();
        // Search results should not be case sensitive so the comparison itself is done after toLowerCase is applied to each term
        query = query.toLowerCase();
        for (Pair p:MainActivity.allVOC) {  //uses String.contains method to search all vocabulary for the query text
            String pData = (String) p.second;
            String firstString=pData.split("#")[MainActivity.swapLanguages ?0:1];
            String secondString=pData.split("#")[MainActivity.swapLanguages ?1:0];
            //if an entry in the allVOC database contains the search query, add it to the queryResults ArrayList
            if(firstString.toLowerCase().contains(query) || secondString.toLowerCase().contains(query)){
                queryResults.add(firstString + " - " + secondString);
            }
        }
    }

}

