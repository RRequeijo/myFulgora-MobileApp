package com.example.myfulgora.data.repository

import com.example.myfulgora.data.model.BikeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

//ESTE FICHEIRO REPRESENTARIA A IMPLEMENTAÇÃO REAL DA INTERFACE BikeRepository. OU SEJA, COMO SE ESTIVESSE A FAZER O ACESSO AO
// SERVIDOR BACKEND DA AMOVER

// 2. A IMPLEMENTAÇÃO FALSA: Finge que vai à internet, mas devolve dados locais.
class MockBikeRepository : BikeRepository {

    override suspend fun getBikeInfo(vin: String): BikeState {
        delay(1000) // Simula o tempo que demora a ir à internet (1 segundo)

        // Devolve os teus dados falsos atuais!
        // Nota: O teu BikeState original deve ter estes campos. Adapta se os nomes forem diferentes.
        return BikeState(
            bikeName = "My Fulgora",
            batteryPercentage = 40,
            range = 85,
            isOnline = true,
            latitude = -20.310380,
            longitude = -40.294931,
            totalKilometers = 1250,
            // ... outros campos que tenhas no teu modelo
        )
    }

    override fun observeBikeUpdates(vin: String): Flow<BikeState> = flow {
        // Simula o StreamMotaUpdates! Fica num loop a enviar dados novos a cada 5 segundos.
        var currentBattery = 40
        while(true) {
            emit(
                BikeState(
                    bikeName = "My Fulgora",
                    batteryPercentage = currentBattery,
                    range = (currentBattery * 2.1).toInt(), // Autonomia simulada
                    isOnline = true,
                    latitude = -20.310380,
                    longitude = -40.294931,
                    totalKilometers = 1250
                )
            )
            delay(5000) // Espera 5 segundos
            if(currentBattery > 1) currentBattery -= 1 // Finge que a bateria está a gastar
        }
    }
}