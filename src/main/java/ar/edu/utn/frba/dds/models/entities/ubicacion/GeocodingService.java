package ar.edu.utn.frba.dds.models.entities.ubicacion;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class GeocodingService {
    private static final String API_KEY = "pk.eyJ1IjoianJhaWptYW4iLCJhIjoiY2x2MmVzanMxMGY5YTJybjVmbXh5MW8weSJ9.NZEa7b9BPMe8znzHHNH8Tg";

    public static double[] getCoordinates(String address) throws IOException {
        String urlString = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + address + ".json?access_token=" + API_KEY;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + responseCode);
        } else {
            StringBuilder inline = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                inline.append(scanner.nextLine());
            }
            scanner.close();

            JSONObject json = new JSONObject(inline.toString());
            JSONArray features = json.getJSONArray("features");
            if (features.length() > 0) {
                JSONObject location = features.getJSONObject(0).getJSONObject("geometry");
                JSONArray coordinates = location.getJSONArray("coordinates");
                double lon = coordinates.getDouble(0);
                double lat = coordinates.getDouble(1);
                return new double[]{lat, lon};
            } else {
                throw new RuntimeException("No results found for the given address.");
            }
        }
    }
}
