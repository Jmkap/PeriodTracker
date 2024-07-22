package com.thesis.periodtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 0;

    private ArrayList<MessageModel> messages;

    public MessageAdapter(ArrayList<MessageModel> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        MessageModel message = messages.get(position);
        return message.getType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case VIEW_TYPE_SENT:
                View sentView = inflater.inflate(R.layout.item_container_sentmessage, parent, false);
                return new SentMessageViewHolder(sentView);
            case VIEW_TYPE_RECEIVED:
                View receivedView = inflater.inflate(R.layout.item_container_receivemessage, parent, false);
                return new ReceivedMessageViewHolder(receivedView);
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageModel message = messages.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENT:
                ((SentMessageViewHolder) holder).bindData(message);
                break;
            case VIEW_TYPE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bindData(message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}

