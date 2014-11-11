package com.hw.openpad.android.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

    public void setList(ArrayList<GameConnection> list){
        games = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.game_cell, parent, false);
        final GameConnection gameConnection = games.get(position);

        TextView gameName = (TextView) rowView.findViewById(R.id.game_name);
        TextView gameDesc = (TextView) rowView.findViewById(R.id.game_desc);
        ImageView gameImage = (ImageView) rowView.findViewById((R.id.game_image));

        gameName.setText(gameConnection.mName);
        gameDesc.setText(gameConnection.mDescription);

        byte[] decodedString = Base64.decode(gameConnection.mIcon.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        gameImage.setImageBitmap(decodedByte); // TODO make images work

        rowView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gameConnection.joinGame();
                return true;
            }
        });

        return rowView;
    }
}
