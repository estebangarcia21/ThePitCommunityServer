package org.thepitcommunityserver.util

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import net.citizensnpcs.trait.LookClose
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.registerEvents

private val CITIZENS_REGISTRY = CitizensAPI.getNPCRegistry()

val worldNPCS = listOf(
    NPC(
        name = listOf(
            "<gold><bold>ITEMS</bold></gold>",
            "<gray>Non-permanent items</gray>"
        ).map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        location = CurrentWorldConfig.shopVillager.toLocation(),
        gui = GUI(
            title = "Non-permanent items",
            rows = 3,
            contents = mapOf(
                11 to ItemStack(Material.DIAMOND_SWORD),
                12 to ItemStack(Material.OBSIDIAN, 8),
                13 to ItemStack(Material.GOLD_PICKAXE),
                14 to ItemStack(Material.DIAMOND_CHESTPLATE),
                15 to ItemStack(Material.DIAMOND_BOOTS)
            ),
            readOnly = true
        ),
        nameHeight = 1.9,
        initialRotation = 180f
    ),
    NPC(
        name = listOf(
            "<green><bold>UPGRADES</bold></green>",
            "<gray>Permanent</gray>"
        ).map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        location = CurrentWorldConfig.perkVillager.toLocation(),
        gui = GUI(
            title = "Permanent upgrades",
            rows = 5,
            contents = mapOf(),
            readOnly = true
        ),
        nameHeight = 1.9,
        initialRotation = 180f
    ),
    NPC(
        name = listOf(
            "<yellow><bold>PRESTIGE</bold></yellow>",
            "<gray>Resets & Renown</gray>"
        ).map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        location = CurrentWorldConfig.prestigeVillager.toLocation(),
        gui = GUI(
            title = "Prestige",
            rows = 5,
            contents = mapOf(),
            readOnly = true
        ),
        nameHeight = 1.9,
        initialRotation = 0f
    ),
    NPC(
        name = listOf(
            "<aqua><bold>QUEST MASTER</bold></aqua>",
            "<gray>Quests & Contracts</gray>"
        ).map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        location = CurrentWorldConfig.questMaster.toLocation(),
        gui = GUI(
            title = "Prestige",
            rows = 5,
            contents = mapOf(),
            readOnly = true
        ),
        nameHeight = 1.9,
        initialRotation = 90f
    ),
)

class NPC(
    name: List<String>,
    nameHeight: Double = 0.0,
    type: EntityType,
    private val location: Location,
    private val gui: GUI,
    private val initialRotation: Float = 0.0f
) : Listener {
    private val npc = CITIZENS_REGISTRY.createNPC(type, "")
    private val openCooldowns = Timer<Player>()
    private val nameHologram = Hologram(name, location.clone().add(org.bukkit.util.Vector(0.0, nameHeight, 0.0)))

    init {
        registerEvents(this)
        setup()
    }

    fun spawn() {
        npc.setAlwaysUseNameHologram(false)

        location.yaw = initialRotation
        npc.spawn(location)
        npc.entity.isCustomNameVisible = false

        nameHologram.show()
    }

    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        val player = event.clicker
        val eventNPC = event.npc

        if (npc.id != eventNPC.id) return

        openCooldowns.cooldown(player, 1 * SECONDS, cooldownAction = {
            player.sendMessage(replaceChatColorTags("<red><bold>HOLD IT!</bold></red> You are going too fast!"))
        }) {
            gui.open(player)
        }
    }

    private fun setup() {
        val lookCloseTrait = CitizensAPI.getTraitFactory().getTrait(LookClose::class.java)
        lookCloseTrait.range = 5.0
        lookCloseTrait.setRealisticLooking(true)
        lookCloseTrait.toggle()

        npc.addTrait(lookCloseTrait)
    }
}

fun deregisterAllNPCs() {
    CITIZENS_REGISTRY.deregisterAll()
}