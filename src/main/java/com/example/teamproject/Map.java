package com.example.teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
import android.widget.Toast;

//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;


import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Map extends FragmentActivity implements OnMapReadyCallback {
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private GoogleMap mMap = null;
    private SupportMapFragment supportMapFragment;
    private SearchView searchView;
    private LatLng start = null;
    private LatLng destination = null;
    private List<LatLng> stops = new ArrayList<LatLng>();
    private RoadPrediction prediction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchlastLocation();
        // search functionality
        searchView = (SearchView) findViewById(R.id.search_location);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Search particular address according to user input
                String search_destination = searchView.getQuery().toString();
                List<Address> addressList = null;
                if (!search_destination.equals("") && mMap != null) {
                    Geocoder geocoder = new Geocoder(Map.this);
                    try {
                        addressList = geocoder.getFromLocationName(search_destination, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("", "error here");
                        return false;
                    }
                    Address address = addressList.get(0);
                    destination = new LatLng(address.getLatitude(), address.getLongitude());
                    MarkerOptions markerOptions = new MarkerOptions().position(destination).title("Your Destination");

                    mMap.animateCamera(CameraUpdateFactory.newLatLng(destination));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(destination, 10));
                    mMap.addMarker((markerOptions));

                }
                //webAPICall();
                test();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // get location update
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        locationRequest.setInterval(1000);          //update per second
        LocationCallback locationCallBack = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationresult) {
                if (locationresult == null) {
                    Log.d("", "No Update");
                } else {
                    currentLocation = locationresult.getLastLocation();
                    // Log.d("", "Current Location Updated");
                }
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallBack, null);

    }

    //Acquire current location
    private void fetchlastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + " " +
                            currentLocation.getLongitude(), Toast.LENGTH_LONG).show();
                    supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
                    supportMapFragment.getMapAsync(Map.this);
                }
            }
        });

    }

    //put marker on the start location based on current location
    public void setUpStartLocation_Marker() {
        start = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(start).title("Here");

        mMap.animateCamera(CameraUpdateFactory.newLatLng(start));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 10));
        mMap.addMarker((markerOptions));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setUpStartLocation_Marker();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        /*
            Click Map Listener: Allow users add a new marker on Map
         */
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Here");
                stops.add(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                mMap.addMarker((markerOptions));
            }
        });

        /*
            Click Marker Listener: Allow user delete marker
         */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.remove();
                for (int i = 0; i < stops.size(); i++) {
                    if (stops.get(i).equals(marker.getPosition())) {
                        stops.remove(stops.get(i));
                        break;
                    }
                }
                return false;
            }
        });

        /*
            Long click markder listener: allow user re-locate start point
         */
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode)
        {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    fetchlastLocation();
                }
                break;
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    public void findRoute() {
        GeoApiContext context = new GeoApiContext.Builder()
                .apiKey("AIzaSyCtwKaZxlDUCTNrpA0aZ0c8wAoGFK-ZqpM") //specific API
                .build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context,
                Double.toString(currentLocation.getLatitude()) + "," + Double.toString(currentLocation.getLongitude()),
                Double.toString(destination.latitude) + "," + Double.toString(destination.longitude));

        // adding stops on the path
        String[] waypoints = new String[stops.size()];
        for(int i = 0; i < stops.size(); i++)
        {
            waypoints[i] = Double.toString(stops.get(i).latitude) + "," + Double.toString(stops.get(i).longitude);
        }
        req.waypoints(waypoints);
        List<LatLng> path = parserResult(req);
        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(15);
            mMap.addPolyline(opts);
        }

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
    }

    /*
        Parse result from google API, return all path along the route
        return: ArrayList contains all Latlng objects
     */
    public List<LatLng> parserResult(DirectionsApiRequest req ){
        List<LatLng> path = new ArrayList();
        try {
            DirectionsResult res = req.await();
            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];
                if (route.legs !=null) {
                    for(int i=0; i<route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j=0; j<leg.steps.length;j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length >0) {
                                    for (int k=0; k<step.steps.length;k++){
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.e("", ex.getLocalizedMessage());
        }
        return path;
    }

//    public void webAPICall(String points){
//        String URL = "https://roads.googleapis.com/v1/snapToRoads?path=";
//        //-35.27801,149.12958|-35.28032,149.12907|-35.28099,149.12929|-35.28144,149.12984|-35.28194,149.13003|-35.28282,149.12956|-35.28302,149.12881|-35.28473,149.12836";
//        URL += Double.toString(start.latitude) + "," + Double.toString(start.longitude) + "|";
//        /*
//                Need to add random selection geo location for computation of route
//         */
//        URL += points;
//
//        URL += Double.toString(destination.latitude) + "," + Double.toString(destination.longitude);
//        URL += "&interpolate=true&key=AIzaSyCtwKaZxlDUCTNrpA0aZ0c8wAoGFK-ZqpM";
//        List<LatLng> path = new ArrayList();
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        JsonObjectRequest objectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                URL,
//                null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray temp = response.getJSONArray("snappedPoints");
//
//                            for(int i = 0; i < temp.length(); i++)
//                            {
//                                JSONObject item = (JSONObject) temp.get(i);
//                                JSONObject location = (JSONObject) item.get("location");
//                                Double lat = Double.parseDouble(location.get("latitude").toString());
//                                Double lon = Double.parseDouble(location.get("longitude").toString());
//                                LatLng road = new LatLng(lat, lon);
//                                path.add(road);
//                                Log.e("","added");
//                            }
//                            //Draw the polyline
//                            if (path.size() > 0) {
//                                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.RED).width(5);
//                                mMap.addPolyline(opts);
//                                Log.e("","Successful");
//                            }
//                        } catch (JSONException e) {
//                            Log.e("", "ERROR");
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//            }
//        }
//        );
//        requestQueue.add(objectRequest);
//
//    }

    public void test(){
        if(start == null || destination == null) return;
        prediction = new RoadPrediction(start, destination, stops);
        if(!prediction.check_validation())
        {
            Toast.makeText(getApplicationContext(), "Location is out of scope", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            findRoute();
            String err = prediction.save(getExternalFilesDir(null).toString() + "/2.txt");
            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
            prediction.setMap(getExternalFilesDir(null).toString() + "/2.txt");
            ArrayList<Node> paths = prediction.findInterceptions();
            double[] fuel = new double[paths.size()];
            double[] time = new double[paths.size()];
            double[] distance = new double[paths.size()];
            double[] intersections = new double[paths.size()];

            List<List<LatLng>> list = new ArrayList<>();
            for(int i = 0; i < paths.size(); i++){
                ArrayList<Intersection> path = paths.get(i).getPath();
                String[] waypoints = new String[path.size()];
                String[] allLatitude = new String[path.size()+2];
                String[] allLongitude = new String[path.size()+2];          // for calculation of fuel
                for(int j = 0; j < path.size(); j++){
                    waypoints[j] = Double.toString(path.get(j).getLatitude()) + "," + Double.toString(path.get(j).getLongitude());
                    allLatitude[j+1] = Double.toString(path.get(j).getLatitude());
                    allLongitude[j+1] = Double.toString(path.get(j).getLongitude());
                }
                allLatitude[0] = Double.toString(currentLocation.getLatitude());
                allLongitude[0] = Double.toString(currentLocation.getLongitude());
                allLatitude[allLatitude.length-1] = Double.toString(destination.latitude);
                allLongitude[allLongitude.length-1] = Double.toString(destination.longitude);

                GeoApiContext context = new GeoApiContext.Builder()
                        .apiKey("AIzaSyCtwKaZxlDUCTNrpA0aZ0c8wAoGFK-ZqpM") //specific API
                        .build();
                DirectionsApiRequest req = DirectionsApi.getDirections(context,
                        Double.toString(currentLocation.getLatitude()) + "," + Double.toString(currentLocation.getLongitude()),
                        Double.toString(destination.latitude) + "," + Double.toString(destination.longitude));
                req.waypoints(waypoints);
                List<LatLng> p = parserResult(req);
                callDirectionAPI(allLatitude, allLongitude);
                Thread.sleep(700);
                fuel[i] =  totalFuel;
                time[i] = totalTime;
                distance[i] = totalDistance;
                intersections[i] = totalIntersection;
                //Draw the polyline
//                if (p.size() > 0) {
//                    PolylineOptions opts = new PolylineOptions().addAll(p).color(Color.RED).width(5);
//                    mMap.addPolyline(opts);
//                }
                list.add(p);

                LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
            }
            /*
                    add activity find fuel
             */
            for(int i = 0; i < list.size(); i++){
                List<LatLng> p =list.get(i);
                Log.e("", Double.toString(fuel[i]));
                Toast.makeText(getApplicationContext(), "The " + i + " route spend fuel : " + String.format("%.2f", fuel[i]) + " (Unit: ml)"
                        + "\nTotal Time: " + Double.toString(time[i]) + "(Unit: second)"+ "\nTotal Distance: " + Double.toString(distance[i]) + "(Unit: meter)"+ "\nTotal Intersection: "
                        + Double.toString(intersections[i]), Toast.LENGTH_LONG).show();
                if (p.size() > 0) {
                    PolylineOptions opts = new PolylineOptions().addAll(p).color(Color.RED).width(5);
                    mMap.addPolyline(opts);
                }
            }

            double fuelusage = Integer.MAX_VALUE;
            int minIndex = 0;
            for(int i = 0; i < list.size(); i++)
            {
                List<LatLng> p =list.get(i);
                if(fuel[i] < fuelusage)
                {
                    fuelusage = fuel[i];
                    minIndex = i;
                }
                /*
                if (p.size() > 0) {
                    PolylineOptions opts = new PolylineOptions().addAll(p).color(Color.RED).width(5);
                    mMap.addPolyline(opts);
                }
                */

            }
            List<LatLng> p =list.get(minIndex);
            Toast.makeText(getApplicationContext(), "shortest path number: " + minIndex, Toast.LENGTH_SHORT).show();
            if (p.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(p).color(Color.GREEN).width(20);
                mMap.addPolyline(opts);
            }





        }catch(Exception e){
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    private double calculateFuel(double distance, double time, double numIntersection,
                                 double avgRedLightTime) {

        double distanceInMile = distance/1609.34;
        double totalDriveTime = time;
        double numberOfIntersection = numIntersection;


        double drivingTime = (totalDriveTime) - numberOfIntersection * avgRedLightTime;
        double drivingTimeHour = drivingTime/3600;
        double avgSpeed = distanceInMile / drivingTimeHour;
        double newFuelEfficiency;
        if(avgSpeed >= 57)
        {
            // every 1mph increase in speed will decrease mpg by 0.3
            newFuelEfficiency = 30 - 0.3 * (avgSpeed - 57);
        }
        else if(avgSpeed >= 25)
        {
            // every 1mph increase in speed will increase mpg by 0.1
            newFuelEfficiency = 27 + 0.1 * (avgSpeed - 25);
        }
        else if(avgSpeed >= 10)
        {
            // every 1mph increase in speed will increase mph by 0.85
            newFuelEfficiency = 10 + 0.85 * (avgSpeed - 5);
        }
        else
        {
            newFuelEfficiency = 10;
        }


        double idleGasPs = 0.3;
        double drivingFuelCostInGallon = distanceInMile / newFuelEfficiency;
        double drivingFuelCostInMl = drivingFuelCostInGallon * 3785.41;
        double idleFuelCost = numberOfIntersection * avgRedLightTime * idleGasPs;
        double totalFuelCost = drivingFuelCostInMl + idleFuelCost;
        return totalFuelCost;
    }

    double totalDistance = 0;
    double totalTime = 0;
    double totalIntersection = 0;
    double totalFuel = 0;
    private void callDirectionAPI(String[] latitude, String[] longitude) {
        totalDistance = 0;
        totalTime = 0;
        totalIntersection = 0;
        totalFuel = 0;
        totalIntersection = latitude.length - 2;

        OkHttpClient client = new OkHttpClient();
        String urlBase = "https://maps.googleapis.com/maps/api/directions/json?";
        String myKey = "&key=AIzaSyBUtoES-TRhAJBT72z9XyctAjh8aeCvGT0";

        StringBuilder origin = new StringBuilder();
        StringBuilder wayPoint = new StringBuilder();
        StringBuilder destination = new StringBuilder();

        origin.append("origin=");
        if(latitude.length > 2)
        {
            wayPoint.append("&waypoints=");
        }
        destination.append("&destination=");
        if(latitude.length >= 2 && longitude.length >= 2 && latitude.length == longitude.length)
        {
            origin.append(latitude[0]);
            origin.append(',');
            origin.append(longitude[0]);

            destination.append(latitude[latitude.length - 1]);
            destination.append(',');
            destination.append(longitude[longitude.length - 1]);

            for(int i = 1; i < latitude.length - 1; i++)
            {
                wayPoint.append(latitude[i]);
                wayPoint.append(',');
                wayPoint.append(longitude[i]);
                if(i < latitude.length - 2)
                {
                    wayPoint.append('|');
                }
            }
        }
        else
        {
            //return 0.0;
        }

        String finalUrl = urlBase + origin.toString() + destination.toString() + wayPoint.toString() +
                myKey;

        // Toast.makeText(this, finalUrl, Toast.LENGTH_SHORT).show();
        Log.d("request url", finalUrl);

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Direction api failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                if(response.isSuccessful())
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Toast.makeText(getApplicationContext(), "Direction api success", Toast.LENGTH_SHORT).show();

                        }
                    });

                    String myResponse = response.body().string();
                    try {
                        JSONObject inputJSONObject = new JSONObject(myResponse);
                        JSONArray legs = inputJSONObject.getJSONArray("routes")
                                .getJSONObject(0)
                                .getJSONArray("legs");
                        for(int i = 0; i < legs.length();i++)
                        {
                            JSONObject leg = legs.getJSONObject(i);
                            JSONObject distance = leg.getJSONObject("distance");
                            JSONObject duration = leg.getJSONObject("duration");
                            totalDistance += distance.getDouble("value");
                            totalTime += duration.getDouble("value");
                        }

                        Log.d("totalDistance", String.valueOf(totalDistance));
                        Log.d("totalTime", String.valueOf(totalTime));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(getApplicationContext(), "Time1: " + totalTime, Toast.LENGTH_LONG).show();       // first test time

                            }
                        });

                        totalFuel = calculateFuel(totalDistance, totalTime, totalIntersection,30);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Jason response",myResponse);
                }

            }


        });
        //Toast.makeText(getApplicationContext(), "Time2: " + totalTime, Toast.LENGTH_LONG).show();       // second test time
        //totalFuel = calculateFuel(totalDistance, totalTime, totalIntersection,30);
        //return totalFuel;
    }
}