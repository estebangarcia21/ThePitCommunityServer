package org.thepitcommunityserver.util

import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.BufferedReader
import java.io.InputStreamReader

val world = Bukkit.getWorld("world")
val currentMap = getMapData().elemental

data class MapConfiguration(
    val elemental: MapData
)

data class MapData(
    val shopVillager: List<Double>,
    val perkVillager: List<Double>,
    val prestigeVillager: List<Double>,
    val questMaster: List<Double>,
    val leaderboard: List<Double>,
    val statsVillager: List<Double>,
    val unlockedFeatures: List<Double>,
    val enderChest: List<Double>,
    val keeper: List<Double>,
    val spawnPoints: List<List<Double>>,
    val bounds: BoundsConfiguration
)

data class BoundsConfiguration(
    val spawn: Bounds
)

data class Bounds(
    val lower: List<Double>,
    val upper: List<Double>
)

fun getMapData(): MapConfiguration {
    val jsonFilePath = "maps.json"
    val jsonInputStream = object {}.javaClass.getResourceAsStream("/$jsonFilePath")
    val jsonString = jsonInputStream?.let { InputStreamReader(it) }?.let { it -> BufferedReader(it).use { it.readText() } }

    val gson = Gson()

    return gson.fromJson(jsonString, MapConfiguration::class.java)
}
