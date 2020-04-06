package com.wertycraft.bot;

import org.json.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MCStatus {

    public String server;
    public int port;

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    private JSONObject json;

    public MCStatus(String server, int port) {
        try {
            json = readJsonFromUrl("https://api.mcsrvstat.us/2/" + server + ":" + port);
        } catch (IOException | JSONException e) {
            System.out.println(e.getMessage());
        }

        this.server = server;
        this.port = port;
    }

    public boolean getOnline() {
        return json.getBoolean("online");
    }

    public String getMotd() {
        if (getOnline()) {
            JSONArray jsA = json.getJSONObject("motd").getJSONArray("clean");
            StringBuilder motd = new StringBuilder();
            for (int i = 0; i < jsA.length(); i++) {
                motd.append(jsA.toList().get(i)).append("\n");
            }
            return motd.toString();
        } else return null;
    }

    public long getLatency() {
        try {
            Socket clientSocket = new Socket();
            long startTime = System.currentTimeMillis();
            clientSocket.connect(new InetSocketAddress(server, port), WertyBot.config.getInt("timeout"));
            long ping = System.currentTimeMillis() - startTime;
            clientSocket.close();
            return ping;
        } catch (IOException e) {
            return -1;
        }
    }

    public List<String> getPlayers() {
        try {
            if (getOnline()) {
                JSONArray arr = json.getJSONObject("players").getJSONArray("list");
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < arr.length(); i++) {
                    list.add(arr.getString(i));
                }
                return list;
            } else return null;
        } catch (JSONException e) {
            return null;
        }
    }

    public int getCurrentPlayers() {
        if (getOnline()) {
            return json.getJSONObject("players").getInt("online");
        } else return 0;
    }

    public int getMaxPlayers() {
        if (getOnline()) {
            return json.getJSONObject("players").getInt("max");
        } else return 0;
    }

    public String getVersion() {
        if (getOnline()) {
            return json.getString("version");
        } else return "Unknown";
    }

}
