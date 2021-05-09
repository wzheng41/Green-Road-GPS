package com.example.teamproject;

import java.util.ArrayList;

public class Node{
    private int x;
    private int y;
    private ArrayList<Intersection> path;
    private int[][] visited;
    public Node(int x, int y, ArrayList<Intersection> path, int[][] visited){
        this.x = x;
        this.y = y;
        this.path = path;
        this.visited = visited;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public ArrayList<Intersection> getPath() {
        return path;
    }
    public boolean not_visited(int x1, int y1){
        return this.visited[x1][y1] == 0;
    }
    public int getSize(){
        return this.path.size();
    }

    public Node deep_copy(int x1, int y1, Intersection new_intersection) {
        int[][] new_visited = new int[7][7];
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j < 7; j++) {
                new_visited[i][j] = this.visited[i][j];
            }
        }
        new_visited[x1][y1] = 1;
        ArrayList<Intersection> new_path = new ArrayList<Intersection>();
        for(int i = 0; i <path.size(); i++){
            new_path.add(this.path.get(i));
        }
        new_path.add(new_intersection);

        return new Node(x1, y1, new_path, new_visited);

    }

}

