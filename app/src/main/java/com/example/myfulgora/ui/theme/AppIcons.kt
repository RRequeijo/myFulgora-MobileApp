package com.example.myfulgora.ui.theme

import com.example.myfulgora.R

// Isto funciona como a tua "Pasta de Ícones"
object AppIcons {

    // Grupo: Navegação (Barra de Baixo)
    object Navbar {
        val Map = R.drawable.bottom_navigation_map
        val Battery = R.drawable.bottom_navigation_battery
        val Home = R.drawable.bottom_navigation_home
        val Social = R.drawable.bottom_navigation_community
        val Performance = R.drawable.bottom_navigation_performance
    }

    //Menu Hamburguer
    object Menu {
        val Profile = R.drawable.profile_hamburguer
        val Settings = R.drawable.settings
        val Help = R.drawable.help_ico
        val Logout = R.drawable.logout
        val Delegation = R.drawable.key_delegation
    }



    // Grupo: Dashboard (Ecrã Principal)
    object Dashboard {
        val BatteryTop = R.drawable.battery_charged

        val MainBike = R.drawable.mota_crop
        val ArrowLeft0 = R.drawable.arrow_bike_left
        val ArrowLeft1 = R.drawable.arrow_bike_left_color
        val ArrowRight0 = R.drawable.arrow_bike_right
        val ArrowRight1 = R.drawable.arrow_bike_right_color

        //icones infocard
        val Battery = R.drawable.battery_bottom
        val Power = R.drawable.power_bolt
        val Bike = R.drawable.bike_mileage
        val Status = R.drawable.info_status



    }

    // Grupo: Ações
    object Actions {
        val Button = R.drawable.button
        val ArrowLeft0 = R.drawable.arrow_bike_left
        val ArrowLeft1 = R.drawable.arrow_bike_left_color
        val ArrowRight0 = R.drawable.arrow_bike_right
        val ArrowRight1 = R.drawable.arrow_bike_right_color
        val DropDown = R.drawable.dropdown_arrow

    }

    object Battery {
        val Battery = R.drawable.battery_bottom
        val Range = R.drawable.battery_tab_icon
        val Charging = R.drawable.battery_tab_icon_2
        //bateria grande
        val BigBattery = R.drawable.battery_charged_2
        val BigBatteryCharging = R.drawable.battery_charging

        //infocards
        val BatteryHealth = R.drawable.battery_tab_icon_3
        val BatteryTemperature = R.drawable.battery_tab_icon_4
        val BatteryConsumption = R.drawable.battery_tab_icon_5
        val BatteryChargingCycles = R.drawable.battery_tab_icon_6


    }

    object Performance {
        val tyre_pressure = R.drawable.performance_tyre_pressure
        val next_service = R.drawable.performance_service_due
        val performance = R.drawable.performance_performance
        val odometer = R.drawable.performance_odometer

    }

    object Social{
        val epoints = R.drawable.social_epoints
        val top_rider = R.drawable.social_top_rider
        val rewards = R.drawable.social_rewards
    }

    object Profile{
        val profile = R.drawable.profile_img
    }

}