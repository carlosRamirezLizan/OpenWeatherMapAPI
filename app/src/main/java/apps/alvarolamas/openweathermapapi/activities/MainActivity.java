package apps.alvarolamas.openweathermapapi.activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import apps.alvarolamas.openweathermapapi.CustomAdapter;
import apps.alvarolamas.openweathermapapi.R;
import apps.alvarolamas.openweathermapapi.WeatherData;


public class MainActivity extends Activity {

    //Logging tag
    private static final String TAG = "MainActivity";

    //EXTRAS Tags
    public static final String CITY = "city";
    public static final String ID = "id";
    public static final String LAT = "lat";
    public static final String LNG = "lng";

    private List<WeatherData> weatherData;

    private RelativeLayout progressBar;
    private CustomAdapter adapter;
    private ListView list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (RelativeLayout) findViewById(R.id.progressBarLayout);
        list = (ListView) findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WeatherData data = (WeatherData) adapter.getItem(position);
                Intent details = new Intent(getApplicationContext(),WeatherDetailsActivity.class);
                details.putExtra(CITY,data.getCity_name());
                details.putExtra(ID,data.getId());
                details.putExtra(LAT, data.getLat());
                details.putExtra(LNG, data.getLon());
                startActivity(details,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
            }
        });

        //Set empty adapter, to be filled by AsyncTask
        weatherData = new ArrayList<>();

        //Check connectivity
        ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetoworkInfo = cm.getActiveNetworkInfo();
        if(activeNetoworkInfo != null && activeNetoworkInfo.isConnected()) {
            loadWeather();
        }
        else
        {
            TextView prog_tv = (TextView) findViewById(R.id.progressBar_tv);
            prog_tv.setText("Se necesita conexión a Internet");
            findViewById(R.id.progressBar).setVisibility(View.GONE);
        }

    }

    private void loadWeather()
    {
        String [] cities = getResources().getStringArray(R.array.cities);
        new GetWeatherData().execute(cities);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_help:
                Dialog dialog = new Dialog(this, android.R.style.Theme_Holo_Light_Dialog);
                dialog.setTitle("Help");
                dialog.setContentView(R.layout.help_item);
                dialog.show();
                return true;

            case R.id.action_refresh:
                weatherData.clear();
                progressBar.setVisibility(View.VISIBLE);

                loadWeather();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetWeatherData extends AsyncTask<String,Void,Void>
    {
        private String defaultUrl = "http://api.openweathermap.org/data/2.5/weather?q=";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            String city="";
            try {
                for (int i=0;i<params.length;i++) {

                    city = params[i];
                    URL url = new URL(defaultUrl + city);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000); //ms
                    conn.setConnectTimeout(15000); //ms
                    conn.setRequestMethod("GET");

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line+"\n");
                    }
                    br.close();
                    String response = sb.toString();

                    //Parse Response and add to the list
                    JSONObject jsonObj = new JSONObject(response);
                    String city_name = jsonObj.getString("name");
                    int id = jsonObj.getInt("id");

                    boolean alreadyStored = false;
                    for (int j = 0; i < weatherData.size(); i++) {
                        //Do not restore this item again
                        if (weatherData.get(j).getId() == id) {
                            alreadyStored = true;
                        }
                    }

                    if(!alreadyStored)
                    {
                        //coord child
                        JSONObject jsonChild = jsonObj.getJSONObject("coord");
                        double lon = jsonChild.getDouble("lon");
                        double lat = jsonChild.getDouble("lat");

                        //main child
                        jsonChild = jsonObj.getJSONObject("main");
                        double current_t = jsonChild.getDouble("temp");
                        double humidity = jsonChild.getDouble("humidity");
                        double pressure = jsonChild.getDouble("pressure");

                        //wind child
                        jsonChild = jsonObj.getJSONObject("wind");
                        double wind_speed = jsonChild.getDouble("speed");
                        double wind_direction = jsonChild.getDouble("deg");

                        JSONArray jsonArrayWeather = jsonObj.getJSONArray("weather");
                        JSONObject jsonWeather = jsonArrayWeather.getJSONObject(0);
                        String icon_name = jsonWeather.getString("icon");

                        URL icon_url = new URL("http://openweathermap.org/img/w/" + icon_name + ".png");
                        Bitmap icon = BitmapFactory.decodeStream(icon_url.openConnection().getInputStream());

                        WeatherData data_item = new WeatherData(city_name, id, lon, lat,
                                current_t, wind_speed, wind_direction,
                                humidity, pressure, icon);
                        Log.d(TAG, data_item.toString());
                        weatherData.add(data_item);

                        publishProgress();
                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new CustomAdapter(getApplicationContext(),weatherData);
                        list.setAdapter(adapter);
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG,"Error retrieving:  "+ city.toUpperCase());
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if(!weatherData.isEmpty()){
                progressBar.animate()
                        .translationY(300.0f)
                        .alpha(0.0f)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                progressBar.setVisibility(View.INVISIBLE);
                                //Return to its original position and alpha
                                progressBar.setAlpha(1.0f);
                                progressBar.setY(progressBar.getY()-300.0f);
                            }
                            @Override
                            public void onAnimationStart(Animator animation) {}
                            @Override
                            public void onAnimationCancel(Animator animation) {}
                            @Override
                            public void onAnimationRepeat(Animator animation) {}
                        });
            }
        }
    }

}
