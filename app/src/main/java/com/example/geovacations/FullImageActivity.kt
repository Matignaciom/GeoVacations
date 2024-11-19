package com.example.geovacations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import com.example.geovacations.ui.theme.GeoVacationsTheme

class FullImageActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener la URI de la imagen desde los extras del Intent
        val imageUri = intent.getStringExtra("imageUri")

        setContent {
            GeoVacationsTheme {
                // Mostrar la imagen a pantalla completa
                imageUri?.let {
                    FullScreenImage(it)
                }
            }
        }
    }

    @Composable
    fun FullScreenImage(imageUri: String) {
        // Mostrar la imagen a pantalla completa usando Coil para cargarla
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageUri,
                contentDescription = "Imagen completa",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}