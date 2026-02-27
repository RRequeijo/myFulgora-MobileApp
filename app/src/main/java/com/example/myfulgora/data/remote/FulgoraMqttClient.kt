package com.example.myfulgora.data.remote

import android.util.Log
import com.example.myfulgora.data.model.BikeState
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.mqtt3.Mqtt3AsyncClient
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.UUID

object FulgoraMqttClient {

    private const val TAG = "FulgoraMqtt"
    private const val BROKER_HOST = "172.20.0.203"
    private const val BROKER_PORT = 1884
    private const val TOPIC_TELEMETRY = "moto/telemetry"

    private var client: Mqtt3AsyncClient? = null

    private val _bikeState = MutableStateFlow(BikeState())
    val bikeState: StateFlow<BikeState> = _bikeState.asStateFlow()

    // Permite que o ViewModel defina o estado inicial (nome da mota, etc)
    fun updateInitialState(newState: BikeState) {
        _bikeState.update { currentState ->
            newState.copy(
                isOnline = currentState.isOnline,
                batteryPercentage = currentState.batteryPercentage,
                speed = currentState.speed
            )
        }
    }

    fun connect() {
        if (client != null && client!!.state.isConnected) return
        client = MqttClient.builder()
            .useMqttVersion3()
            .identifier("android-app-${UUID.randomUUID()}")
            .serverHost(BROKER_HOST)
            .serverPort(BROKER_PORT)
            .automaticReconnectWithDefaultConfig()
            .addConnectedListener {
                _bikeState.update { it.copy(isOnline = true) }
                subscribeToTelemetry()
            }
            .addDisconnectedListener {
                _bikeState.update { it.copy(isOnline = false) }
            }
            .buildAsync()
        client?.connect()
    }

    private fun subscribeToTelemetry() {
        client?.subscribeWith()
            ?.topicFilter(TOPIC_TELEMETRY)
            ?.callback { publish -> handleMessage(publish) }
            ?.send()
    }

    private fun handleMessage(publish: Mqtt3Publish) {
        val payload = String(publish.payloadAsBytes, StandardCharsets.UTF_8)
        try {
            val json = JSONObject(payload)
            _bikeState.update { currentState ->
                val tempDouble = json.optDouble("batteryTemp", currentState.batteryTemp.toDouble())

                currentState.copy(
                    batteryPercentage = json.optInt("batteryPercentage", currentState.batteryPercentage),
                    range = json.optInt("range", currentState.range),
                    isOnline = json.optBoolean("isOnline", currentState.isOnline),
                    isCharging = json.optBoolean("isCharging", currentState.isCharging),
                    batteryCycles = json.optInt("batteryCycles", currentState.batteryCycles),
                    batteryTemp = tempDouble.toInt(),
                    batteryHealth = json.optString("batteryHealth", currentState.batteryHealth),
                    timeLeft = json.optString("timeLeft", currentState.timeLeft)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Erro a fazer parse do JSON de telemetria: $payload", e)
        }
    }
}
