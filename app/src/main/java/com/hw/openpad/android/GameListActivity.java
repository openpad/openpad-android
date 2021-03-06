package com.hw.openpad.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.hw.openpad.android.model.GameAdapter;
import com.hw.openpad.android.model.NetworkManager;

import java.util.ArrayList;


public class GameListActivity extends Activity implements NetworkManager.ServerDiscoveryListener {
    private GameAdapter mGameAdapter;
    private ListView mListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        mListView = (ListView) findViewById(R.id.game_list);
        this.mGameAdapter = new GameAdapter(this, 0);
        mListView.setAdapter(mGameAdapter);
        NetworkManager.setShouldDiscover(true);
        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkManager.disconnectAll();
        NetworkManager.setShouldDiscover(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_list, menu);
        return true;
    }


    @Override
    protected void onPause() {
        super.onPause();
        NetworkManager.setShouldDiscover(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        NetworkManager.setShouldDiscover(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.action_refresh)
            NetworkManager.refreshConnections();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setGames(ArrayList<GameConnection> games) {
        mGameAdapter.setList(games);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGameAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void joinGame() {
        Intent intent = new Intent(this, ControllerActivity.class);
        startActivity(intent);
    }

    public void refresh(MenuItem item) {
        refresh();
    }

    private void refresh() {
        NetworkManager.findServers(this);
    }

}
