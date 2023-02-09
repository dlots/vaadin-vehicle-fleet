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
import java.util.List;

public class RoutingHandler {
    private static final String ROUTING_API_URL = "https://graphhopper.com/api/1/route/";
    private static final String API_KEY = "?key=";
    private static final String NO_POINTS = "&calc_points=false";

    private final HttpClient httpClient;

    public RoutingHandler() {
        this.httpClient = new DefaultHttpClient();
    }

    public Double getRouteLength(List<GpsPoint> gpsPoints) {
        if (gpsPoints == null || gpsPoints.isEmpty()) {
            return 0.;
        }
        try {
            StringBuilder points = new StringBuilder();
            for (GpsPoint point : gpsPoints) {
                points.append(String.format("&point=%f,%f", point.getLatitude(), point.getLongitude()));
            }
            // Geocoder API expects coordinates geocode in "<longitude>,<latitude>" format.
            JsonObject responseJson = getRoutesResponse(points.toString());
            return responseJson.getArray("paths").getObject(0).getNumber("distance");
        } catch (IOException e) {
            return -1.0;
        }
    }

    private JsonObject getRoutesResponse(String points) throws IOException {
        String url = "" +
                ROUTING_API_URL +
                API_KEY + System.getenv("GRAPHHOPPER_API_KEY") +
                points +
                NO_POINTS;
        InputStream stream = sendRequest(url);
        String jsonString = convertStreamToString(stream);
        return Json.parse(jsonString);
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
