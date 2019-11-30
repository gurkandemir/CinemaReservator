package com.visionless.cinemareservation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.visionless.cinemareservation.model.Ticket;

import java.util.ArrayList;

public class CustomTicketListAdapter extends ArrayAdapter<Ticket> {

    private final LayoutInflater inflater;
    private final Context context;
    private ViewHolder holder;
    private final ArrayList<Ticket> tickets;

    public CustomTicketListAdapter(Context context, ArrayList<Ticket> tickets) {
        super(context,0, tickets);
        this.context = context;
        this.tickets = tickets;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return tickets.size();
    }

    @Override
    public Ticket getItem(int position) {
        return tickets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return tickets.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.ticket_list_item, null);

            holder = new ViewHolder();
            holder.seatNameLabel = (TextView) convertView.findViewById(R.id.seat_name_label);
            holder.movieNameLabel = (TextView) convertView.findViewById(R.id.movie_name_label);
            holder.houseNameLabel = (TextView) convertView.findViewById(R.id.house_name_label);
            holder.timeNameLabel = (TextView) convertView.findViewById(R.id.time_name_label);
            holder.dateNameLabel = (TextView) convertView.findViewById(R.id.date_name_label);
            convertView.setTag(holder);

        }
        else{
            //Get viewholder we already created
            holder = (ViewHolder)convertView.getTag();
        }

        Ticket ticket = tickets.get(position);
        if(ticket != null){
            holder.seatNameLabel.setText(""+ticket.getSeat());
            holder.movieNameLabel.setText(ticket.getFilm());
            holder.houseNameLabel.setText(ticket.getSaloon());
            holder.timeNameLabel.setText(ticket.getTime());
            holder.dateNameLabel.setText(ticket.getDate());
        }
        return convertView;
    }

    //View Holder Pattern for better performance
    private static class ViewHolder {
        TextView seatNameLabel;
        TextView movieNameLabel;
        TextView houseNameLabel;
        TextView timeNameLabel;
        TextView dateNameLabel;
    }
}