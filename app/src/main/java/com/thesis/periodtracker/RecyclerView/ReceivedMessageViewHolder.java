package com.thesis.periodtracker.RecyclerView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;

import com.thesis.periodtracker.R;

public class ReceivedMessageViewHolder extends CustomViewHolder{

    private TextView tvMessage;
    private ImageView ivImage;
    private TextView tvDateTime;

    public ReceivedMessageViewHolder(@NonNull View itemView) {
        super(itemView);
        this.tvMessage = itemView.findViewById(R.id.textRcvMessage);
        this.tvDateTime = itemView.findViewById(R.id.textRcvDateTime);
        this.ivImage = itemView.findViewById(R.id.messageImage);
    }

    @Override
    public void bindData(MessageModel message) {
        this.tvMessage.setText(message.getMessage());
        this.tvDateTime.setText(message.getTimestamp());
        Glide.with(ivImage.getContext())
                .load(message.getImageUrl())
                .into(ivImage);
        if (message.getImageUrl() != null && !message.getImageUrl().isEmpty()){
            this.tvMessage.setVisibility(View.GONE);
            this.ivImage.setVisibility(View.VISIBLE);
        } else {
            this.tvMessage.setVisibility(View.VISIBLE);
            this.ivImage.setVisibility(View.INVISIBLE);
        }
    }

    public void setTvDateTime(String tvDateTime) {
        this.tvDateTime.setText(tvDateTime);
    }
    public void setTvMessage(String tvMessage) {
        this.tvMessage.setText(tvMessage);
    }
}
