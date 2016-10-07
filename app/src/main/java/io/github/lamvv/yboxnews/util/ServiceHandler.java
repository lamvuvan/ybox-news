package io.github.lamvv.yboxnews.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by lamvu on 7/9/2016.
 */
public class ServiceHandler {

    public static String serviceHandler(String url) {

        StringBuilder json = new StringBuilder();
        try {
            // create a url object
            URL myUrl = new URL(url);
            // create a urlconnection object
            URLConnection urlConnection = myUrl.openConnection();
            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;
            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                json.append(line + "\n");
            }
            bufferedReader.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
