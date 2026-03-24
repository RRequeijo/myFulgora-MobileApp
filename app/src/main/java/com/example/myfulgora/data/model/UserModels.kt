package com.example.myfulgora.data.model

//UserModels (As Cadeiras e Mesas): É apenas a planta do restaurante. Diz que uma Bike tem um vin e um batteryLevel. Não faz absolutamente nada, só define a forma das coisas.

data class User(
    val username: String,
    val profile: UserProfile,
    val bikes: MutableList<Bike>
)

data class UserProfile(
    var name: String,
    var email: String,
    var photoUri: String? = null
)

data class Bike(
    val vin: String,
    var name: String,
    val batteryLevel: Int = 0,
    val batteryRange: Double = 0.0,
    var documents: MutableMap<String, String>? = null
)