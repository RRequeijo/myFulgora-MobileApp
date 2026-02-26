package com.example.myfulgora.data.model

data class BikeState(
    val bikeName: String = "Fulgora",
    val totalBikes: Int = 1,
    val speed: Int = 0,
    val batteryPercentage: Int = 0,
    val consumption: Double = 0.0,
    val range: Int = 0,
    val isLocked: Boolean = true,
    val isCharging: Boolean = false,
    val isOnline: Boolean = false,

    // Luzes e Sinais
    val leftTurnSignal: Boolean = false,
    val rightTurnSignal: Boolean = false,
    val highBeam: Boolean = false,

    // Mensagens de erro/aviso
    val warningMessage: String? = null,

    //BaterryScreen
    val batteryHealth: String = "Good", // Ex: Good, Fair, Bad
    val batteryTemp: Int = 0, // Em graus Celsius
    val batteryCycles: Int = 0,
    val avgConsumption: Double = 0.0, // Em kW/100km

    val timeLeft: String = "0h 00m"
)
