package kj.buddylocator;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kj.buddylocator.R;
import kj.buddylocator.StaticData.StaticData;
import kj.buddylocator.jsonparser.JsonAddIdInWhitelist;
import kj.buddylocator.jsonparser.JsonCheckin;

public class AddWhitelist extends ActionBarActivity {

    EditText etGetID;
    Button btAdd;

    AsyncTaskAddWhitelist addWhitelistTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_whitelist);

        etGetID = (EditText) findViewById(R.id.etAddWhitelistID);
        btAdd = (Button) findViewById(R.id.btAddWhitelistID);

        addWhitelistTask = new AsyncTaskAddWhitelist();

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean found = false;

                String idd = etGetID.getText().toString();

                // loop through all users
                for (int i = 0; i < StaticData.FULL_DB.length(); i++) {
                    JSONObject c = null;
                    try {
                        c = StaticData.FULL_DB.getJSONObject(i);


                    // Storing each json item in variable

                    String ids = c.getString("id");

                        if(ids.equals(idd)){
                            addWhitelistTask.execute();
                            found = true;
                        }







                    } catch (JSONException e) {
                        //e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Catch JSON exception", Toast.LENGTH_LONG).show();
                    }
                }
                if(!found){
                    Toast.makeText(getApplicationContext(), "No user exists with such ID..", Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskAddWhitelist extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskRegisterU.java";

        // set your json string url here
        String yourJsonStringUrl = "http://" + StaticData.SERVER_IP + "/json_test/insert_id_in_whitelist.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {

            try {

                String MY_PREFS_NAME = "prefs";
                SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
                String myId = prefs.getString("id", "0");

                Log.d("ID-------------", myId);
                // instantiate our json parser
                JsonAddIdInWhitelist jParser = new JsonAddIdInWhitelist();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl, myId, etGetID.getText().toString());

                // get the array of users
                dataJsonArr = json.getJSONArray("test");

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

                    //items.add(new ListViewItemWhitelist(name, data, id, lattitude, longitude, whitelist));
                    //+ ", username: " + username);

                }

            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Catch JSON exception", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {

            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_whitelist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
