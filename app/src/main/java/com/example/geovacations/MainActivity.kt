package com.example.geovacations

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.geovacations.ui.theme.GeoVacationsTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private var tempImageFile: File? = null
    private val vacationViewModel: VacationViewModel = VacationViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupPermissionLaunchers()
        setupCameraLauncher()

        setContent {
            GeoVacationsTheme {
                VacationScreen(vacationViewModel = vacationViewModel)
            }
        }
    }

    @Composable
    fun VacationScreen(vacationViewModel: VacationViewModel) {
        val vacationName = vacationViewModel.vacationName.value
        val imageUris = vacationViewModel.imageUris
        val location = vacationViewModel.location.value

        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = vacationName,
                onValueChange = { vacationViewModel.vacationName.value = it },
                label = { Text("Lugar visitado") },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(onClick = { requestLocationPermission() }) {
                    Text("Obtener ubicación")
                }

                Button(onClick = { requestCameraPermission() }) {
                    Text("Capturar foto")
                }

                Button(onClick = {
                    location?.let {
                        val intent = Intent(this@MainActivity, MapActivity::class.java).apply {
                            putExtra("LATITUDE", it.first)
                            putExtra("LONGITUDE", it.second)
                        }
                        startActivity(intent)
                    }
                }) {
                    Text("Ver en el mapa")
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f)
            ) {
                items(imageUris) { imageUri ->
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Foto",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .padding(8.dp)
                            .clickable {
                                // Navegar a FullImageActivity con la URI de la imagen
                                val intent = Intent(this@MainActivity, FullImageActivity::class.java).apply {
                                    putExtra("imageUri", imageUri)
                                }
                                startActivity(intent)
                            }
                    )
                }
            }

            location?.let {
                Text(text = "Ubicación: ${it.first}, ${it.second}", modifier = Modifier.padding(16.dp))
            }
        }
    }

    private fun setupPermissionLaunchers() {
        locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                fetchLocation()
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show()
            }
        }

        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                capturePhoto()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                tempImageFile?.let {
                    // Usar FileProvider para obtener un URI seguro
                    val imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", it)
                    vacationViewModel.addImageUri(imageUri.toString())
                    Toast.makeText(this, "Imagen capturada: $imageUri", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No se capturó ninguna imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLocation() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    location?.let {
                        vacationViewModel.location.value = Pair(it.latitude, it.longitude)
                        Toast.makeText(this, "Ubicación: ${it.latitude}, ${it.longitude}", Toast.LENGTH_SHORT).show()
                    } ?: Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun capturePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        tempImageFile = File.createTempFile("IMG_", ".jpg", cacheDir)

        // Usar FileProvider para pasar el URI del archivo al Intent
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, "${packageName}.provider", tempImageFile!!))
        cameraLauncher.launch(intent)
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun requestCameraPermission() {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }
}
