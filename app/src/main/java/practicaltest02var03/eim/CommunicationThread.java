package practicaltest02var03.eim;

/**
 * Created by student on 19.05.2017.
 */

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

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
            String response = "";

            if (serverThread.getData().containsKey(word)) {
                response = serverThread.getData().get(word);

            } else {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://services.aonaware.com/DictService/DictService.asmx/Define?word=" + word);
                ResponseHandler responseHandler = new BasicResponseHandler();
                response = httpClient.execute(httpGet, responseHandler).toString();

                Document doc = Jsoup.parse(response, " ", Parser.xmlParser());
                int nr = 0;
                for (Element e : doc.select("WordDefinition")) {
                    if (nr == 1) {
                        response = e.text();
                        nr++;
                        serverThread.setData(word, response);
                        break;
                    } else {
                        nr++;
                    }
                }
            }


//            this.cache.put(word, response);
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
