package com.example.juan.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class NewsAdapter extends RecyclerView.Adapter<NewsHolder> {
    private static final String LOG_TAG = NewsAdapter.class.getName();
    private final Context mContext;
    private List<News> mNewsList = new ArrayList<>();

    public NewsAdapter(Context context, List<News> newsList, View parentView) {
        this.mContext = context;
        this.mNewsList = newsList;
    }

    @Override
    public NewsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.news_list_item, parent, false);
        return new NewsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsHolder holder, int position) {
        News currentNews = mNewsList.get(position);
        currentNews.setInformationText(currentNews.getInformationText());
        currentNews.setPublishedDate(dateFormatter(currentNews.getPublishedDate()));
        holder.bindNewsInformation(currentNews, currentNews.getAuthor(), informationTextFormatter(currentNews.getInformationText().trim()), mContext, currentNews.getPublishedDate());
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public News getItem(int position) {
        return mNewsList.get(position);
    }

    public void clear() {
        mNewsList.clear();
    }

    public void addAll(List<News> newsData) {
        if (newsData != null) {
            clear();
            mNewsList.addAll(newsData);
            notifyDataSetChanged();
        }
    }

    @SuppressWarnings("deprecation")
    private String informationTextFormatter(String informationText) {
        StringBuilder finalText = new StringBuilder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            finalText.append(Html.fromHtml(informationText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            finalText.append(Html.fromHtml(informationText));
        }
        return finalText.toString();
    }

    private String dateFormatter(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy/MM/dd, HH:mm");
        Date date;
        String formattedDate = time;
        try {
            date = sdf.parse(time);
            formattedDate = output.format(date);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Problem parsing the date", e);
        }
        return formattedDate;
    }
}
