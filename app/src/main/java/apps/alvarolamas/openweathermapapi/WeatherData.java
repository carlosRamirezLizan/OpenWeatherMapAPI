package apps.alvarolamas.openweathermapapi;

import android.graphics.Bitmap;

/**
 * Created by alamas on 4/05/15.
 */
public class WeatherData {

    private String city_name;
    private int id;
    private double lon;
    private double lat;
    private double temperature;
    private double wind_speed;
    private double wind_direction;
    private double humidity;
    private double pressure;
    private Bitmap icon;

    public WeatherData(String city_name, int id, double lon, double lat,
                       double temperature, double wind_speed, double wind_direction,
                       double humidity, double pressure, Bitmap icon) {
        this.city_name = city_name;
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.temperature = temperature - 273;
        this.wind_speed = wind_speed;
        this.wind_direction = wind_direction;
        this.humidity = humidity;
        this.pressure = pressure;
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "pressure=" + pressure +
                ", humidity=" + humidity +
                ", wind_direction=" + wind_direction +
                ", wind_speed=" + wind_speed +
                ", temperature=" + temperature +
                ", lat=" + lat +
                ", lon=" + lon +
                ", id=" + id +
                ", city_name='" + city_name + '\'' +
                '}';
    }

    public String getCity_name() {
        return city_name;
    }

    public int getId() {
        return id;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public String getWind_direction() {
        if(wind_direction<=10 || wind_direction>=350)
            return "E";
        else if(wind_direction>10 && wind_direction<80)
            return "NE";
        else if(wind_direction>=80 && wind_direction<=100)
            return "N";
        else if(wind_direction>100 && wind_direction<170)
            return "NW";
        else if(wind_direction>=170 && wind_direction<=190)
            return "W";
        else if(wind_direction>190 && wind_direction<260)
            return "SW";
        else if(wind_direction>=260 && wind_direction<=280)
            return "S";
        else if(wind_direction>280 && wind_direction<350)
            return "SE";

        return "unknown";
    }

    public double getHumidity() {
        return humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public Bitmap getIcon() {
        return icon;
    }


}
