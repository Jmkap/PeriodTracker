package com.thesis.periodtracker;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReceivedMessageViewHolder extends CustomViewHolder{

    private TextView tvMessage;
    private TextView tvDateTime;

    public ReceivedMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.tvMessage = itemView.findViewById(R.id.textRcvMessage);
        this.tvDateTime = itemView.findViewById(R.id.textRcvDateTime);
    }

    @Override
    public void bindData(MessageModel message) {
        this.tvMessage.setText(message.getMessage());
        this.tvDateTime.setText(message.getTimestamp());
    }

    public void setTvDateTime(String tvDateTime) {
        this.tvDateTime.setText(tvDateTime);
    }
    public void setTvMessage(String tvMessage) {
        this.tvMessage.setText(tvMessage);
    }
}
