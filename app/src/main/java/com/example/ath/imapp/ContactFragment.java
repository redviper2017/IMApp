package com.example.ath.imapp;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ath.imapp.interfaces.GetDataService;
import com.example.ath.imapp.model.Employee;
import com.example.ath.imapp.network.RetrofitClientInstance;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactFragment extends Fragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private static final String TAG = "ContactFragment";

    private static final int READ_CONTACT_PERMISSION = 1;

    private ListView listView;
    private ContactAdapter contactAdapter;
    private ArrayList<ContactModel> contactModelArrayList;
    private Context context;

    private ArrayList<Employee> employeeList;

    public ContactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        listView = view.findViewById(R.id.listView);

        employeeList = new ArrayList<>();

        contactModelArrayList = new ArrayList<>();

        context = getActivity().getApplicationContext();

        populateContactsList(context);



        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //requesting permission to read device contacts
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED){

        }else {
            requestContactPermission();
        }
    }    

    private void populateContactsList(final Context context) {
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                //plus any other properties you wish to query
        };

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, null, null, null);
        } catch (SecurityException e) {
            //SecurityException can be thrown if we don't have the right permissions
        }

        if (cursor != null) {
            try {
                Bitmap bp = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.user_no_photo);
                HashSet<String> normalizedNumbersAlreadyFound = new HashSet<>();
                int indexOfNormalizedNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);
                int indexOfDisplayName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexOfDisplayNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                while (cursor.moveToNext()) {
                    String normalizedNumber = cursor.getString(indexOfNormalizedNumber);
                    if (normalizedNumbersAlreadyFound.add(normalizedNumber)) {
                        String displayName = cursor.getString(indexOfDisplayName);
                        String displayNumber = cursor.getString(indexOfDisplayNumber);
                        String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO_URI));
                        if (imageUri != null) {
                            try {
                                bp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(imageUri));
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        ContactModel contactModel = new ContactModel();



                        contactModel.setName(displayName);
                        contactModel.setNumber(displayNumber);
                        contactModel.setImageUri(bp);
                        contactModelArrayList.add(contactModel);
                    }
                }
            } finally {
                cursor.close();
            }
            loadEmployees();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    contactAdapter = new ContactAdapter(context, sortMyContactsList(contactModelArrayList));
                    listView.setAdapter(contactAdapter);
                }
            },1000);

        }
    }

    //This function is used to sort the fetched contacts from the device
    public ArrayList<ContactModel> sortMyContactsList(final ArrayList<ContactModel> contactModelArrayList) {
        // sorting the array list of contacts
        ArrayList<String>  stringArrayList = new ArrayList<>();

        ArrayList<ContactModel> userContacts = new ArrayList<>();

        ArrayList<ContactModel> sortedUserContacts = new ArrayList<>();

        //filtering the device contacts who are registered to the app
        for (int i=0 ;i<contactModelArrayList.size(); i++){

            String number = contactModelArrayList.get(i).getNumber();

            number = number.replaceAll("\\s+","");



            for (int j=0; j<employeeList.size();j++){
                if (employeeList.get(j).getMobile().equals(number)){
                    contactModelArrayList.get(i).setNumber(number);
                    contactModelArrayList.get(i).setName(employeeList.get(j).getFirstName());

                    //setting contact image
                    final String imageURL = employeeList.get(j).getIamge();

                    final int k =i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Uri uri =  Uri.parse( imageURL );
                                Bitmap bitmap = null;
                                InputStream in = new java.net.URL(imageURL).openStream();
                                bitmap = BitmapFactory.decodeStream(in);
                                Log.d(TAG,"bitmap: "+bitmap);


                                contactModelArrayList.get(k).setImageUri(bitmap);
                                Log.d(TAG,"bitmap set is "+contactModelArrayList.get(k).getImageUri());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                    userContacts.add(contactModelArrayList.get(i));
                    Log.d(TAG,"all my contact numbers: "+number);
                }
            }
        }

        Log.d(TAG,"number of user app contacts: "+userContacts.size());

        //sorting the contacts in alphabetical order
        for (int i=0; i<userContacts.size(); i++)
            stringArrayList.add(userContacts.get(i).getName());

        Collections.sort(stringArrayList);

        for (int i=0; i<stringArrayList.size(); i++){
            for (int j=0; j<userContacts.size(); j++){
                if (stringArrayList.get(i).equals(userContacts.get(j).getName())){
                    sortedUserContacts.add(userContacts.get(j));
                }
            }
        }

        return sortedUserContacts;
    }

    public void loadEmployees(){
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Employee>> call = service.getAllEmployees();
        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(@NonNull Call<List<Employee>> call, @NonNull Response<List<Employee>> response) {
                List<Employee> exampleList = response.body();
                employeeList.addAll(exampleList);
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Log.d(TAG,"response from api: "+"FAILED");
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();

    }


    //methods for requesting permission to read device contacts
    private void requestContactPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_CONTACTS)){

            new AlertDialog.Builder(getContext(), R.style.Theme_AppCompat_Dialog_Alert)
                    .setTitle("Contact Permission Needed")
                    .setMessage("This permission is needed for setting up your contacts")
                    .setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION);
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Intent intent = new Intent(getContext(),HomeActivity.class);
                            startActivity(intent);
                        }
                    })
                    .create().show();

        }else {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_CONTACT_PERMISSION){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getContext(),"Permission GRANTED",Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(getContext(),"Permission DENIED",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return false;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return false;
    }
}