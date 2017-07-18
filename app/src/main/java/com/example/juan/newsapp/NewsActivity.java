package com.example.juan.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String NEWS_REQUEST_URL = "http://content.guardianapis.com/";
    private static final int NEWS_LOADER_ID = 1;
    private NewsAdapter mAdapter;
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        RecyclerView newsListView = (RecyclerView) findViewById(R.id.news_list);
        mEmptyStateTextView = (TextView) findViewById(R.id.news_empty_view);
        mAdapter = new NewsAdapter(this, new ArrayList<News>(), newsListView);
        LinearLayoutManager LayoutManager = new LinearLayoutManager(this);
        newsListView.setLayoutManager(LayoutManager);
        newsListView.setAdapter(mAdapter);
        newsListView.addOnItemTouchListener(new CustomRVItemTouchListener(getApplicationContext(), newsListView, new RecyclerViewItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                News currentNews = mAdapter.getItem(position);
                if (currentNews != null) {
                    Uri newsUri = Uri.parse(currentNews.getnURL());
                    Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                    startActivity(websiteIntent);
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }

        }));
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_search_by_key)) || key.equals(getString(R.string.settings_number_articles_key)) || key.equals(getString(R.string.settings_order_by_key))) {
            mAdapter.clear();
            mEmptyStateTextView.setVisibility(View.GONE);
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String searched_by = sharedPrefs.getString(getString(R.string.settings_search_by_key), getString(R.string.settings_search_by_default));
        String ordered_by = sharedPrefs.getString(getString(R.string.settings_order_by_key), getString(R.string.settings_order_by_default));
        String page_size = sharedPrefs.getString(getString(R.string.settings_number_articles_key), getString(R.string.settings_number_articles_default));

        // URL help http://open-platform.theguardian.com/explore/
        Uri uri = Uri.parse(NEWS_REQUEST_URL + searched_by);
        Uri.Builder uriBuilder = uri.buildUpon();
        uriBuilder.appendQueryParameter("show-fields","all");
        uriBuilder.appendQueryParameter("page-size", page_size);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("order-by", ordered_by);
        uriBuilder.appendQueryParameter("api-key", "test");
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> newsList) {
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_news_found);
        mAdapter.clear();
        if (newsList != null && !newsList.isEmpty()) {
            mAdapter.addAll(newsList);
            mEmptyStateTextView.setVisibility(View.GONE);
        }

        if (mAdapter.getItemCount() == 0) {
            mEmptyStateTextView.setText(R.string.no_news_found);
            mEmptyStateTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, Settings.class);
            startActivity(settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}







