package org.thepitcommunityserver.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.util.Vector
import java.io.InputStreamReader

val CurrentWorld: World = Bukkit.getWorld("world")
val CurrentWorldConfig = getMapData().elemental

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
    val spawnPoints: List<SpawnPoint>,
    val centerDropdown: CenterDropdown,
    val bounds: BoundsConfiguration,
)

data class SpawnPoint(
    val pos: DeserializedLocation,
    val rot: Double
) {
    fun toBukkitVector(): Vector {
        require(pos.size == 3) { "List must be a of size 3 to convert to org.bukkit.util.vector (x, y, z)" }

        return Vector(pos[0], pos[1], pos[2])
    }

    fun toLocation(world: World = CurrentWorld): Location {
        return pos.toLocation(world = world, rot = rot.toFloat())
    }
}

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

val randomSpawnLocation: Location
    get() = CurrentWorldConfig.spawnPoints.random().toLocation()

fun getMapData(): MapConfiguration {
    val yamlFilePath = "maps.yaml"
    val yamlInputStream = object {}.javaClass.getResourceAsStream("/$yamlFilePath")
    val yamlString = yamlInputStream?.let { inputStream -> InputStreamReader(inputStream).use { it.readText() } }

    val objectMapper = ObjectMapper(YAMLFactory())
    val jsonNode = objectMapper.readTree(yamlString)

    val gson = Gson()

    return gson.fromJson(jsonNode.toString(), MapConfiguration::class.java)
}

fun DeserializedLocation.toBukkitVector(): Vector {
    require(this.size == 3) { "List must be a of size 3 to convert to org.bukkit.util.vector (x, y, z)" }

    return Vector(this[0], this[1], this[2])
}

fun DeserializedLocation.toLocation(world: World = CurrentWorld, rot: Float? = null): Location {
    val loc = this.toBukkitVector().toLocation(world)
    if (rot != null) {
        loc.yaw = rot
    }

    return loc
}
