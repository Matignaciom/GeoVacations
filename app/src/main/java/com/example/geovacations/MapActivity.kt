package com.example.geovacations

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import org.osmdroid.api.IMapController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.util.GeoPoint
import org.osmdroid.config.Configuration

class MapActivity : ComponentActivity() {

    private lateinit var mapView: MapView
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = "com.example.geovacations/1.0"

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                setupMap()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            setupMap()
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
        }
    }

    private fun setupMap() {
        setContentView(R.layout.activity_map)

        mapView = findViewById(R.id.mapView)

        mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        val controller: IMapController = mapView.controller
        controller.setZoom(15.0)

        val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

        if (latitude != 0.0 && longitude != 0.0) {
            val geoPoint = GeoPoint(latitude, longitude)
            val marker = Marker(mapView)
            marker.position = geoPoint
            marker.title = "Ubicación del lugar visitado"
            mapView.overlays.add(marker)

            mapView.controller.setCenter(geoPoint)
        }
    }
}