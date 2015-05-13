package apps.alvarolamas.openweathermapapi.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

import apps.alvarolamas.openweathermapapi.CustomDetailsAdapter;
import apps.alvarolamas.openweathermapapi.R;
import apps.alvarolamas.openweathermapapi.WeatherDetail;

public class WeatherDetailsActivity extends Activity implements OnMapReadyCallback {

    //Logging tag
    private static final String TAG = "WeatherDetail";

    //Bearing constants, Map orientation
    private static final int DIRECTION_NORTH = 0;
    private static final int DIRECTION_EAST = 90;
    private static final int DIRECTION_SOUTH = 180;
    private static final int DIRECTION_WEST = 270;

    private double lat, lon;
    private String city;
    private List<WeatherDetail> details;

    private RelativeLayout progressBar;
    private GoogleMap map;
    private CustomDetailsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        getWindow().setAllowEnterTransitionOverlap(true);

        //Gettin' extras
        Bundle extras = getIntent().getExtras();
        city = extras.getString(MainActivity.CITY);
        setTitle(city);
        int id = extras.getInt(MainActivity.ID);
        lat = extras.getDouble(MainActivity.LAT);
        lon = extras.getDouble(MainActivity.LNG);

        ListView details_list = (ListView)findViewById(R.id.detail_listView);
        details = new ArrayList<>();
        adapter = new CustomDetailsAdapter(WeatherDetailsActivity.this,details);
        details_list.setAdapter(adapter);

        progressBar = (RelativeLayout) findViewById(R.id.progressBarLayout1);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        new GetWeatherDetails().execute(id);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_weather_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_centerMap) {
            if(map==null)
            {
                Toast.makeText(this,"Map not loaded yet",Toast.LENGTH_SHORT).show();
                return true;
            }
            //latlng, zoom, tilt, bearing
            CameraPosition pos = new CameraPosition(new LatLng(lat,lon),5,DIRECTION_NORTH,1);
            map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
            Toast.makeText(this,"Centering map in " + city,Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //latlng, zoom, tilt, bearing
        CameraPosition pos = new CameraPosition(new LatLng(lat,lon),5,DIRECTION_NORTH,1);
        map.animateCamera(CameraUpdateFactory.newCameraPosition(pos));
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(city));
        //addMarker returns a marker, it can be used to:
        //marker.showInfoWindow();
    }

    private class GetWeatherDetails extends AsyncTask<Integer,Void,Void>
    {
        @Override
        protected Void doInBackground(Integer... params) {
            int id = params[0];
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?id="+id);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                br.close();
                String response = sb.toString();

                JSONArray jsonArray = new JSONObject(response).getJSONArray("list");
                for(int i=0; i<16;i++)
                {
                    JSONObject detail = jsonArray.getJSONObject(i);
                    String time = detail.getString("dt_txt");
                    Double temp = detail.getJSONObject("main").getDouble("temp");
                    String icon_name = detail.getJSONArray("weather").getJSONObject(0).getString("icon");
                    URL icon_url = new URL("http://openweathermap.org/img/w/"+icon_name+".png");
                    Bitmap icon = BitmapFactory.decodeStream(icon_url.openConnection().getInputStream());
                    details.add(new WeatherDetail(city,time,temp,icon));
                    Log.i(TAG, details.get(i).toString());

                    publishProgress();
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            adapter.notifyDataSetChanged();
            if(!details.isEmpty())
            {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
