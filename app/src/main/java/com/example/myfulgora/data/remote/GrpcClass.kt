package com.example.myfulgora.data.remote

import android.util.Log
import com.example.grpc.MotaRequest
import com.example.grpc.MotaResponse
import com.example.grpc.MotasServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class GrpcClass {
    // Usamos o IP do teu servidor gRPC. 
    // Se estiveres no emulador e o servidor no mesmo PC, podes tentar "10.0.2.2"
    private val channel = ManagedChannelBuilder.forAddress("192.168.1.127", 5154)
        .usePlaintext()
        .keepAliveTime(30, TimeUnit.SECONDS)
        .build()

    private val stub = MotasServiceGrpcKt.MotasServiceCoroutineStub(channel)

    suspend fun getMotaInfo(vinMota: String): MotaResponse? {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("GRPC", "📡 A tentar ligar ao servidor (192.168.1.127:5154)...")
                val request = MotaRequest.newBuilder().setVin(vinMota).build()

                // O stub.getMotaInfo é uma suspend function gerada pelo gRPC Kotlin
                val response = stub.getMotaInfo(request)
                
                Log.d("GRPC", "✅ SUCESSO! Resposta recebida do servidor.")
                response
            } catch (e: Exception) {
                Log.e("GRPC", "❌ ERRO na chamada gRPC: ${e.message}")
                // e.printStackTrace() // Opcional, para ver a stack trace completa no Logcat
                null
            }
        }
    }
}
