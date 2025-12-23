package org.thepitcommunityserver.util

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.util.Vector
import org.thepitcommunityserver.db.data
import org.thepitcommunityserver.game.guis.buildShopGUI

private val CITIZENS_REGISTRY = CitizensAPI.getNPCRegistry()
private val customNpcRegistry = mutableMapOf<Int, NPC>()

data class NPCDefinition(
    val name: List<String>,
    val level: Int,
    val location: () -> Location,
    val guiTitle: String,
    val guiRows: Int = 5,
    val rotation: Float = 0f,
    val gui: GUI? = null
)

private val npcDefinitions = listOf(
    NPCDefinition(
        name = listOf("<gold><bold>ITEMS</bold></gold>", "<gray>Non-permanent items</gray>"),
        level = 0,
        location = { CurrentWorldConfig.shopVillager.toLocation() },
        guiTitle = "Non-permanent items",
        rotation = 180f,
        gui = buildShopGUI()
    ),
    NPCDefinition(
        name = listOf("<green><bold>UPGRADES</bold></green>", "<gray>Permanent</gray>"),
        level = 10,
        location = { CurrentWorldConfig.perkVillager.toLocation() },
        guiTitle = "Permanent upgrades",
        rotation = 180f
    ),
    NPCDefinition(
        name = listOf("<yellow><bold>PRESTIGE</bold></yellow>", "<gray>Prestige & Renown</gray>"),
        level = 120,
        location = { CurrentWorldConfig.prestigeVillager.toLocation() },
        guiTitle = "Prestige",
        rotation = 0f
    ),
    NPCDefinition(
        name = listOf("<aqua><bold>QUEST MASTER</bold></aqua>", "<gray>Quests & Contracts</gray>"),
        level = 30,
        location = { CurrentWorldConfig.questMaster.toLocation() },
        guiTitle = "Quests & Contracts",
        rotation = 75f
    ),
    NPCDefinition(
        name = listOf("<dark-aqua:bold>STATS VILLAGER</dark-aqua:bold>", "<gray>My pit stats</gray>"),
        level = 50,
        location = { CurrentWorldConfig.statsVillager.toLocation() },
        guiTitle = "Stats",
        rotation = 105f
    )
)

val worldNPCS = npcDefinitions.map { def ->
    NPC(
        name = def.name.map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        level = def.level,
        location = def.location(),
        gui = def.gui ?: GUI(
            title = def.guiTitle,
            contents = mapOf(),
            readOnly = true
        ),
        nameHeight = 1.9,
        initialRotation = def.rotation
    )
}

object NPCClickHandler : Listener {
    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        val customNpc = customNpcRegistry[event.npc.id] ?: return
        customNpc.handleClick(event.clicker)
    }
}

class NPC(
    name: List<String>,
    nameHeight: Double = 0.0,
    type: EntityType,
    private val level: Int,
    private val location: Location,
    private val gui: GUI,
    private val initialRotation: Float = 0.0f
) {
    private val npc = CITIZENS_REGISTRY.createNPC(type, "")
    private val openCooldowns = Timer<Player>()
    private val nameHologram = Hologram(name, location.clone().add(Vector(0.0, nameHeight, 0.0)))

    fun spawn() {
        npc.setAlwaysUseNameHologram(false)
        location.yaw = initialRotation
        npc.spawn(location)
        npc.entity.isCustomNameVisible = false
        nameHologram.show()
        customNpcRegistry[npc.id] = this
    }

    fun handleClick(player: Player) {
        if (player.data.level < level) {
            player.sendMessage(replaceChatColorTags("<red>You must be level $level to access this!</red>"))
            player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
//             return // TODO: UNCOMMENT FOR PRODUCTION
        }

        openCooldowns.cooldown(player, 1 * SECONDS, cooldownAction = {
            player.sendMessage(replaceChatColorTags("<red><bold>HOLD IT!</bold></red> You are going too fast!"))
        }) {
            gui.open(player)
        }
    }
}

fun deregisterAllNPCs() {
    CITIZENS_REGISTRY.deregisterAll()
    customNpcRegistry.clear()
}