package kj.buddylocator;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import kj.buddylocator.StaticData.StaticData;
import kj.buddylocator.api.FloatingActionButton;
import kj.buddylocator.fragments.Contacts;
import kj.buddylocator.fragments.Whitelist;
import kj.buddylocator.tabs.SlidingTabLayout;


public class MainActivity extends ActionBarActivity {

    ViewPager mPager;
    SlidingTabLayout mTabs;

    FloatingActionButton fabButton;

    AlertDialog.Builder builder, wifiMessageBuilder;
    AlertDialog actions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(tb);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#303f9f"));

            setupFab();
            setupFabListener();
        }

        setTabsAndPager();

        checkIfRegistered();

        //new CheckIfServerOnline().execute();
/*
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://"+StaticData.SERVER_IP+"/json_test/hello.php");

            HttpResponse httpResponse = httpClient.execute(httpPost);
        } catch (UnknownHostException e) {
            Toast.makeText(getApplicationContext(), "Server Offline", Toast.LENGTH_LONG).show();
        } catch (ClientProtocolException e) {
            Toast.makeText(getApplicationContext(), "Server Offline", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Server Offline", Toast.LENGTH_LONG).show();
        }*/

    }

    private void checkIfRegistered() {

        //SharedPreferences prefs = this.getSharedPreferences();//.gegetPreferences(MODE_PRIVATE);
        //String restoredText = prefs.getString("registered", null);
        String MY_PREFS_NAME = "prefs";
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("registered", null);
        //Toast.makeText(getApplicationContext(), restoredText, Toast.LENGTH_LONG).show();
        if (restoredText!=null && !restoredText.equals("yes"))
        {
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        } else if(restoredText==null){
            Intent i = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(i);
        }

    }

public class CheckIfServerOnline extends AsyncTask{

    boolean online = true;
    @Override
    protected Object doInBackground(Object[] params) {

        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://"+StaticData.SERVER_IP+"/json_test/hello.php");

            if(httpClient.execute(httpPost) == null){
                online = false;
            }

        } catch (UnknownHostException e) {
            Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
        } catch (ClientProtocolException e) {
            Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_LONG).show();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(!online){
            Toast.makeText(getApplicationContext(), "Server Offline", Toast.LENGTH_LONG).show();
        }
    }
}
    private void setTabsAndPager() {
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setViewPager(mPager);
    }



    private void setupFabListener() {
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPager.getCurrentItem() == 0){
                    // Create Whitelist profile
                    //Toast.makeText(getApplicationContext(), "Create Whitelist contact", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, AddWhitelist.class);
                    startActivity(i);
                } else if(mPager.getCurrentItem() == 1){
                    // Create Contact list
                    Toast.makeText(getApplicationContext(), "Search for a new contact", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupFab() {

        fabButton = new FloatingActionButton.Builder(this)
                .withDrawable(getDrawable(R.drawable.ic_action_content_add))
                .withButtonColor(Color.parseColor("#ee009385"))
                .withGravity(Gravity.BOTTOM | Gravity.RIGHT)
                .withMargins(0, 0, 16, 16)
                .create();

    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String[] some_array = getResources().getStringArray(R.array.tab_name);
            return some_array[position];
        }

        @Override
        public Fragment getItem(int i) {

            switch (i){
                case 0: Whitelist wi = Whitelist.getInstance();
                        return wi;
                case 1: Contacts pr = Contacts.getInstance();
                    return pr;
            }

            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getMenuInflater().inflate(R.menu.menu_main, menu);
           // MenuItem item = menu.findItem(R.id.myswitch);
            //item.setActionView(R.layout.switch_layout);
        } else {
            getMenuInflater().inflate(R.menu.menu_main_old, menu);
            //MenuItem item = menu.findItem(R.id.myswitch);
            //item.setActionView(R.layout.switch_layout);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_checkin) {
            Intent i = new Intent(MainActivity.this, CheckIn.class);
            startActivity(i);
            return true;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            if(id == R.id.action_add){
                if(mPager.getCurrentItem() == 0){
                    // Create Whitelist profile
                    Toast.makeText(getApplicationContext(), "Create Whitelist contact", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, AddWhitelist.class);
                    startActivity(i);
                } else if(mPager.getCurrentItem() == 1){
                    // Create Contact list
                    Toast.makeText(getApplicationContext(), "Search for a new contact", Toast.LENGTH_SHORT).show();
                }
            }
        }


        if (id == R.id.action_id) {
            String MY_PREFS_NAME = "prefs";
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            String myId = prefs.getString("id", "0");
            if(myId != "0"){
                new AlertDialog.Builder(this)
                        .setMessage("ID: "+myId)
                        .show();
                //Toast.makeText(getApplicationContext(), "Your ID is:\n" + myId, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Please register first to use this app", Toast.LENGTH_LONG).show();
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            }

            return true;
        }

        if (id == R.id.action_info) {

            new AlertDialog.Builder(this)
                    .setMessage("Buddy Locator\n\nProject by:\nKapil Jituri\nNiranjan Molkeri\nNikhil Gaud\n\nFor class CSCI 567 at CSU Chico\nunder Prof. Bryan Dixon")
                    .show();
            return true;
        }



        return super.onOptionsItemSelected(item);
    }
}
