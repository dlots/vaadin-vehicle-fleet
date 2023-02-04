package com.github.dlots.vehiclefleet.util;

import com.github.dlots.vehiclefleet.data.entity.GpsPoint;
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
import java.util.List;

public class YandexMapsHandler {
    private static final String GEOCODER_API_URL = "https://geocode-maps.yandex.ru/1.x/";
    private static final String FORMAT = "?format=json";
    private static final String API_KEY = "&apikey=";
    private static final String GEOCODE = "&geocode=";
    private static final String GPS_TRACK_STATIC_MAP_URL = "https://static-maps.yandex.ru/1.x/?l=map&pl=";
    private static final String A_B_FORMAT = "&pt=%f,%f,pm2am~%f,%f,pm2bm";
    private static final String RANDOM_IMAGE_URL = "https://picsum.photos/200";

    private final HttpClient httpClient;

    public YandexMapsHandler() {
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

    public String getGpsTrackMapUrl(List<GpsPoint> gpsPoints) {
        String coordinates = null;
        String AB = null;
        if (gpsPoints != null && !gpsPoints.isEmpty()) {
            StringBuilder coordinatesBuilder = new StringBuilder();
            for (GpsPoint gpsPoint : gpsPoints) {
                coordinatesBuilder.append(gpsPoint.getLongitude()).append(',').append(gpsPoint.getLatitude()).append(',');
            }
            coordinatesBuilder.deleteCharAt(coordinatesBuilder.length() - 1);
            coordinates = coordinatesBuilder.toString();
            GpsPoint A = gpsPoints.get(0);
            GpsPoint B = gpsPoints.get(gpsPoints.size() - 1);
            AB = String.format(A_B_FORMAT, A.getLongitude(), A.getLatitude(), B.getLongitude(), B.getLatitude());
        }
        return (coordinates == null || coordinates.isEmpty()) ?
                RANDOM_IMAGE_URL : GPS_TRACK_STATIC_MAP_URL + coordinates + AB;
    }

    private JsonObject getGeocoderResponse(String geocode) throws IOException {
        String url = "" +
                GEOCODER_API_URL +
                FORMAT +
                API_KEY + System.getenv("YANDEX_API_KEY") +
                GEOCODE + URLEncoder.encode(geocode, StandardCharsets.UTF_8);
        InputStream stream = sendRequest(url);
        String jsonString = convertStreamToString(stream);
        JsonObject json = Json.parse(jsonString);
        return json.getObject("response");
    }

    private InputStream sendRequest(String url) throws IOException {
        HttpUriRequest request = new HttpGet(url);
        HttpResponse response = httpClient.execute(request);
        if (response.getStatusLine().getStatusCode() != HttpStatusCode.OK.getCode()) {
            throw new IOException(response.getStatusLine().toString());
        }
        return response.getEntity().getContent();
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
