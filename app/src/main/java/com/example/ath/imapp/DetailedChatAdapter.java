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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

    MessageListActivity messageListActivity;

    public DetailedChatAdapter(Context context, ArrayList<UserMessage> mMessageList, String senderName) {
        this.mContext = context;
        this.mMessageList = mMessageList;
        this.senderName = senderName;
        messageListActivity = (MessageListActivity) context;
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
            return new SentMessageHolder(view,messageListActivity);
        }else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view,messageListActivity);
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


    private class SentMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView messageText, timeText;
        CheckBox checkBox;
        MessageListActivity messageListActivity;
        RelativeLayout relativeLayout;


        SentMessageHolder(@NonNull View itemView, MessageListActivity messageListActivity) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            checkBox = itemView.findViewById(R.id.checkbox);
            this.messageListActivity = messageListActivity;
            relativeLayout = itemView.findViewById(R.id.rel_layout_sentMessageList);
            relativeLayout.setOnLongClickListener(messageListActivity);
            checkBox.setOnClickListener(this);

        }

        void bind(UserMessage message){

            messageText.setText(message.getMessage());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(message.getTime());
            //check if the activity is is in normal mode or contextual action mode
            if (!messageListActivity.is_in_action_mode){
                checkBox.setVisibility(View.GONE);
            }
            else {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(false);
            }
        }

        @Override
        public void onClick(View v) {
            messageListActivity.prepareSelection(v, getAdapterPosition());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView messageText, timeText, nameText;
        ImageView profileImage;
        CheckBox checkBox;
        MessageListActivity messageListActivity;
        RelativeLayout relativeLayout;

        public ReceivedMessageHolder(@NonNull View itemView, MessageListActivity messageListActivity) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            checkBox = itemView.findViewById(R.id.checkbox);
            this.messageListActivity = messageListActivity;
            relativeLayout = itemView.findViewById(R.id.rel_layout_receivedMessageList);
            relativeLayout.setOnLongClickListener(messageListActivity);
            checkBox.setOnClickListener(this);
        }

        void bind(UserMessage message){
            Log.d(TAG,"message text: "+message.getMessage());
            messageText.setText(message.getMessage());
            timeText.setText(message.getTime());
            nameText.setText(message.getSenderName());
            Uri uri = Uri.parse(message.getSenderImage());
            Glide.with(mContext).load(uri).into(profileImage);
            //check if the activity is is in normal mode or contextual action mode
            if (!messageListActivity.is_in_action_mode){
                checkBox.setVisibility(View.GONE);
            }
            else {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(false);
            }
        }

        @Override
        public void onClick(View v) {
            messageListActivity.prepareSelection(v, getAdapterPosition());
        }
    }

    public void updateAdapter(ArrayList<UserMessage> list){
        for (UserMessage userMessage : list){
            mMessageList.remove(userMessage);
        }
        notifyDataSetChanged();
    }

    public void setFilter(ArrayList<UserMessage> newList){
        mMessageList = new ArrayList<>();
        mMessageList.addAll(newList);
        notifyDataSetChanged();
    }

}
