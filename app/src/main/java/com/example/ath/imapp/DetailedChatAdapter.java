package com.example.ath.imapp;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;

public class DetailedChatAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private static final String TAG = "DetailedChatAdapter";

    private Context mContext;

    private ArrayList<UserMessage> mMessageList;

    private String senderName;

    public DetailedChatAdapter(Context mContext, ArrayList<UserMessage> mMessageList, String senderName) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
        this.senderName = senderName;
        Log.d(TAG,"message list size in DetailedChatAdapter: "+mMessageList.size());
    }

    @Override
    public int getItemCount() {
        Log.d(TAG,"list size: "+mMessageList.size());
        return mMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {

        UserMessage message = mMessageList.get(position);

        if (senderName.equals(message.getSenderName())){
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }else {
            return VIEW_TYPE_MESSAGE_SENT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType==VIEW_TYPE_MESSAGE_SENT){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        }else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        UserMessage message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }

    }


    private class SentMessageHolder extends RecyclerView.ViewHolder{

        TextView messageText, timeText;

        SentMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(UserMessage message){

            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());

        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder{

        TextView messageText, timeText, nameText;
        ImageView profileImage;

        public ReceivedMessageHolder(@NonNull View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(UserMessage message){
            Log.d(TAG,"message text: "+message.getMessage());
            messageText.setText(message.getMessage());
            timeText.setText(message.getTime());
            nameText.setText(message.getSenderName());
            Uri uri = Uri.parse(message.getSenderImage());
            Glide.with(mContext).load(uri).into(profileImage);
        }
    }
}
