package com.thesis.periodtracker;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public abstract class CustomViewHolder extends RecyclerView.ViewHolder {

    public CustomViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bindData(MessageModel message);
}
