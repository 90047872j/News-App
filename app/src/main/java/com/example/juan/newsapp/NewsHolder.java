package com.example.juan.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.widget.TextView;

class NewsHolder extends ViewHolder {

    private final TextView tvSection;
    private final TextView tvPDate;
    private final TextView tvAuthor;
    private final TextView tvTitle;
    private final TextView tvInfo;

    public NewsHolder(View newsView) {
        super(newsView);
        tvSection = (TextView) newsView.findViewById(R.id.section_textView);
        tvAuthor = (TextView) newsView.findViewById(R.id.author_textView);
        tvTitle = (TextView) newsView.findViewById(R.id.title_textView);
        tvInfo = (TextView) newsView.findViewById(R.id.information_textView);
        tvPDate = (TextView) newsView.findViewById(R.id.pDate_textView);
    }

    public void bindNewsInformation(News newsContainer, String author, String informationText, Context context, String formattedDate) {
        tvSection.setText(newsContainer.getSection());
        tvAuthor.setText(context.getString(R.string.written_by) + author);
        tvTitle.setText(newsContainer.getTitle());
        tvInfo.setText(informationText);
        tvPDate.setText(formattedDate);
    }
}
