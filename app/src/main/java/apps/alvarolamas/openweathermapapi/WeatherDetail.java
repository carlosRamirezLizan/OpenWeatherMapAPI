package apps.alvarolamas.openweathermapapi;

import android.graphics.Bitmap;

/**
 * Created by alamas on 7/05/15.
 */
public class WeatherDetail {

    private String time;
    private String name;
    private double temp;
    private Bitmap bitmap;

    public WeatherDetail(String name, String time, double temp, Bitmap bitmap) {
        this.time = time;
        this.name = name;
        this.temp = temp-273;
        this.bitmap = bitmap;
    }

    public String getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public double getTemp() {
        return temp;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public String toString() {
        return "WeatherDetails{" +
                "time='" + time + '\'' +
                ", name='" + name + '\'' +
                ", temp=" + temp +
                '}';
    }
}
