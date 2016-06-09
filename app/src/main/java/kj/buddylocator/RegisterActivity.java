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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import kj.buddylocator.StaticData.StaticData;
import kj.buddylocator.jsonparser.JsonRegisterUser;

public class RegisterActivity extends ActionBarActivity {

    String currentDateandTime;
    EditText etName;

    AsyncTaskRegisterUser registerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView tv = (TextView) findViewById(R.id.tvRegID);
        etName = (EditText) findViewById(R.id.editText);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        currentDateandTime = sdf.format(new Date());

        registerTask = new AsyncTaskRegisterUser();

        tv.setText(currentDateandTime);

        Button btRegister = (Button) findViewById(R.id.btRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((!etName.getText().toString().equals(null)) && (!etName.getText().toString().equals(""))){
                    Log.d("Requesting REGISTER", "Called async task..........................................");
                    registerTask.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // you can make this class as another java file so it will be separated from your main activity.
    public class AsyncTaskRegisterUser extends AsyncTask<String, String, String> {

        final String TAG = "AsyncTaskRegisterU.java";

        // set your json string url here
        String yourJsonStringUrl = "http://" + StaticData.SERVER_IP + "/json_test/register.php";

        // contacts JSONArray
        JSONArray dataJsonArr = null;

        @Override
        protected void onPreExecute() {}

        @Override
        protected String doInBackground(String... arg0) {

            try {

                // instantiate our json parser
                JsonRegisterUser jParser = new JsonRegisterUser();

                // get json string from url
                JSONObject json = jParser.getJSONFromUrl(yourJsonStringUrl, etName.getText().toString(), currentDateandTime);

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
                Toast.makeText(getApplicationContext(), "JSON Exception", Toast.LENGTH_LONG).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String strFromDoInBg) {

            String MY_PREFS_NAME = "prefs";

            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("registered", "yes");
            editor.putString("id", currentDateandTime);
            editor.commit();

            finish();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
