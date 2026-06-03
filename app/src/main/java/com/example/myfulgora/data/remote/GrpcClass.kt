package com.example.myfulgora.data.remote

import android.util.Log
import com.example.grpc.MotaRequest
import com.example.grpc.MotaResponse
import com.example.grpc.MotasServiceGrpcKt
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GrpcClass {
<<<<<<< Updated upstream
    // Usamos o IP do teu servidor gRPC. 
    // Se estiveres no emulador e o servidor no mesmo PC, podes tentar "10.0.2.2"
    private val channel = ManagedChannelBuilder.forAddress("192.168.1.127", 5154)
        .usePlaintext()
=======

    companion object {
        private const val TAG = "GrpcClass"

        // TODO: Move HOST and PORT to BuildConfig fields so they can be set per
        //   build variant (debug = local LAN, release = production server).
        //   Example in build.gradle defaultConfig:
        //     buildConfigField("String", "GRPC_HOST", "\"your.production.server\"")
        //     buildConfigField("int",    "GRPC_PORT",  "5154")
        //
        // SECURITY: Switch usePlaintext() → useTransportSecurity() (TLS) in production.
        //   Plaintext gRPC exposes all bike telemetry on the network.
        private const val GRPC_HOST = "85.234.145.56"
        private const val GRPC_PORT = 50052
    }

    private val channel = ManagedChannelBuilder
        .forAddress(GRPC_HOST, GRPC_PORT)
        .usePlaintext() // TODO: Replace with .useTransportSecurity() for production
>>>>>>> Stashed changes
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

    // Esta é a função que fica com o "tubo" aberto a receber dados a cada 5 segundos!
    fun streamMotaUpdates(vinMota: String): Flow<MotaResponse> = flow {
        val request = MotaRequest.newBuilder().setVin(vinMota).build()
        try {
            Log.d("GRPC", "🔄 A abrir canal de Stream para a mota: $vinMota")

            // O gRPC em Kotlin é tão inteligente que transforma o Stream automaticamente num Flow!
            stub.streamMotaUpdates(request).collect { response ->
                Log.d("GRPC", "⚡ Novo pacote recebido! Bateria: ${response.batteryLevel}%")
                emit(response) // Envia o dado fresco para o ViewModel
            }
        } catch (e: Exception) {
            Log.e("GRPC", "❌ ERRO no Stream gRPC: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
}
