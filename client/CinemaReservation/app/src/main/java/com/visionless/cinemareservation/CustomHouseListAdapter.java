package com.visionless.cinemareservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.visionless.cinemareservation.model.Saloon;

import java.util.ArrayList;

public class CustomHouseListAdapter extends ArrayAdapter<Saloon> {

    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    private final ArrayList<Saloon> saloons;

    public CustomHouseListAdapter(Context context, ArrayList<Saloon> persons) {
        super(context,0, persons);
        this.context = context;
        this.saloons = persons;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return saloons.size();
    }

    @Override
    public Saloon getItem(int position) {
        return saloons.get(position);
    }

    @Override
    public long getItemId(int position) {
        return saloons.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.saloon_list_item, null);

            holder = new ViewHolder();
            holder.houseNameLabel = (TextView) convertView.findViewById(R.id.house_name_label);
            holder.houseLocationLabel = (TextView) convertView.findViewById(R.id.house_location_label);
            convertView.setTag(holder);

        }
        else{
            //Get viewholder we already created
            holder = (ViewHolder)convertView.getTag();
        }

        Saloon saloon = saloons.get(position);
        if(saloon != null){
            holder.houseNameLabel.setText(saloon.getName());
            holder.houseLocationLabel.setText(saloon.getLocation());

        }
        return convertView;
    }

    //View Holder Pattern for better performance
    private static class ViewHolder {
        TextView houseNameLabel;
        TextView houseLocationLabel;
    }
}