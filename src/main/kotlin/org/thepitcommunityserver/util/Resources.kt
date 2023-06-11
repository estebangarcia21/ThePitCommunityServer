package org.thepitcommunityserver.util

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

data class MapsData(
    val elemental: ElementalData
)

data class ElementalData(
    val shopVillager: List<Double>,
    val perkVillager: List<Double>,
    val prestigeVillager: List<Double>,
    val questMaster: List<Double>,
    val leaderboard: List<Double>,
    val statsVillager: List<Double>,
    val unlockedFeatures: List<Double>,
    val enderChest: List<Double>,
    val keeper: List<Double>,
    val spawnPoints: List<List<Double>>
)

fun getMapData(): MapsData {
    val jsonFilePath = "maps.json"
    val jsonInputStream = object {}.javaClass.getResourceAsStream("/$jsonFilePath")
    val jsonString = jsonInputStream?.let { InputStreamReader(it) }?.let { it -> BufferedReader(it).use { it.readText() } }

    val gson = Gson()

    return gson.fromJson(jsonString, MapsData::class.java)
}
