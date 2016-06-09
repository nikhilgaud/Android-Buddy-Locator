package kj.buddylocator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kj.buddylocator.StaticData.StaticData;
import kj.buddylocator.jsonparser.JsonCheckin;

/**
 * Created by Kapil on 5/14/2015.
 */
public class LocationService extends Service
{
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;

    //AsyncTaskCheckinFromService checkinService;

    Intent intent;
    Context ourContext;
    int counter = 0;

    @Override
    public void onCreate()
    {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);
        //checkinService = new AsyncTaskCheckinFromService();
        ourContext = getApplicationContext();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskCheckinFromService extends AsyncTask<String, String, String> {

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
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl, myId, "" + StaticData.LOC.getLatitude(), "" + StaticData.LOC.getLongitude());

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

            //finish();
            ShowToastInIntentService("Pushed your new location on server !!\nLattitude: " +
                    StaticData.LOC.getLatitude() + "\nLongitude: " + StaticData.LOC.getLongitude());

        }
    }

    public boolean CheckInternet(Context ctx) {

        try{
            ConnectivityManager connec = (ConnectivityManager) ctx
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            // Check if wifi or mobile network is available or not. If any of them is
            // available or connected then it will return true, otherwise false;
            return wifi.isConnected() || mobile.isConnected();
        } catch(Exception e){
            ShowToastInIntentService("No Internet connection");
        }
        return false;
    }


    public void ShowToastInIntentService(final String sText)
    {  final Context myContext = this;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast toast1 = Toast.makeText(myContext, sText, Toast.LENGTH_SHORT);
                toast1.show();
            }
        });
    }

    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.i("**********", "#######################################################" +
                    "\n#######################################################################" +
                    "\n#######################################################################" +
                    "\n#######################################################################" +
                    " Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {

                StaticData.LOC = loc;
                //ShowToastInIntentService("Lattitude: "+loc.getLatitude()+"\nLongitude: " + loc.getLongitude());

                if(CheckInternet(ourContext)){
                    new AsyncTaskCheckinFromService().execute();
                }


                //loc.getLatitude();
                //loc.getLongitude();
                //intent.putExtra("Latitude", loc.getLatitude());
                //intent.putExtra("Longitude", loc.getLongitude());
                //intent.putExtra("Provider", loc.getProvider());
                //sendBroadcast(intent);



            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }

    }
}
