package com.github.dlots.vehiclefleet.util;

import com.vaadin.flow.server.HttpStatusCode;
import elemental.json.Json;
import elemental.json.JsonObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class YandexGeocoderHandler {
    private static final String GEOCODER_API_URL = "https://geocode-maps.yandex.ru/1.x/";
    private static final String FORMAT = "?format=json";
    private static final String API_KEY = "&apikey=";
    private static final String GEOCODE = "&geocode=";

    private final HttpClient httpClient;

    public YandexGeocoderHandler() {
        this.httpClient = new DefaultHttpClient();
    }

    public String getAddressFromCoordinates(double latitude, double longitude) {
        try {
            // Geocoder API expects coordinates geocode in "<longitude>,<latitude>" format.
            JsonObject responseJson = getGeocoderResponse(String.format("%f,%f", longitude, latitude));
            JsonObject addressObject = responseJson
                    .getObject("GeoObjectCollection")
                    .getArray("featureMember")
                    .getObject(0).getObject("GeoObject");
            return String.format("%s, %s", addressObject.getString("description"), addressObject.getString("name"));
        } catch (IOException e) {
            return "Error retrieving address";
        }
    }

    private JsonObject getGeocoderResponse(String geocode) throws IOException {
        String url = "" +
                GEOCODER_API_URL +
                FORMAT +
                API_KEY + System.getenv("YANDEX_API_KEY") +
                GEOCODE + URLEncoder.encode(geocode, StandardCharsets.UTF_8);
        HttpUriRequest request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatusCode.OK.getCode()) {
            throw new IOException(response.getStatusLine().toString());
        }
        String jsonString = convertStreamToString(response.getEntity().getContent());
        JsonObject json = Json.parse(jsonString);
        return json.getObject("response");
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
