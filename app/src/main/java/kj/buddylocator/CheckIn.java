package kj.buddylocator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kj.buddylocator.R;
import kj.buddylocator.StaticData.StaticData;
import kj.buddylocator.jsonparser.JsonCheckin;
import kj.buddylocator.jsonparser.JsonRegisterUser;

public class CheckIn extends ActionBarActivity implements LocationListener{

    TextView tvLat, tvLong;
    Button btCheckin, btStart, btStop;
    ProgressBar pb;

    protected LocationManager locationManager;

    AsyncTaskCheckin checkinTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);

        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLong = (TextView) findViewById(R.id.tvLon);
        btCheckin = (Button) findViewById(R.id.btCheckin);
        btStart = (Button) findViewById(R.id.btCheckinStart);
        btStop = (Button) findViewById(R.id.btCheckinStop);
        pb = (ProgressBar) findViewById(R.id.progressBar);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        checkinTask = new AsyncTaskCheckin();

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Service Started", Toast.LENGTH_SHORT).show();
                startService(new Intent(getBaseContext(), LocationService.class));
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
                stopService(new Intent(getBaseContext(), LocationService.class));
            }
        });

        btCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkinTask.execute();
            }
        });
    }

    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskCheckin extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskRegisterU.java";

        // set your json string url here
        String yourJsonStringUrl = "http://" + StaticData.SERVER_IP + "/json_test/set_location.php";

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
                JsonCheckin jParser = new JsonCheckin();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl, myId, tvLat.getText().toString(), tvLong.getText().toString());

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
        getMenuInflater().inflate(R.menu.menu_check_in, menu);
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

    @Override
    public void onLocationChanged(Location location) {
        tvLat.setText(location.getLatitude()+"");
        tvLong.setText(location.getLongitude()+"");
        pb.setVisibility(View.INVISIBLE);
        btCheckin.setEnabled(true);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
