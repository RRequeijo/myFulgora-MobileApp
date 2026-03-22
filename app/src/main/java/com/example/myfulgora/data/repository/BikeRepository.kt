package com.example.myfulgora.data.repository

import com.example.myfulgora.data.model.BikeState
import kotlinx.coroutines.flow.Flow

// 1. O CONTRATO: A app diz o que precisa, independentemente de onde vêm os dados.
interface BikeRepository {
    // Para quando a app abre (equivalente ao GetMotaInfo do teu .proto)
    suspend fun getBikeInfo(vin: String): BikeState

    // Para ficar a ouvir atualizações em tempo real (equivalente ao StreamMotaUpdates)
    fun observeBikeUpdates(vin: String): Flow<BikeState>
}