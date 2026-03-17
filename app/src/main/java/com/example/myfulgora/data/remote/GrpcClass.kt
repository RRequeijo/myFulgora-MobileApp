package com.example.myfulgora.data.remote

import android.util.Log
import com.example.grpc.MotaRequest
import com.example.grpc.MotaResponse
import com.example.grpc.MotasServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GrpcClass {
    // 1. Configura o "Tubo" para o servidor do laboratório
    private val channel = ManagedChannelBuilder.forAddress("172.20.0.202", 5154)
        .usePlaintext() // usePlaintext() significa que não usamos HTTPS/SSL (normal em testes locais)
        .build()

    // 2. Cria o "Atendente" Kotlin que sabe as regras do ficheiro .proto
    private val stub = MotasServiceGrpcKt.MotasServiceCoroutineStub(channel)

    // 3. A função que pede os dados (corre fora da UI thread para não encravar o ecrã)
    suspend fun getMotaInfo(vinMota: String): MotaResponse? {
        return withContext(Dispatchers.IO) {
            try {
                // Prepara a pergunta (MotaRequest)
                val request = MotaRequest.newBuilder()
                    .setVin(vinMota)
                    .build()

                // Faz a chamada e espera pela resposta
                val response = stub.getMotaInfo(request)
                Log.d("GRPC", "Sucesso! Bateria recebida: ${response.batteryLevel}")

                response // Devolve a resposta
            } catch (e: Exception) {
                Log.e("GRPC", "Erro na chamada gRPC: ${e.message}")
                null
            }
        }
    }
}