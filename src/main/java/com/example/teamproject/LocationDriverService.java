package com.example.teamproject;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.things.userdriver.UserDriverManager;
import com.google.android.things.userdriver.location.GnssDriver;


import com.google.android.things.userdriver.location.GnssDriver;
import com.google.android.things.userdriver.UserDriverManager;

public class LocationDriverService extends Service {

    public LocationDriverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        throw new UnsupportedOperationException("Not yet implemented");
    }





}