package com.hw.openpad.android.model;

import android.os.AsyncTask;
import android.util.Log;

import com.hw.openpad.android.GameConnection;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by ethan on 10/27/14.
 */

public class NetworkManager {
    static final int start_port = 47810, num_ports = 10;

    static ServerDiscoveryListener listener;
    static HashSet<String> hosts = new HashSet<String>();
    static ArrayList<GameConnection> games = new ArrayList<GameConnection>();
    public static GameConnection joinedGame;
    static boolean isInGame = false, shouldDiscover;

    public static void findServers(ServerDiscoveryListener listener) {
        NetworkManager.listener = listener;
        new DiscoverTask().execute();
    }

    public static void didJoin(GameConnection gameConnection) {
        joinedGame = gameConnection;
        listener.joinGame();
    }

    public static void setShouldDiscover(boolean bool) {
        shouldDiscover = bool;
    }

    static class DiscoverTask extends AsyncTask<Void, Void, Void> {

        public DiscoverTask(){

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                final DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                byte[] data = new byte['a'];
                DatagramPacket packet = new DatagramPacket(data, data.length,
                        InetAddress.getByName("255.255.255.255"), 9999);

                for (int i = 0; i < 10; i++) {
                    socket.send(packet);
                }

                while (shouldDiscover) {
                    socket.receive(packet);
                    if (packet.getPort() == 9999) {
                        data = packet.getData();
                        int port = (data[0] & 0xFF) * 256 + (data[1] & 0xFF);

                        String addr = packet.getAddress().toString() + ":" + port;
                        if(!hosts.contains(addr)) {
                            new ConnectTask().execute(packet.getAddress(), port);
                            hosts.add(addr);
                        }
                        Log.d("PlayPhone", "Found a server at: " + addr);
                    }
                    break;
                }
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    static class ConnectTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {
            try {
                InetAddress ip = (InetAddress) params[0];
                Integer port = (Integer) params[1];
                GameConnection gc = new GameConnection(ip, port);
                gc.sendDiscovery();
                gc.listen();
            } catch (Exception e) {
               e.printStackTrace();
            }
            return null;
        }

    }

    public static void addConnection(GameConnection gc) {
        games.add(gc);
        listener.setGames(games);
    }

    public static void removeConnection(GameConnection gc) {
        games.remove(gc);
    }

    public static void disconnectAll() {
        for (GameConnection gc : games) {
            removeConnection(gc);
            gc.disconnect();
        }
    }

    public static void refreshConnections() {
        listener.setGames(games);
    }

    public static interface ServerDiscoveryListener {
        public void setGames(ArrayList<GameConnection> games);
        public void joinGame();
    }
}
