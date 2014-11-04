package com.hw.openpad.android.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hw.openpad.android.GameConnection;
import com.hw.openpad.android.R;

import java.util.ArrayList;

/**
 * Created by ethan on 11/3/14.
 */
public class GameAdapter extends ArrayAdapter<GameConnection> {

    public static GameAdapter instance;
    public ArrayList<GameConnection> games = new ArrayList<GameConnection>();
    private Context context;

    public GameAdapter(Context context, int resource) {
        super(context, resource);
        this.context = context;
        instance = this;
    }

    @Override
    public int getCount() {
        return games.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.game_cell, parent, false);
        GameConnection gameConnection = games.get(position);
        TextView gameName = (TextView) rowView.findViewById(R.id.game_name);
        gameName.setText(gameConnection.name);
        TextView gameDesc = (TextView) rowView.findViewById(R.id.game_desc);
        return convertView;
    }
}
