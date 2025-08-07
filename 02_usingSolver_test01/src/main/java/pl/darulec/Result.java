package pl.darulec;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Result {
    public static List<String> asteroidMonitor(int year, String pha) {
        List<String> result = new ArrayList<>();
        try {
            String url = "https://jsonmock.hackerrank.com/api/asteroids/search?page=1";
            if (year > 0) url += "&discovery_date=" + year;
            if (pha != null) url += "&pha=" + pha;

            HttpResponse<String> response = HttpClient.newHttpClient()
                .send(HttpRequest.newBuilder().uri(URI.create(url)).build(), 
                      HttpResponse.BodyHandlers.ofString());
            
            String json = response.body();
            int dataStart = json.indexOf("\"data\":[") + 8;
            String data = json.substring(dataStart, json.lastIndexOf("]"));
            
            // Proste wydobycie designation i period_yr
            for (String item : data.split("\\},\\{")) {
                if (item.contains("\"designation\":")) {
                    int start = item.indexOf("\"designation\":\"") + 14;
                    int end = item.indexOf("\"", start);
                    if (start > 13 && end > start) {
                        result.add(item.substring(start, end));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Błąd: " + e.getMessage());
        }
        return result;
    }

    public static void main(String[] args) {
        for (String asteroida : asteroidMonitor(2010, "Y")) {
            System.out.println(asteroida);
        }
    }
}