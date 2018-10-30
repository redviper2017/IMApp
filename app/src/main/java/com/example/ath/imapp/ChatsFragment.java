package com.example.ath.imapp;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ath.imapp.interfaces.GetDataService;
import com.example.ath.imapp.model.Employee;
import com.example.ath.imapp.model.Example;
import com.example.ath.imapp.network.RetrofitClientInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment{

    int currentUserID =1;

    String currentUserName = null;

    private static final String TAG = "ChatsFragment";

    private ArrayList<Employee> employeeList;


    private ArrayList<Example> allSentChatMessageListForUser;
    private ArrayList<Example> allReceivedChatMessageListForUser;

    private ArrayList<Messages> allChatMessagesListForUser;
    private ArrayList<Personnel> allMillitaryPersonnelList;

    private ListView listView;
    private ChatAdapter chatAdapter;
    private Context context;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);


        employeeList = new ArrayList<>();

        allSentChatMessageListForUser = new ArrayList<>();
        allReceivedChatMessageListForUser = new ArrayList<>();

        allChatMessagesListForUser = new ArrayList<>();
        allMillitaryPersonnelList = new ArrayList<>();

        listView = view.findViewById(R.id.chatsView);
        context = getActivity().getApplicationContext();

        loadEmployees();
        loadAllReceivedMessages();
        loadAllSentMessages();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"total number of received messages = "+allReceivedChatMessageListForUser.size());
                Log.d(TAG,"total number of sent messages = "+allSentChatMessageListForUser.size());

                createListOfAllMessages();
                createListOfAllMilitaryPersonnel();
                getCurrentUserName();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //Arranging the received message list

                        for (int i=0; i<allReceivedChatMessageListForUser.size(); i++){
                            for (int j=1;j<allReceivedChatMessageListForUser.size();j++){
                                if (allReceivedChatMessageListForUser.get(j).getSenderId()==allReceivedChatMessageListForUser.get(i).getSenderId()){
                                    if (Long.parseLong(stringTimestampToInt(allReceivedChatMessageListForUser.get(i).getTimestamp()))
                                            <
                                            Long.parseLong(stringTimestampToInt(allReceivedChatMessageListForUser.get(j).getTimestamp()))){
                                        allReceivedChatMessageListForUser.remove(i);
                                    }else if (Long.parseLong(stringTimestampToInt(allReceivedChatMessageListForUser.get(i).getTimestamp()))
                                            >
                                            Long.parseLong(stringTimestampToInt(allReceivedChatMessageListForUser.get(j).getTimestamp()))){
                                        allReceivedChatMessageListForUser.remove(j);
                                    }
                                }
                            }
                        }

                        for (int i=0; i<allReceivedChatMessageListForUser.size(); i++) {
                            Log.d(TAG, "sender " + i + " : " + allReceivedChatMessageListForUser.get(i).getSenderId());
                            Log.d(TAG,"sender message "+i+" : "+allReceivedChatMessageListForUser.get(i).getMessage());
                        }

                        Log.d(TAG, "allReceivedChatMessageListForUser size = " + allReceivedChatMessageListForUser.size());
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //arranging the sent message list

                        for (int i=0; i<allSentChatMessageListForUser.size(); i++){
                            for (int j=1; j<allSentChatMessageListForUser.size(); j++){
                                if (allSentChatMessageListForUser.get(j).getReceiverId()==allSentChatMessageListForUser.get(i).getReceiverId()){
                                    if (Long.parseLong(stringTimestampToInt(allSentChatMessageListForUser.get(i).getTimestamp()))
                                            <
                                            Long.parseLong(stringTimestampToInt(allSentChatMessageListForUser.get(j).getTimestamp()))){
                                        allSentChatMessageListForUser.remove(i);
                                    }else if (Long.parseLong(stringTimestampToInt(allSentChatMessageListForUser.get(i).getTimestamp()))
                                            >=
                                            Long.parseLong(stringTimestampToInt(allSentChatMessageListForUser.get(j).getTimestamp()))){
                                        allSentChatMessageListForUser.remove(j);
                                    }
                                }
                            }
                        }

                        for (int i=0; i<allSentChatMessageListForUser.size(); i++) {
                            Log.d(TAG, "receiver " + i + " : " + allSentChatMessageListForUser.get(i).getReceiverId());
                            Log.d(TAG, "receiver message " + i + " : " + allSentChatMessageListForUser.get(i).getMessage());
                        }
                        Log.d(TAG, "allSentChatMessageListForUser size = " + allSentChatMessageListForUser.size());
                    }
                }).start();
            }
        },1000);

        //creating the final chat list to display with the most recent chats

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {

                for (int i=0; i<allReceivedChatMessageListForUser.size(); i++){
                    for (int j=0; j<allSentChatMessageListForUser.size(); j++){
                        if (allReceivedChatMessageListForUser.get(i).getSenderId()==allSentChatMessageListForUser.get(j).getReceiverId()) {
                            allReceivedChatMessageListForUser.get(i).setMessage(allSentChatMessageListForUser.get(j).getMessage());
                            allSentChatMessageListForUser.remove(j);
                        }
                    }
                }
                Log.d(TAG,"final size of allSentChatMessageListForUser = "+allSentChatMessageListForUser.size());

                for (int i=0; i<allSentChatMessageListForUser.size(); i++){
                    Log.d(TAG,"final sender of allSentChatMessageListForUser = "+i+" "+allSentChatMessageListForUser.get(i).getReceiverId());
                    allReceivedChatMessageListForUser.add(allSentChatMessageListForUser.get(i));
                }
                Log.d(TAG,"final size of allReceivedChatMessageListForUser = "+allReceivedChatMessageListForUser.size());

                chatAdapter = new ChatAdapter(context,allReceivedChatMessageListForUser,employeeList,currentUserName,currentUserID);
                chatAdapter.notifyDataSetChanged();
                listView.setAdapter(chatAdapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String senderName = ((TextView)view.findViewById(R.id.sender_name)).getText().toString();
                        Log.d(TAG,"sender name passed in intent is : "+senderName);


                        int senderId = allReceivedChatMessageListForUser.get(position).getSenderId();
                        if (senderId==currentUserID){
                            senderId = allReceivedChatMessageListForUser.get(position).getReceiverId();
                        }

                        Log.d(TAG,"current sender id = "+senderId);
                        int messageId = allReceivedChatMessageListForUser.get(position).getId();
                        String senderImage = getSenderImage(senderId,messageId);
                        Log.d(TAG,"current sender image = "+senderImage);

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList("AllChatsList", allChatMessagesListForUser);
                        bundle.putParcelableArrayList("AllMilitaryPersonnel",allMillitaryPersonnelList);

                        Intent intent = new Intent(context,MessageListActivity.class);
                        intent.putExtras(bundle);
                        intent.putExtra("SenderName",senderName);
                        intent.putExtra("SenderId",senderId);
                        intent.putExtra("SenderImage",senderImage);
                        intent.putExtra("UserName",currentUserName);
                        intent.putExtra("UserId",currentUserID);

                        startActivity(intent);
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        registerForContextMenu(listView);
                        return false;
                    }
                });
            }
        },2000);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.options,menu);
//        menu.setHeaderTitle("What do you want to do with this chat?");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.option_delete){
            Toast.makeText(context,"deleteing...",Toast.LENGTH_LONG).show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void loadAllReceivedMessages(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Example>> call = service.getAllReceivedChatsForUser();
        call.enqueue(new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                List<Example> receivedList = response.body();
                Log.d(TAG,"code got inside loadAllReceivedMessages = "+response.code());
                allReceivedChatMessageListForUser.addAll(receivedList);
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {

            }
        });
    }


    public void loadAllSentMessages(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Example>> call = service.getAllSentChatsForUser();
        call.enqueue(new Callback<List<Example>>() {
            @Override
            public void onResponse(Call<List<Example>> call, Response<List<Example>> response) {
                List<Example> sentList = response.body();
                Log.d(TAG,"code got inside loadAllSentMessages = "+response.code());
                allSentChatMessageListForUser.addAll(sentList);
            }

            @Override
            public void onFailure(Call<List<Example>> call, Throwable t) {

            }
        });
    }


    //following function gets the data of all employees from the server

    public void loadEmployees(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Employee>> call = service.getAllEmployees();
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(@NonNull Call<List<Employee>> call, @NonNull Response<List<Employee>> response) {
                List<Employee> exampleList = response.body();
                employeeList.addAll(exampleList);
                if (exampleList != null) {
                    Log.d(TAG,"total number of employees is: "+exampleList.size());
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Log.d(TAG,"response from api: "+"FAILED");
            }
        });
    }

    public void createListOfAllMessages(){
        for (int i=0; i<allReceivedChatMessageListForUser.size();i++){
            Messages messages = new Messages(allReceivedChatMessageListForUser.get(i).getId(),allReceivedChatMessageListForUser.get(i).getSenderId(),
                    allReceivedChatMessageListForUser.get(i).getReceiverId(),allReceivedChatMessageListForUser.get(i).getMessage(),
                    allReceivedChatMessageListForUser.get(i).getTimestamp());
            allChatMessagesListForUser.add(messages);
        }

        for (int i=0; i<allSentChatMessageListForUser.size();i++){
            Messages messages = new Messages(allSentChatMessageListForUser.get(i).getId(),allSentChatMessageListForUser.get(i).getSenderId(),
                    allSentChatMessageListForUser.get(i).getReceiverId(),allSentChatMessageListForUser.get(i).getMessage(),
                    allSentChatMessageListForUser.get(i).getTimestamp());
            allChatMessagesListForUser.add(messages);
        }

        Log.d(TAG,"total number of messages = "+ allChatMessagesListForUser.size());
    }

    public void createListOfAllMilitaryPersonnel(){
        for (int i=0; i<employeeList.size();i++){
            Personnel personnel = new Personnel(employeeList.get(i).getId(), employeeList.get(i).getFirstName(), employeeList.get(i).getLastName(),
                    employeeList.get(i).getEmail(), employeeList.get(i).getImage(), employeeList.get(i).getMobile());
            allMillitaryPersonnelList.add(personnel);
        }
        Log.d(TAG,"total number of military personnel = "+allMillitaryPersonnelList.size());
    }

    public void getCurrentUserName(){
        for (int i=0; i<employeeList.size(); i++){
            if (employeeList.get(i).getId()==currentUserID){
                currentUserName = employeeList.get(i).getFirstName()+" "+employeeList.get(i).getLastName();
            }
        }
    }

    public String getSenderImage(int senderId, int messageId){
        String senderImage = null;
        for (int i=0; i<employeeList.size(); i++){
            if (senderId!=currentUserID) {
                if (employeeList.get(i).getId() == senderId) {
                    senderImage = employeeList.get(i).getImage();
                }
            }else {
                for (int j=0; j<allChatMessagesListForUser.size(); j++){
                    if (allChatMessagesListForUser.get(j).getId()==messageId){
                        int id = allChatMessagesListForUser.get(j).getReceiverId();
                        for (int k=0; k<employeeList.size(); k++){
                            if (employeeList.get(k).getId()==id){
                                senderImage = employeeList.get(k).getImage();
                            }
                        }
                    }
                }
            }
        }
        return senderImage;
    }

    public void saveArrayList(ArrayList<ChatModel> chatList, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(chatList);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public ArrayList<ChatModel> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<ChatModel>>() {}.getType();
        return gson.fromJson(json, type);
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