package com.example.ath.imapp;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ath.imapp.interfaces.GetDataService;
import com.example.ath.imapp.network.RetrofitClientInstance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageListActivity extends AppCompatActivity implements View.OnLongClickListener {

    boolean is_in_action_mode = false;

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

    private EditText newChatMessageEditText;
    private Button chatSendButton;

    private ArrayList<Integer> allCommunicatorsIdList;

    ArrayList<UserMessage> selectionList;
    int counter = 0;
    TextView counter_text_view;

    Toolbar toolbar;

    RecyclerView.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        mMessageRecycler = findViewById(R.id.reyclerview_message_list);
        context = getApplicationContext();

        messagesListToDisplay = new ArrayList<>();
        chatListToDisplay = new ArrayList<>();

        allCommunicatorsIdList = new ArrayList<>();

        selectionList = new ArrayList<>();

        newChatMessageEditText = findViewById(R.id.edittext_chatbox);
        chatSendButton =  findViewById(R.id.button_chatbox_send);

        toolbar  = findViewById(R.id.toolbar_messages);
        setSupportActionBar(toolbar);
        counter_text_view = findViewById(R.id.counter_text);


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
            Log.d(TAG, "sender: " + allChatMessagesListForUser.get(i).getSender_id());
            Log.d(TAG, "receiver: " + allChatMessagesListForUser.get(i).getReceiverId());
        }

        getAllCommunicatorsId(allChatMessagesListForUser);

        mMessageRecycler.setAdapter(null);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
        mMessageRecycler.hasFixedSize();


        for (int i=0; i<allChatMessagesListForUser.size(); i++){
            if (allChatMessagesListForUser.get(i).getSender_id()==senderId
                    ||
                    allChatMessagesListForUser.get(i).getReceiverId()==senderId){
                messagesListToDisplay.add(allChatMessagesListForUser.get(i));
            }
        }
        Log.d(TAG,"total number of messages for current user and sender = "+messagesListToDisplay.size());
        for (int i=0; i<messagesListToDisplay.size();i++){

            if (messagesListToDisplay.get(i).getSender_id()!=userId){
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

        chatListToDisplay = sortList(chatListToDisplay);
        Log.d(TAG,"earliest message's timestamp = "+chatListToDisplay.get(0).getTime());
        Log.d(TAG,"chat list size in adapter = "+chatListToDisplay.size());

        detailedChatAdapter = new DetailedChatAdapter(MessageListActivity.this,sortList(chatListToDisplay),senderName);
        detailedChatAdapter.notifyDataSetChanged();

        mMessageRecycler.setAdapter(detailedChatAdapter);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(context));
        mMessageRecycler.hasFixedSize();

        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String newMessage = newChatMessageEditText.getText().toString();
                final Messages messages = new Messages(userId,senderId,newMessage,getDateTime());
                GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                Call<Messages> call = service.createMessage(messages);
                call.enqueue(new Callback<Messages>() {
                    @Override
                    public void onResponse(Call<Messages> call, Response<Messages> response) {

                    }

                    @Override
                    public void onFailure(Call<Messages> call, Throwable t) {

                    }
                });
                UserMessage userMessage = new UserMessage(userName,senderImage,newMessage,getDateTime(),senderName);
                chatListToDisplay.add(userMessage);
                detailedChatAdapter.notifyDataSetChanged();
                newChatMessageEditText.setText(null);
            }
        });

    }

    //this function is used to find all the IDs of the military personnel with whom the current user has communicated so far
    private void getAllCommunicatorsId(ArrayList<Messages> allChatMessagesListForUser) {
        for (int i=0 ;i<allChatMessagesListForUser.size(); i++){
            if (allChatMessagesListForUser.get(i).getSender_id()!=1){
                allCommunicatorsIdList.add(allChatMessagesListForUser.get(i).getSender_id());
            }else if (allChatMessagesListForUser.get(i).getSender_id()==1){
                allCommunicatorsIdList.add(allChatMessagesListForUser.get(i).getReceiverId());
            }else
                allCommunicatorsIdList.add(allChatMessagesListForUser.get(i).getSender_id());
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

    /*This function is used to sort all the chat messages of the current user based on timestamps
    * so that the chat message that came first is displayed first in the detailed chat view*/
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

    //The function is used to get the current date+time in the api defined format
    public String getDateTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());
        String date = simpleDateFormat.format(new Date());
        Log.d(TAG,"date = "+date);
        return date;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailed_message_menu,menu);
        return true;
    }


    @Override
    public boolean onLongClick(View v) {
        Log.d(TAG,"on long click in detailedChatAdapter inside messageListActivity");
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.context_menu);
        is_in_action_mode = true;
        detailedChatAdapter.notifyDataSetChanged();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    public void prepareSelection(View view, int position){
        if (((CheckBox)view).isChecked()){
            selectionList.add(chatListToDisplay.get(position));
            counter = counter+1;
            updateCounter(counter);
        }else {
            selectionList.remove(chatListToDisplay.get(position));
            counter = counter - 1;
            updateCounter(counter);
        }
    }

    public void updateCounter(int counter){
        if (counter != 0){
            counter_text_view.setText(counter+" chats selected");
            counter_text_view.setVisibility(View.VISIBLE);
        }else {
            counter_text_view.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete){
            DetailedChatAdapter chatAdapter = detailedChatAdapter;
            chatAdapter.updateAdapter(selectionList);
            clearActionMode();
        }
        else if (item.getItemId() == android.R.id.home){
            clearActionMode();
            detailedChatAdapter.notifyDataSetChanged();
        }else if (item.getItemId() == R.id.action_copy){
            copyToClipBoard(selectionList);
            Toast.makeText(getApplicationContext(),"Copied to clipboard..",Toast.LENGTH_SHORT).show();
            clearActionMode();
        }
        return true;
    }

    public void clearActionMode(){
        is_in_action_mode = false;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.detailed_message_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        counter_text_view.setVisibility(View.GONE);
        counter = 0;
        selectionList.clear();
    }

    @Override
    public void onBackPressed() {
        if (is_in_action_mode){
            clearActionMode();
            detailedChatAdapter.notifyDataSetChanged();
        }else {
            super.onBackPressed();
        }
    }

    public void copyToClipBoard(ArrayList<UserMessage> selectionList){
        String text = "";
        for (int i=0; i<selectionList.size(); i++){
            text = text + "\n" + selectionList.get(i).getMessage();
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("copiedData", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
        }
    }
}
