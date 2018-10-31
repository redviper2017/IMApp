package com.example.ath.imapp;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ath.imapp.interfaces.GetDataService;
import com.example.ath.imapp.model.Employee;
import com.example.ath.imapp.network.RetrofitClientInstance;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    android.support.v7.widget.Toolbar homeToolbar;
    TabLayout homeTabLayout;
    ViewPager viewPager;
    TabItem callsTab, chatsTab;
    HomeFragmentPagerAdapter homeFragmentPagerAdapter;

    boolean fabExpanded = false;
    FloatingActionButton fabCreate;
    LinearLayout layoutFabSecretChat;
    LinearLayout layoutFabChanel;
    LinearLayout layoutFabGroup;
    LinearLayout layoutFabChat;

    DrawerLayout drawerLayout;

    private int userId = 1; //Later this id will be acquired from the login credentials
    CircleImageView user_image;
    TextView user_name;
    TextView user_phone;

    private ArrayList<Employee> employeeList;

    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        homeToolbar = findViewById(R.id.toolbar_home);
        homeToolbar.setTitle("");
        setSupportActionBar(homeToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);


        homeTabLayout = findViewById(R.id.tabs_home);
        callsTab = findViewById(R.id.tab_calls);
        chatsTab = findViewById(R.id.tab_chats);

        viewPager = findViewById(R.id.tabs_pager_home);
        homeFragmentPagerAdapter = new HomeFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(homeFragmentPagerAdapter);
        homeTabLayout.setupWithViewPager(viewPager);

        fabCreate = findViewById(R.id.fabCreates);

        layoutFabSecretChat = findViewById(R.id.layoutNewSecretChat);
        layoutFabChanel = findViewById(R.id.layoutNewChanel);
        layoutFabGroup = findViewById(R.id.layoutNewGroup);
        layoutFabChat = findViewById(R.id.layoutNewChat);

        drawerLayout = findViewById(R.id.drawer_layout);



        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.getHeaderView(0);

        user_image = headerView.findViewById(R.id.img_nav_header_img);
        user_name = headerView.findViewById(R.id.txt_nav_header_name);
        user_phone = headerView.findViewById(R.id.txt_nav_header_phone);

        employeeList = new ArrayList<>();

        loadEmployees();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"employeelist size inside home activity = "+employeeList.size());
                for (int i=0; i<employeeList.size(); i++){
                    if (userId == employeeList.get(i).getId()){
                        String name = (employeeList.get(i).getFirstName() +" "+ employeeList.get(i).getLastName());
                        String phone = employeeList.get(i).getMobile();
                        user_name.setText(name);
                        user_phone.setText(phone);
                        Uri uri = Uri.parse(employeeList.get(i).getIamge());
                        Glide.with(getApplicationContext()).load(uri).into(user_image);
                        Log.d(TAG,"user name = "+name);
                        Log.d(TAG,"user name = "+phone);

                        saveEmployeeList(employeeList,"EmployeeList");
                    }
                }
            }
        },2000);


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                switch (id){
                    case R.id.nav_contacts:
                        fragment = new ContactFragment();
                        fragmentManager.popBackStackImmediate();
                        break;
                    case R.id.nav_settings:
                        fragment = new SettingsFragment();
                        fragmentManager.popBackStackImmediate();
                        break;
                }
                if (fragment != null){
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.container,fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        //When main Fab (Create) is clicked, it expands if not expanded already.
        //Collapses if main FAB was open already.
        //This gives FAB (Create) open/close behavior
        fabCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabExpanded){
                    closeSubMenusFab();
                }else {
                    openSubMenusFab();
                }
            }
        });

        //Only main FAB is visible in the beginning
        closeSubMenusFab();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home,menu);

        MenuItem search = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) search.getActionView();
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //closes FAB submenus
    private void closeSubMenusFab(){
        layoutFabSecretChat.setVisibility(View.INVISIBLE);
        layoutFabChanel.setVisibility(View.INVISIBLE);
        layoutFabGroup.setVisibility(View.INVISIBLE);
        layoutFabChat.setVisibility(View.INVISIBLE);

        fabCreate.setImageResource(R.drawable.ic_create_24dp);
        fabExpanded = false;
    }

    //Opens FAB submenus
    private void openSubMenusFab(){
        layoutFabSecretChat.setVisibility(View.VISIBLE);
        layoutFabChanel.setVisibility(View.VISIBLE);
        layoutFabGroup.setVisibility(View.VISIBLE);
        layoutFabChat.setVisibility(View.VISIBLE);

        //Change settings icon to 'X' icon
        fabCreate.setImageResource(R.drawable.ic_close_24dp);
        fabExpanded = true;
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

    public void saveEmployeeList(ArrayList<Employee> employeeCredentials, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(employeeCredentials);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

}
