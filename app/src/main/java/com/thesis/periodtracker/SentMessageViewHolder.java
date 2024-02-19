package com.thesis.periodtracker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SentMessageViewHolder extends CustomViewHolder {

    private ArrayList<MessageModel> data;
    private TextView tvMessage;
    private TextView tvDateTime;
    private ImageView imgProfile;

    public SentMessageViewHolder(@NonNull View itemView, ArrayList<MessageModel> data, RecyclerView.Adapter adapter) {
        super(itemView);
        this.data = data;
        this.tvMessage = itemView.findViewById(R.id.textSntMessage);
        this.tvDateTime = itemView.findViewById(R.id.textSntDateTime);
    }

    @Override
    public void bindData(MessageModel message) {

    }

    public void setTvDateTime(TextView tvDateTime) {
        this.tvDateTime = tvDateTime;
    }
}
