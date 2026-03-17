package com.example.myfulgora.data.helpers

object UnitConverter {

    // Função para converter e formatar Distância (ex: Odómetro, Autonomia)
    fun formatDistance(km: Int, useMetric: Boolean): String {
        return if (useMetric) {
            "$km km"
        } else {
            val miles = km * 0.621371
            // Formata para ter apenas 1 casa decimal (ex: "12.4 mi" em vez de "12.43232 mi")
            String.format("%.1f mi", miles)
        }
    }

    // Já te deixo aqui a função de Velocidade preparada para o futuro!
    fun formatSpeed(kmh: Int, useMetric: Boolean): String {
        return if (useMetric) {
            "$kmh km/h"
        } else {
            val mph = kmh * 0.621371
            "${mph.toInt()} mph" // Velocidade costuma ser um número inteiro
        }
    }
}