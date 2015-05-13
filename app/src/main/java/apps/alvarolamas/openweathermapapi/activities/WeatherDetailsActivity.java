package apps.alvarolamas.openweathermapapi.activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
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
import android.view.Window;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import apps.alvarolamas.openweathermapapi.CustomDetailsAdapter;
import apps.alvarolamas.openweathermapapi.R;
import apps.alvarolamas.openweathermapapi.WeatherDetail;

public class WeatherDetailsActivity extends Activity implements OnMapReadyCallback {

    //Logging tag
    private static final String TAG = "WeatherDetail";

    //Bearing constants
    private static final int DIRECTION_NORTH = 0;
    private static final int DIRECTION_EAST = 90;
    private static final int DIRECTION_SOUTH = 180;
    private static final int DIRECTION_WEST = 270;

    private double lat, lon;
    private String city;
    private List<WeatherDetail> details;

    private RelativeLayout progressBar;
    private GoogleMap map;
    private ListView details_list;
    private CustomDetailsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_details);
        getWindow().setAllowEnterTransitionOverlap(true);

        //Gettin' extras
        Bundle extras = getIntent().getExtras();
        city = extras.getString("city");
        setTitle(city);
        int id = extras.getInt("id");
        lat = extras.getDouble("lat");
        lon = extras.getDouble("lon");

        details_list = (ListView)findViewById(R.id.detail_listView);
        details = new ArrayList<>();
        adapter = new CustomDetailsAdapter(WeatherDetailsActivity.this,details);
        details_list.setAdapter(adapter);

        ((TextView) findViewById(R.id.detail_tv)).setText(city);

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
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(city));
        //marker.showInfoWindow();
    }

    private class GetWeatherDetails extends AsyncTask<Integer,Void,Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Integer... params) {
            int id = params[0];
            try {
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet("http://api.openweathermap.org/data/2.5/forecast?id="+id);
                Log.d(TAG,"Response retrieved");
                HttpEntity httpResponse = client.execute(get).getEntity();
                String response = EntityUtils.toString(httpResponse);
                Log.d(TAG,"Response retrieved");

                JSONArray jsonArray = new JSONObject(response).getJSONArray("list");
                for(int i=0; i<16;i++)
                {
                    JSONObject detail = jsonArray.getJSONObject(i);
                    String time = detail.getString("dt_txt");
                    Double temp = detail.getJSONObject("main").getDouble("temp");
                    String icon_name = detail.getJSONArray("weather").getJSONObject(0).getString("icon");
                    URL url = new URL("http://openweathermap.org/img/w/"+icon_name+".png");
                    Bitmap icon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    details.add(new WeatherDetail(city,time,temp,icon));
                    Log.i(TAG, details.get(i).toString());
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            if(!details.isEmpty())
            {
                progressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}
