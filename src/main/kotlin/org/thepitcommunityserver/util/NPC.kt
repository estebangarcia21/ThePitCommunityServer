package org.thepitcommunityserver.util

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.thepitcommunityserver.db.data
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
            onOpen = { player ->
                val gold = player.data.gold

                fun purchaseableLore(price: Double, vararg lore: String): List<String> {
                    val purchaseMessage =
                        if (gold >= price) "<yellow>Click to purchase!</yellow>" else "<red>Not enough gold!</red>"

                    return buildLore(
                        *lore,
                        "",
                        "<italic>Lost on death</italic>",
                        "Cost: <gold>${price.toInt()}g</gold>",
                        purchaseMessage,
                        defaultColor = ChatColor.GRAY
                    )
                }

                fun purchaseableName(price: Double, name: String): String {
                    val nameColor = if (gold >= price) "yellow" else "red"

                    return "<$nameColor>$name</$nameColor>".parseChatColors()
                }

                val diamondSwordPrice = 150.0
                val obsidianPrice = 40.0
                val goldenPickaxePrice = 40.0
                val diamondChestplatePrice = 500.0
                val diamondBootsPrice = 300.0

                setContents(
                    mapOf(
                        11 to buildItem(
                            name = purchaseableName(diamondSwordPrice, "Diamond Sword"),
                            material = Material.DIAMOND_SWORD,
                            itemID = "diamond-sword",
                            lore = purchaseableLore(
                                diamondSwordPrice,
                                "<blue>+20% damage vs bountied"
                            ),
                            flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
                        ),
                        12 to buildItem(
                            name = purchaseableName(obsidianPrice, "Obsidian"),
                            material = Material.OBSIDIAN,
                            itemID = "obsidian",
                            lore = purchaseableLore(
                                obsidianPrice,
                                "Remains for 120 seconds"
                            ),
                            count = 8
                        ),
                        13 to buildItem(
                            name = purchaseableName(goldenPickaxePrice, "Gold Pickaxe"),
                            material = Material.GOLD_PICKAXE,
                            itemID = "gold-pickaxe",
                            lore = purchaseableLore(
                                goldenPickaxePrice,
                                "Breaks a 5-high pillar of",
                                "obsidian when 2-tapping it"
                            ),
                            flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
                        ),
                        14 to buildItem(
                            name = purchaseableName(diamondChestplatePrice, "Diamond Chestplate"),
                            material = Material.DIAMOND_CHESTPLATE,
                            itemID = "diamond-chestplate",
                            lore = purchaseableLore(
                                diamondChestplatePrice,
                                "Auto-equips on buy!"
                            )
                        ),
                        15 to buildItem(
                            name = purchaseableName(diamondBootsPrice, "Diamond Boots"),
                            material = Material.DIAMOND_BOOTS,
                            itemID = "diamond-boots",
                            lore = purchaseableLore(
                                diamondBootsPrice,
                                "Auto-equips on buy!"
                            )
                        )
                    )
                )
            },
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
}

fun deregisterAllNPCs() {
    CITIZENS_REGISTRY.deregisterAll()
}
