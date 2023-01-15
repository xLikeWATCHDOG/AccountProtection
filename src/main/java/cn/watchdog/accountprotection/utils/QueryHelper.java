package cn.watchdog.accountprotection.utils;

import com.google.gson.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

public class QueryHelper {
    public static String get(String urlString, String token) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setReadTimeout(5 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("token", token);
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                StringBuilder builder = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                for (String s = br.readLine(); s != null; s = br.readLine()) {
                    builder.append(s);
                }
                br.close();
                return builder.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> queryIP(String ip) {
        String url = "https://api.ip138.com/ip/?ip=" + ip + "&datatype=jsonp";
        String token = "f81a3214ac4e4b6c312c769ca0ad22f5";
        JsonObject jsonObject = new JsonParser().parse(Objects.requireNonNull(get(url, token))).getAsJsonObject();
        JsonArray jsonArray = jsonObject.getAsJsonArray("data");
        List<String> ret = new ArrayList<>();
        for (JsonElement jsonElement : jsonArray) {
            ret.add(jsonElement.getAsString());
        }
        return ret;
    }
}