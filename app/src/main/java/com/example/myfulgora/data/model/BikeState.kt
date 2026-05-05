package com.example.myfulgora.data.model

//BikeState (O Prato Feito): É a travessa de comida que sai da cozinha. Tem a bateria, o modo de condução e os documentos.

data class BikeState(
    // --- 1. IDENTIFICAÇÃO E ESTADO GERAL ---
    val bikeName: String = "Fulgora Motorcycle",
    val totalBikes: Int = 0,
    val isOnline: Boolean = false,
    val isLocked: Boolean = true,
    val drivingMode: String = "Normal",

    // --- 2. CONDUÇÃO E PERFORMANCE ---
    val totalKilometers: Int = 0,
    val averageSpeed: Int = 0,
    val range: Int = 0,
    val consumption: Double = 0.0,
    val tyreFront: Int = 0,
    val tyreBack: Int = 0,

    // --- 3. BATERIA (BÁSICO E CARREGAMENTO) ---
    val batteryPercentage: Int = 59,
    val isCharging: Boolean = true,
    val timeLeft: String = "0h 00m",
    val batteryHealth: String = "Good",
    val batteryTemp: Double = 0.0,
    val batteryCycles: Int = 0,
    val avgConsumption: Double = 0.0,

    // --- 6. MENSAGENS E ALERTAS ---
    val warningMessage: String? = null,

    // --- 6. LOCALIZAÇÃO ---
    val latitude: Double = 41.287611,
    val longitude: Double = -7.739555,



    // DOCUMENTAÇÃO
    val documents: Map<String, String> = emptyMap()

)
