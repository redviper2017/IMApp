package com.example.ath.imapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ath.imapp.model.Employee;
import com.example.ath.imapp.model.Example;

import java.util.ArrayList;

public class ChatAdapter extends BaseAdapter{

    private static final String TAG = "ChatsAdapter";

    private Context context;
    private ArrayList<Example> receivedMessagesList;

    private ArrayList<Employee> employeeList;

    private String currentUserName = null;

    private int currentUserID = 0;


    public ChatAdapter(Context context, ArrayList<Example> receivedMessagesList, ArrayList<Employee> employeeList, String currentUserName, int currentUserID) {

        this.context = context;
        this.receivedMessagesList = receivedMessagesList;
        this.employeeList = employeeList;
        this.currentUserName = currentUserName;
        this.currentUserID = currentUserID;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return receivedMessagesList.size();
    }

    @Override
    public Object getItem(int position) {
        return receivedMessagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null){

            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.chat_rows, null, true);
            holder.tvname = convertView.findViewById(R.id.sender_name);
            holder.tvmessage = convertView.findViewById(R.id.sender_message);
            holder.ivImage = convertView.findViewById(R.id.sender_image);

            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }


        Log.d(TAG,"size of received list: "+receivedMessagesList.size());

        for (int i=0;i<receivedMessagesList.size(); i++)
            Log.d(TAG,"sender of received list: "+receivedMessagesList.get(i).getSenderId());

        Log.d(TAG,"current user id: "+currentUserID);

        if (receivedMessagesList.get(position).getSenderId()!=currentUserID){
            for (int i=0; i<employeeList.size(); i++){
                if (receivedMessagesList.get(position).getSenderId()==employeeList.get(i).getId()){
                    String name = employeeList.get(i).getFirstName()+" "+employeeList.get(i).getLastName();
                    holder.tvname.setText(name);
                    holder.tvmessage.setText(receivedMessagesList.get(position).getMessage());
                    Uri uri = Uri.parse(employeeList.get(i).getIamge());
                    Glide.with(context).load(uri).into(holder.ivImage);
                }
            }
        }else {
            for (int i=0; i<employeeList.size(); i++){
                if (receivedMessagesList.get(position).getReceiverId()==employeeList.get(i).getId()){
                    String name = employeeList.get(i).getFirstName()+" "+employeeList.get(i).getLastName();
                    holder.tvname.setText(name);
                    holder.tvmessage.setText(receivedMessagesList.get(position).getMessage());
                    Uri uri = Uri.parse(employeeList.get(i).getIamge());
                    Glide.with(context).load(uri).into(holder.ivImage);
                }
            }
        }

//        if (receivedMessagesList.get(position).getSenderName().equals(currentUserName)){
//            for (int i=0; i<employeeList.size(); i++){
//                if (receivedMessagesList.get(position).getReceiverId()==employeeList.get(i).getId()){
//                    String name = employeeList.get(i).getFirstName()+" "+employeeList.get(i).getLastName();
//                    holder.tvname.setText(name);
//                    Uri uri = Uri.parse(employeeList.get(position).getIamge());
//                    Glide.with(context).load(uri).into(holder.ivImage);
//                }
//            }
//        }else {
//            holder.tvname.setText(receivedMessagesList.get(position).getSenderName());
//            Uri uri = Uri.parse(receivedMessagesList.get(position).getSenderImageUri());
//            Glide.with(context).load(uri).into(holder.ivImage);
//        }
//        holder.tvmessage.setText(receivedMessagesList.get(position).getSenderMessage());

        return convertView;
    }

    private class ViewHolder {

        protected TextView tvname, tvmessage;
        protected ImageView ivImage;

    }
}
