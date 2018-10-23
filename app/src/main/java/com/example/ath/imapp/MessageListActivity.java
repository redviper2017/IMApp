package com.example.ath.imapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class MessageListActivity extends AppCompatActivity {

    private static final String TAG = "MessageListActivity";

    private String senderName, userName, senderImage;
    private int senderId, userId;
    private ArrayList<Messages> allChatMessagesListForUser;
    private  ArrayList<Personnel> allMillitaryPersonnelList;

    private ArrayList<Messages> messagesListToDisplay;
    private ArrayList<UserMessage> chatListToDisplay;

    private RecyclerView mMessageRecycler;

    private Context context;

    DetailedChatAdapter detailedChatAdapter;

    private ArrayList<Integer> allCommunicatorsIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        context = getApplicationContext();

        messagesListToDisplay = new ArrayList<>();
        chatListToDisplay = new ArrayList<>();

        allCommunicatorsIdList = new ArrayList<Integer>();

        //retrieving all data passed from ChatsFragment
        Intent intent = getIntent();
        userName = intent.getStringExtra("UserName");
        userId = intent.getIntExtra("UserId",0);
        senderName = intent.getStringExtra("SenderName");
        senderId = intent.getIntExtra("SenderId",0);
        senderImage = intent.getStringExtra("SenderImage");
        allChatMessagesListForUser = this.getIntent().getExtras().getParcelableArrayList("AllChatsList");
        allMillitaryPersonnelList = this.getIntent().getExtras().getParcelableArrayList("AllMilitaryPersonnel");

        //checking data retrieved from intent
        Log.d(TAG,"user name = "+userName);
        Log.d(TAG, "user id = "+userId);
        Log.d(TAG, "sender name = "+senderName);
        Log.d(TAG, "sender id = "+senderId);
        Log.d(TAG, "sender image = "+senderImage);
        Log.d(TAG, "size of all chats list = "+allChatMessagesListForUser.size());
        Log.d(TAG, "size of all military personnel list = "+allMillitaryPersonnelList.size());

        for (int i=0; i<allChatMessagesListForUser.size(); i++) {
            Log.d(TAG, "sender: " + allChatMessagesListForUser.get(i).getSenderId());
            Log.d(TAG, "receiver: " + allChatMessagesListForUser.get(i).getReceiverId());
        }

        getAllCommunicatorsId(allChatMessagesListForUser);

        mMessageRecycler.setAdapter(null);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
        mMessageRecycler.hasFixedSize();

        for (int i=0; i<allChatMessagesListForUser.size(); i++){
            if (allChatMessagesListForUser.get(i).getSenderId()==senderId
                    ||
                    allChatMessagesListForUser.get(i).getReceiverId()==senderId){
                messagesListToDisplay.add(allChatMessagesListForUser.get(i));
            }
        }
        Log.d(TAG,"total number of messages for current user and sender = "+messagesListToDisplay.size());
        for (int i=0; i<messagesListToDisplay.size();i++){

            if (messagesListToDisplay.get(i).getSenderId()!=userId){
                UserMessage userMessage = new UserMessage(
                        senderName,senderImage,messagesListToDisplay.get(i).getMessage(),messagesListToDisplay.get(i).getTimestamp(),userName
                );
                chatListToDisplay.add(userMessage);
            }
            else {
                UserMessage userMessage = new UserMessage(
                        userName,senderImage,messagesListToDisplay.get(i).getMessage(),messagesListToDisplay.get(i).getTimestamp(),senderName
                );
                chatListToDisplay.add(userMessage);
            }
        }

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // sorting the list in ascending order of time
//
//                ArrayList<UserMessage> temp = new ArrayList<>();
//
//                for (int i=0; i<chatListToDisplay.size(); i++){
//                    for (int j=1; j<chatListToDisplay.size(); j++){
//                        if (Long.parseLong(stringTimestampToInt(chatListToDisplay.get(i).getTime()))
//                                >
//                                Long.parseLong(stringTimestampToInt(chatListToDisplay.get(j).getTime()))){
//                            UserMessage userMessage =  chatListToDisplay.get(i);
//                            chatListToDisplay.add(i,chatListToDisplay.get(j));
//                            chatListToDisplay.add(j,userMessage);
//                        }
//                    }
//                }
//
//                for (int i=0; i<chatListToDisplay.size();i++)
//                    Log.d(TAG,"total number of timestamps: "+chatListToDisplay.size());
//            }
//        }).start();


        chatListToDisplay = sortList(chatListToDisplay);
        Log.d(TAG,"earliest message's timestamp = "+chatListToDisplay.get(0).getTime());
        Log.d(TAG,"chat list size in adapter = "+chatListToDisplay.size());

        detailedChatAdapter = new DetailedChatAdapter(context,sortList(chatListToDisplay),senderName);
        detailedChatAdapter.notifyDataSetChanged();

        mMessageRecycler.setAdapter(detailedChatAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
        mMessageRecycler.hasFixedSize();
    }

    //this function is used to find all the IDs of the military personnel with whom the current user has communicated so far
    private void getAllCommunicatorsId(ArrayList<Messages> allChatMessagesListForUser) {
        for (int i=0 ;i<allChatMessagesListForUser.size(); i++){
            if (allChatMessagesListForUser.get(i).getSenderId()!=1){
                allCommunicatorsIdList.add(allChatMessagesListForUser.get(i).getSenderId());
            }else if (allChatMessagesListForUser.get(i).getSenderId()==1){
                allCommunicatorsIdList.add(allChatMessagesListForUser.get(i).getReceiverId());
            }else
                allCommunicatorsIdList.add(allChatMessagesListForUser.get(i).getSenderId());
        }

        Log.d(TAG,"number of people communicated with this user = "+allCommunicatorsIdList.size());
        for (int i=0; i<allCommunicatorsIdList.size(); i++)
            Log.d(TAG,"user "+allCommunicatorsIdList.get(i)+" has communicated with "+userName);

        Set<Integer> hs = new HashSet<>();
        hs.addAll(allCommunicatorsIdList);
        allCommunicatorsIdList.clear();
        allCommunicatorsIdList.addAll(hs);

        for (int i=0; i<allCommunicatorsIdList.size(); i++)
            Log.d(TAG,"unique user "+allCommunicatorsIdList.get(i)+" has communicated with "+userName);
    }

    public Long sortListBasedOnTimestamp(ArrayList<UserMessage> messageList){
        ArrayList<UserMessage> temp = new ArrayList<>();
        Long minimum = Long.parseLong(stringTimestampToInt(messageList.get(0).getTime()));
        for (int i=1; i<messageList.size(); i++){
            if (Long.parseLong(stringTimestampToInt(messageList.get(i).getTime()))<minimum){
                minimum = Long.parseLong(stringTimestampToInt(messageList.get(i).getTime()));
            }
        }
        Log.d(TAG,"minimum = "+minimum);
        return minimum;
    }

    public ArrayList<UserMessage> sortList(ArrayList<UserMessage> messageList){
        Collections.sort(messageList, new Comparator<UserMessage>() {
            @Override
            public int compare(UserMessage o1, UserMessage o2) {
                if (Long.parseLong(stringTimestampToInt(o1.getTime()))>Long.parseLong(stringTimestampToInt(o2.getTime()))){
                    return 1;
                }
                if (Long.parseLong(stringTimestampToInt(o1.getTime()))<Long.parseLong(stringTimestampToInt(o2.getTime()))){
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        return messageList;
    }


    //following function converts the timestamp of the messages into plain strings
    public String stringTimestampToInt(String timeStamp){
        String time = null;
        char timeCharArray[] = timeStamp.toCharArray();
        for (int i=0; i<timeCharArray.length; i++){
            if (timeCharArray[i]!='-' && timeCharArray[i]!=':' && timeCharArray[i]!=' '){
                if (time !=null) {
                    time = time + timeCharArray[i];
                }else {
                    time = String.valueOf(timeCharArray[i]);
                }
            }
        }
        return time;
    }

}
