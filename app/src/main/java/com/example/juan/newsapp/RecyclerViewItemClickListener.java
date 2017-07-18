package com.example.juan.newsapp;

import android.view.View;

interface RecyclerViewItemClickListener {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}