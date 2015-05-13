package apps.alvarolamas.openweathermapapi;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import apps.alvarolamas.openweathermapapi.R;
import apps.alvarolamas.openweathermapapi.WeatherDetail;

/**
 * Created by alamas on 7/05/15.
 */
public class CustomDetailsAdapter extends BaseAdapter {

    private static final String TAG ="CustomDetailsAdapter";

    private Context context;
    private List<WeatherDetail> data;

    static class ViewHolder{
        TextView temp_tv, time_tv;
        ImageView icon;

        ViewHolder(TextView temp_tv, TextView time_tv, ImageView icon) {
            this.temp_tv = temp_tv;
            this.time_tv = time_tv;
            this.icon = icon;
        }
    }


    public CustomDetailsAdapter(Context context, List<WeatherDetail> data) {
        this.context = context;
        this.data = data;
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
        TextView temp_tv, time_tv;
        ImageView icon;

        if(convertView==null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.detail_list_item,parent,false);
            temp_tv = (TextView) convertView.findViewById(R.id.temp_tv);
            time_tv = (TextView) convertView.findViewById(R.id.time_tv);
            icon = (ImageView) convertView.findViewById(R.id.detail_icon);
            convertView.setTag(new ViewHolder(temp_tv,time_tv,icon));
        }
        else{
            ViewHolder holder = (ViewHolder) convertView.getTag();
            temp_tv = holder.temp_tv;
            time_tv = holder.time_tv;
            icon = holder.icon;
        }

        DecimalFormat formatter = new DecimalFormat("#0.0");
        WeatherDetail item = data.get(position);
        temp_tv.setText(formatter.format(item.getTemp())+" ºC");
        temp_tv.setTextColor(context.getResources().getColor((getColor(item.getTemp()))));
        time_tv.setText(item.getTime());
        icon.setImageBitmap(item.getBitmap());

        return convertView;
    }

    private int getColor(double temp)
    {
        if(temp < 0)
        {
            return R.color.colorPrimaryDark;
        }
        else if(temp>=0 && temp<10)
        {
            return R.color.colorPrimary;
        }
        else if (temp>=10 && temp<20)
        {
            return R.color.green;
        }
        else if(temp>=20 && temp<30)
            return R.color.orange;
        else
            return R.color.red;
    }
}
