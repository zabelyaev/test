package com.example.roman.drivers;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.roman.drivers.HttpHandler.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

    public class MainActivity extends AppCompatActivity {
        private String TAG = MainActivity.class.getSimpleName();
        private ListView lv;

        ArrayList<HashMap<String, String>> fullBidDriversList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            fullBidDriversList = new ArrayList<>();
            lv = (ListView) findViewById(R.id.list);

            new GetBid().execute();
        }
        private class GetBid extends AsyncTask<Void, Void, Void> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Toast.makeText(MainActivity.this, "Подождите, данные загружаются...", Toast.LENGTH_LONG).show();

            }

            @Override
            protected Void doInBackground(Void... arg0) {
                HttpHandler sh = new HttpHandler();
                // Making a request to url and getting response
                String url = "http://auto-park.mywebcommunity.org/php/get.php";
                String jsonStr = sh.makeServiceCall(url);

                Log.e(TAG, "Response from url: " + jsonStr);
                if (jsonStr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(jsonStr);

                        // Getting JSON Array node
                        JSONArray fullBidDrivers = jsonObj.getJSONArray("bid");

                        // looping through All Contacts
                        for (int i = 0; i < fullBidDrivers.length(); i++) {
                            JSONObject c = fullBidDrivers.getJSONObject(i);
                            //String id = c.getString("id");
                            //String driver = c.getString("driver");
                            //String car = c.getString("car");
                            String type_bid = c.getString("type_bid");
                            String address_start = c.getString("address_start");
                            String address_finish = c.getString("address_finish");
                            String comment = c.getString("comment");
                            //String status = c.getString("status");

                            // Phone node is JSON Object
                            //JSONObject phone = c.getJSONObject("phone");
                            // String mobile = phone.getString("mobile");
                            // String home = phone.getString("home");
                            //String office = phone.getString("office");

                            // tmp hash map for single contact
                            HashMap<String, String> fullBidDriversHash = new HashMap<>();

                            // adding each child node to HashMap key => value
                            //fullBidDriversHash.put("id", id);
                            //fullBidDriversHash.put("driver", driver);
                            //fullBidDriversHash.put("car", car);
                            fullBidDriversHash.put("type_bid", type_bid);
                            fullBidDriversHash.put("address_start", address_start);
                            fullBidDriversHash.put("address_finish", address_finish);
                            fullBidDriversHash.put("comment", comment);
                            //fullBidDriversHash.put("status", status);


                            // adding contact to contact list
                            fullBidDriversList.add(fullBidDriversHash);
                        }
                    } catch (final JSONException e) {
                        Log.e(TAG, "Ошибка Json парсера: " + e.getMessage());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Ошибка Json парсера: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }

                } else {
                    Log.e(TAG, "Невозможно получить json с сервера.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Невозможно получить json с сервера. Проверьте logCat для устранения ошибки!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                ListAdapter adapter = new SimpleAdapter(MainActivity.this, fullBidDriversList,
                        R.layout.drivers_list_item, new String[]{"type_bid","address_start","address_finish","comment"},
                        new int[]{R.id.type_bid,R.id.address_start,R.id.address_finish,R.id.comment});
                lv.setAdapter(adapter);
            }
        }

//menu

}
