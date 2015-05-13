package apps.alvarolamas.openweathermapapi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import apps.alvarolamas.openweathermapapi.activities.MainActivity;

/**
 * Created by alamas on 6/05/15.
 */
public class CustomAdapter extends BaseAdapter {

    static class ViewHolder {
        TextView city_name, temperature, humidity, pressure, wind;
        ImageView icon;

        ViewHolder(TextView city_name, TextView temperature, TextView humidity, TextView pressure, TextView wind, ImageView icon) {
            this.city_name = city_name;
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
            this.wind = wind;
            this.icon = icon;
        }
    }

    private Context context;
    private List<WeatherData> data;


    public CustomAdapter(Context context, List<WeatherData> data) {
        this.context = context;
        this.data = data;

    }

    public void removeViewAt(int index) {

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView city_name, temperature, humidity, pressure, wind;
        ImageView icon;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
            city_name = (TextView) convertView.findViewById(R.id.city_name);
            temperature = (TextView) convertView.findViewById(R.id.temperature);
            humidity = (TextView) convertView.findViewById(R.id.humidity);
            pressure = (TextView) convertView.findViewById(R.id.pressure);
            wind = (TextView) convertView.findViewById(R.id.wind);
            icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(new ViewHolder(city_name, temperature, humidity, pressure, wind, icon));
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            city_name = holder.city_name;
            temperature = holder.temperature;
            humidity = holder.humidity;
            pressure = holder.pressure;
            wind = holder.wind;
            icon = holder.icon;
        }

        //Set Values
        WeatherData item = data.get(position);

        city_name.setText(item.getCity_name());
        DecimalFormat formatter = new DecimalFormat("#0.0");
        temperature.setText(formatter.format(item.getTemperature()) + " ºC");
        humidity.setText(Double.toString(item.getHumidity()) + " %");
        pressure.setText(Double.toString(item.getPressure()) + " hpa");
        wind.setText(Double.toString(item.getWind_speed()) + " m/s\n(" + item.getWind_direction() + ")");
        icon.setImageBitmap(item.getIcon());

        return convertView;
    }
}