package com.example.geovacations

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class VacationViewModel : ViewModel() {
    // Propiedad para almacenar la ubicación (latitud, longitud)
    var location = mutableStateOf<Pair<Double, Double>?>(null)

    // Propiedad para almacenar el nombre del lugar de las vacaciones
    var vacationName = mutableStateOf("")

    // Lista mutable para almacenar las URI de las imágenes
    var imageUris = mutableStateListOf<String>()

    // Método para agregar una URI de imagen a la lista
    fun addImageUri(imageUri: String) {
        imageUris.add(imageUri)
    }
}