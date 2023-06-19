package org.thepitcommunityserver.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import java.io.InputStreamReader

val world: World = Bukkit.getWorld("world")
val currentMap = getMapData().elemental

typealias DeserializedLocation = List<Double>

data class MapConfiguration(
    val elemental: MapData
)

data class MapData(
    // Hologram locations.
    val shopVillager: DeserializedLocation,
    val perkVillager: DeserializedLocation,
    val prestigeVillager: DeserializedLocation,
    val questMaster: DeserializedLocation,
    val leaderboard: DeserializedLocation,
    val statsVillager: DeserializedLocation,
    val unlockedFeatures: DeserializedLocation,
    val enderChest: DeserializedLocation,
    val keeper: DeserializedLocation,
    val centerHologram: DeserializedLocation,

    // Misc.
    val spawnPoints: List<DeserializedLocation>,
    val centerDropdown: CenterDropdown,
    val bounds: BoundsConfiguration,
)

data class BoundsConfiguration(
    val spawn: Bounds
)

data class Bounds(
    val lower: DeserializedLocation,
    val upper: DeserializedLocation
)

data class CenterDropdown(
    val pos: DeserializedLocation,
    val radius: Double
)

fun getMapData(): MapConfiguration {
    val yamlFilePath = "maps.yaml"
    val yamlInputStream = object {}.javaClass.getResourceAsStream("/$yamlFilePath")
    val yamlString = yamlInputStream?.let { inputStream -> InputStreamReader(inputStream).use { it.readText() } }

    val objectMapper = ObjectMapper(YAMLFactory())
    val jsonNode = objectMapper.readTree(yamlString)

    val gson = Gson()

    return gson.fromJson(jsonNode.toString(), MapConfiguration::class.java)
}
