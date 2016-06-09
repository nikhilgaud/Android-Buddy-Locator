package kj.buddylocator.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kj.buddylocator.StaticData.StaticData;
import kj.buddylocator.database.OurDatabaseHandler;
import kj.buddylocator.jsonparser.JsonParser;
import kj.buddylocator.CustomListViewAdapterWhitelist;
import kj.buddylocator.ListViewItemWhitelist;
import kj.buddylocator.R;

/**
 * Created by Kapil on 3/14/2015.
 */
public class Whitelist extends Fragment {

    ImageButton ibRefresh;
    ListView listView;
    List<ListViewItemWhitelist> items;

    public static Whitelist getInstance(){
        return new Whitelist();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.whitelist_frag, container, false);
        listView = (ListView) layout.findViewById(R.id.lvWiFi);
        ibRefresh = (ImageButton) layout.findViewById(R.id.ibRefresh);
        items = new ArrayList<ListViewItemWhitelist>();
        //items.add(new ListViewItemWhitelist("Whitelist is empty", "", "", "", "", ""));
        CustomListViewAdapterWhitelist adapter = new CustomListViewAdapterWhitelist(getActivity().getApplicationContext(), items);
        listView.setAdapter(adapter);

        // we will using AsyncTask during parsing
        new AsyncTaskParseJson().execute();

        ibRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.clear();
                Toast.makeText(getActivity(), "Successfully pulled recent location of all friends..", Toast.LENGTH_SHORT).show();
                new AsyncTaskParseJson().execute();
            }
        });
        //loadListView();



        //CustomListViewAdapterWiFi adapter = new CustomListViewAdapterWiFi(getActivity(), items);
        //listView.setAdapter(adapter);
        return layout;
    }




    public void loadListView() {

        OurDatabaseHandler wdbh = new OurDatabaseHandler(getActivity().getApplicationContext());
        wdbh.open();
        items = wdbh.readDatabase();
        wdbh.close();

    }

    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskParseJson extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskParseJson.java";

        // set your json string url here
        String yourJsonStringUrl = "http://" + StaticData.SERVER_IP + "/json_test/index.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {

            try {

                // instantiate our json parser
                JsonParser jParser = new JsonParser();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl);

                if(json != null) {

                // get the array of users
                dataJsonArr = json.getJSONArray("test");

                StaticData.FULL_DB = dataJsonArr;

                // loop through all users
                for (int i = 0; i < dataJsonArr.length(); i++) {

                    JSONObject c = dataJsonArr.getJSONObject(i);

                    // Storing each json item in variable
                    String name = c.getString("name");
                    String data = c.getString("data");
                    String id = c.getString("id");
                    String lattitude = c.getString("lattitude");
                    String longitude = c.getString("longitude");
                    String whitelist = c.getString("whitelist");
                    //String username = c.getString("username");

                    // show the values in our logcat
                    Log.e(TAG, "firstname: " + name
                            + ", lastname: " + data
                            + ", id:" + id
                            + ", lattitude:" + lattitude
                            + ", longitude:" + longitude
                            + ", whitelist:" + whitelist);

                    String MY_PREFS_NAME = "prefs";
                    SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                    String myId = prefs.getString("id", "0");

                    if (id.equals(myId)) {
                        //Log.i("WL", whitelist);
                        //String[] wdata = whitelist.split("\\:");
                        StaticData.WHITELIST = whitelist.split("\\:");
                        //Log.i("WL", Arrays.toString(wdata));
                    }

                    //items.add(new ListViewItemWhitelist(name, data, id, lattitude, longitude, whitelist));
                    //+ ", username: " + username);

                }

                // loop through all users
                for (int i = 0; i < dataJsonArr.length(); i++) {

                    JSONObject c = dataJsonArr.getJSONObject(i);

                    // Storing each json item in variable
                    String name = c.getString("name");
                    String id = c.getString("id");
                    String data = c.getString("data");
                    String lattitude = c.getString("lattitude");
                    String longitude = c.getString("longitude");
                    String whitelist = c.getString("whitelist");

                    if (Arrays.toString(StaticData.WHITELIST).contains(id)) {
                        items.add(new ListViewItemWhitelist(name, data, id, lattitude, longitude, whitelist));
                    }

                }

            }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {

            CustomListViewAdapterWhitelist adapter = new CustomListViewAdapterWhitelist(getActivity(), items);
            listView.setAdapter(adapter);
        }
    }

}
