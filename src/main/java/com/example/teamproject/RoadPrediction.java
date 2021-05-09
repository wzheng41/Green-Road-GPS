package com.example.teamproject;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.GeoApiContext;
import com.google.maps.RoadsApi;

import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner; // Import the Scanner class to read text files

public class RoadPrediction {
    private LatLng start;
    private LatLng destination;
    private List<LatLng> stops;
    private final LatLng top_left_corner = new LatLng(33.439985, -111.983661);
    private final LatLng bottom_right_corner = new LatLng(33.383582, -111.887204);
    private Intersection[][] Intersections;

    public RoadPrediction(LatLng start, LatLng destination, List<LatLng> stops) {
        this.start = start;
        this.destination = destination;
        this.stops = stops;
    }

    public void setStart(LatLng new_start){
        this.start = new_start;
    }
    public void setDestination(LatLng new_destination){
        this.destination = new_destination;
    }
    public boolean check_validation(){
        return start.latitude < top_left_corner.latitude && start.latitude > bottom_right_corner.latitude
                && start.longitude > top_left_corner.longitude && start.longitude < bottom_right_corner.longitude
                && destination.latitude < top_left_corner.latitude && destination.latitude > bottom_right_corner.latitude
                && destination.longitude > top_left_corner.longitude && destination.longitude < bottom_right_corner.longitude;
    }
    public void setStops(List<LatLng> new_stops){
        this.stops = new_stops;
    }

    public void setMap(String fileName){
        this.Intersections = new Intersection[7][7];
        try{
            File information = new File(fileName);
            Scanner scan = new Scanner(information);
            for(int i = 0; i < 7; i++) {
                for(int j = 0; j < 7; j++){
                    double latitude = scan.nextDouble();
                    double longitude = scan.nextDouble();
                    double light_timing = scan.nextDouble();
                    Intersection intersection = new Intersection(latitude, longitude, light_timing);
                    Intersections[i][j] = intersection;
                }
            }
            scan.close();
        }catch(Exception e){
            Log.e("", "Can't read file");
            e.printStackTrace();
        }
    }

    //find path
    public ArrayList<Node> findInterceptions(){
        double dist1 = Double.POSITIVE_INFINITY;
        double dist2 = Double.POSITIVE_INFINITY;
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 7; j++){
                if (Math.abs(start.latitude - Intersections[i][j].getLatitude()) + Math.abs(start.longitude - Intersections[i][j].getLongitude()) < dist1){
                    dist1 = Math.abs(start.latitude - Intersections[i][j].getLatitude()) + Math.abs(start.longitude - Intersections[i][j].getLongitude());
                    x1 = i;
                    y1 = j;
                }
                if (Math.abs(destination.latitude - Intersections[i][j].getLatitude()) + Math.abs(destination.longitude - Intersections[i][j].getLongitude()) < dist2){
                    dist2 = Math.abs(destination.latitude - Intersections[i][j].getLatitude()) + Math.abs(destination.longitude - Intersections[i][j].getLongitude());
                    x2 = i;
                    y2 = j;
                }
            }
        }
        // find route
        int[] res = new int[4];
        res[0] = x1;
        res[1] = y1;
        res[2] = x2;
        res[3] = y2;
        ArrayList<Node> paths = bfs(res);
        return paths;

    }

    public String save(String fileName){
        try{
            FileWriter myWriter = new FileWriter(fileName);
            String information = "33.429435\n" +
                    "-111.891101\n" +
                    "0\n" +
                    "33.437218\n" +
                    "-111.960922\n" +
                    "0\n" +
                    "33.436215\n" +
                    "-111.952718\n" +
                    "0\n" +
                    "33.435682\n" +
                    "-111.941897\n" +
                    "0\n" +
                    "33.435972\n" +
                    "-111.926342\n" +
                    "0\n" +
                    "33.436102\n" +
                    "-111.909530\n" +
                    "0\n" +
                    "33.433765\n" +
                    "-111.890971\n" +
                    "0\n" +
                    "33.429824\n" +
                    "-111.977948\n" +
                    "0\n" +
                    "33.429603\n" +
                    "-111.961076\n" +
                    "40\n" +
                    "33.430967\n" +
                    "-111.952433\n" +
                    "40\n" +
                    "33.429555\n" +
                    "-111.939935\n" +
                    "40\n" +
                    "33.428950\n" +
                    "-111.926447\n" +
                    "40\n" +
                    "33.429134\n" +
                    "-111.909059\n" +
                    "40\n" +
                    "33.429371\n" +
                    "-111.891140\n" +
                    "40\n" +
                    "33.421945\n" +
                    "-111.978147\n" +
                    "0\n" +
                    "33.421865\n" +
                    "-111.96093340\n" +
                    "40\n" +
                    "33.421922\n" +
                    "-111.952237\n" +
                    "40\n" +
                    "33.421965\n" +
                    "-111.939935\n" +
                    "40\n" +
                    "33.422011\n" +
                    "-111.926384\n" +
                    "40\n" +
                    "33.421993\n" +
                    "-111.909142\n" +
                    "40\n" +
                    "33.421942\n" +
                    "-111.891251\n" +
                    "0\n" +
                    "33.421865\n" +
                    "-111.96093340\n" +
                    "0\n" +
                    "33.413127\n" +
                    "-111.960916\n" +
                    "40\n" +
                    "33.414636\n" +
                    "-111.952296\n" +
                    "40\n" +
                    "33.414796\n" +
                    "-111.939990\n" +
                    "40\n" +
                    "33.414691\n" +
                    "-111.926269\n" +
                    "40\n" +
                    "33.414823\n" +
                    "-111.909117\n" +
                    "40\n" +
                    "33.414894\n" +
                    "-111.891318\n" +
                    "0\n" +
                    "33.408728\n" +
                    "-111.972501\n" +
                    "0\n" +
                    "33.407403\n" +
                    "-111.960805\n" +
                    "40\n" +
                    "33.407365\n" +
                    "-111.952213\n" +
                    "40\n" +
                    "33.407527\n" +
                    "-111.939874\n" +
                    "40\n" +
                    "33.407447\n" +
                    "-111.926263\n" +
                    "40\n" +
                    "33.407394\n" +
                    "-111.909141\n" +
                    "40\n" +
                    "33.407435\n" +
                    "-111.891238\n" +
                    "0\n" +
                    "33.392846\n" +
                    "-111.967551\n" +
                    "0\n" +
                    "33.392780\n" +
                    "-111.960900\n" +
                    "40\n" +
                    "33.392914\n" +
                    "-111.952014\n" +
                    "40\n" +
                    "33.392710\n" +
                    "-111.939608\n" +
                    "40\n" +
                    "33.393048\n" +
                    "-111.926333\n" +
                    "40\n" +
                    "33.392913\n" +
                    "-111.909124\n" +
                    "40\n" +
                    "33.392962\n" +
                    "-111.891194\n" +
                    "40\n" +
                    "33.388393\n" +
                    "-111.967516\n" +
                    "0\n" +
                    "33.387617\n" +
                    "-111.960960\n" +
                    "40\n" +
                    "33.385721\n" +
                    "-111.952285\n" +
                    "40\n" +
                    "33.385448\n" +
                    "-111.939429\n" +
                    "40\n" +
                    "33.385604\n" +
                    "-111.926398\n" +
                    "40\n" +
                    "33.385555\n" +
                    "-111.909284\n" +
                    "40\n" +
                    "33.385828\n" +
                    "-111.891131\n" +
                    "0";
        myWriter.write(information);
        myWriter.close();
        }catch(Exception e){
            e.printStackTrace();
            return e.toString();
        }
        return "Successfully";
    }

    public ArrayList<Node> bfs(int[] points){
        int[][] visited = new int[7][7];
        ArrayList<Intersection> path = new ArrayList<Intersection>();
        visited[points[0]][points[1]] = 1;
        path.add(Intersections[points[0]][points[1]]);
        ArrayList<Node> queue = new ArrayList<Node>();
        queue.add(new Node(points[0], points[1], path, visited));
        ArrayList<Node> res = new ArrayList<Node>();
        while(queue.size() != 0) {
            Node current_node =queue.get(0);
            queue.remove(0);
            if(current_node.getX() == points[2] && current_node.getY() == points[3]){
                res.add(current_node);
                continue;
            }
            if(res.size() >= 5){
                break;
            }
            int x = current_node.getX();
            int y = current_node.getY();
            if(x-1 >= 0 && current_node.not_visited(x-1, y)){
                queue.add(current_node.deep_copy(x-1,y, Intersections[x-1][y]));
            }
            if(x+1 < 7 && current_node.not_visited(x+1, y)){
                queue.add(current_node.deep_copy(x+1,y, Intersections[x+1][y]));
            }
            if(y-1 >= 0 && current_node.not_visited(x, y-1)){
                queue.add(current_node.deep_copy(x,y-1, Intersections[x][y-1]));
            }
            if(y+1 < 7 && current_node.not_visited(x, y+1)){
                queue.add(current_node.deep_copy(x,y+1, Intersections[x][y+1]));
            }
        }
        Log.e("", String.valueOf(res.size()));
        return res;

    }









}
