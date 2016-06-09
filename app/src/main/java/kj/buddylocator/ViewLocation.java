package kj.buddylocator;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewLocation extends ActionBarActivity {

    TextView tvName, tvLat, tvLon;

    Button btViewOnMap;

    String lati;
    String longi;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        tvLat = (TextView) findViewById(R.id.tvLat);
        tvLon = (TextView) findViewById(R.id.tvLon);
        tvName = (TextView) findViewById(R.id.tvName);

        btViewOnMap = (Button) findViewById(R.id.btViewOnMap);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            lati = extras.getString("latitude");
            longi = extras.getString("longitude");
            name = extras.getString("name");

            tvName.setText(name);
            tvLat.setText(lati);
            tvLon.setText(longi);
        }

        btViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent i = new Intent(ViewLocation.this, MapsActivity.class);
                i.putExtra("latitude", lati);
                i.putExtra("longitude",longi);
                i.putExtra("name",name);
                startActivity(i);*/

                if(isGoogleMapInstalled()){
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("geo:0,0?q=" + (lati + ", " + longi)));
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Please install Google Maps app from Play Store", Toast.LENGTH_LONG).show();
                    }
                } else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage("Install Google Maps");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Install", (DialogInterface.OnClickListener) getGoogleMapsListener());
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }



                /*
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            LAT = extras.getString("latitude");
            LONGI = extras.getString("longitude");
            NAME = extras.getString("name");
        }*/

            }
        });

        if(lati.equals(null) || lati.equals("")){
            btViewOnMap.setEnabled(false);
        }

    }

    public View.OnClickListener getGoogleMapsListener()
    {
        return new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);
            }

        };
    }

    private boolean isGoogleMapInstalled() {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_location, menu);
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
