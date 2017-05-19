package practicaltest02var03.eim;

/**
 * Created by student on 19.05.2017.
 */

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class CommunicationThread extends Thread {

    private ServerThread serverThread;
    private Socket socket;
    private HashMap<String, String> cache;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = Utilities.getReader(socket);
            PrintWriter writer = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Started...");

            String word = reader.readLine();

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://services.aonaware.com/DictService/DictService.asmx/Define?word=" + word);
            ResponseHandler responseHandler = new BasicResponseHandler();
            String response = httpClient.execute(httpGet, responseHandler).toString();

//            JSONObject root = new JSONObject(response);
//            JSONArray array = root.getJSONArray("RESULTS");
//            String toClient = "";
//            int len = array.length() < 8 ? array.length() : 8;
//            for (int k = 0; k < len; k++) {
//                JSONObject entry = array.getJSONObject(k);
//                toClient += entry.getString("name") + "|";
//            }

            this.cache.put(word, response);
            writer.println(response);

            socket.close();
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Ended...");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
