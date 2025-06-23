package org.thepitcommunityserver.util

import net.citizensnpcs.api.CitizensAPI
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.db.data

private val CITIZENS_REGISTRY = CitizensAPI.getNPCRegistry()
private val customNpcRegistry = mutableMapOf<Int, NPC>()


// TODO UPDATE: DYNAMICALLY ITEMS ARE CURRENTLY HARDCODED BUT THE INVENOTRY SIZE CHANGES BASED ON CERTAIN PERKS AND STUFF

val worldNPCS = listOf(
    NPC(
        name = listOf(
            "<gold><bold>ITEMS</bold></gold>",
            "<gray>Non-permanent items</gray>"
        ).map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        level = 0,
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
                        "<italic>Lost on death.</italic>",
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
                            lore = purchaseableLore(
                                diamondSwordPrice,
                                "<blue>+20% damage vs bountied"
                            ),
                            flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
                        ),
                        12 to buildItem(
                            name = purchaseableName(obsidianPrice, "Obsidian"),
                            material = Material.OBSIDIAN,
                            lore = purchaseableLore(
                                obsidianPrice,
                                "Remains for 120 seconds"
                            ),
                            count = 8
                        ),
                        13 to buildItem(
                            name = purchaseableName(goldenPickaxePrice, "Gold Pickaxe"),
                            material = Material.GOLD_PICKAXE,
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
                            lore = purchaseableLore(
                                diamondChestplatePrice,
                                "Auto-equips on buy!"
                            )
                        ),
                        15 to buildItem(
                            name = purchaseableName(diamondBootsPrice, "Diamond Boots"),
                            material = Material.DIAMOND_BOOTS,
                            lore = purchaseableLore(
                                diamondBootsPrice,
                                "Auto-equips on buy!"
                            )
                        )
                    )
                )
            },
            clickHandlers = mutableMapOf(
                11 to { ctx ->
                    val player = ctx.player
                    val gold = player.data.gold
                    val price = 150.0

                    if (gold < price) {
                        player.sendMessage(replaceChatColorTags("<red>Not enough gold!</red>"))
                        player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
                        return@to
                    }

                    player.data.gold -= price

                    val ironSwordItem = player.inventory.contents.find { it?.type == Material.IRON_SWORD }
                    if (ironSwordItem != null) {
                        player.inventory.remove(ironSwordItem)
                    }

                    player.inventory.addItem(
                        buildItem(
                            name = "Diamond Sword",
                            material = Material.DIAMOND_SWORD,
                            flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
                        )
                    )

                    player.sendMessage(replaceChatColorTags("<bold><green>PURCHASE!</green></bold> <gold>Diamond Sword</gold>"))
                    player.playSound(player.location, Sound.LEVEL_UP, 1.0f, 1.0f)

                },
                12 to { ctx ->
                    val player = ctx.player
                    val gold = player.data.gold
                    val price = 40.0

                    if (gold < price) {
                        player.sendMessage(replaceChatColorTags("<red>Not enough gold!</red>"))
                        player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
                        return@to
                    }

                    player.data.gold -= price
                    player.inventory.addItem(
                        buildItem(
                            name = "Obsidian",
                            material = Material.OBSIDIAN,
                            count = 8
                        )
                    )

                    player.sendMessage(replaceChatColorTags("<green><bold>PURCHASE!</bold></green> <gold>Obsidian</gold>"))
                    player.playSound(player.location, Sound.LEVEL_UP, 1.0f, 1.0f)
                },
                13 to { ctx ->
                    val player = ctx.player
                    val gold = player.data.gold
                    val price = 40.0

                    if (gold < price) {
                        player.sendMessage(replaceChatColorTags("<red>Not enough gold!</red>"))
                        player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
                        return@to
                    }

                    player.data.gold -= price
                    player.inventory.addItem(
                        buildItem(
                            name = "<gold>Golden Pickaxe</gold>".parseChatColors(),
                            material = Material.GOLD_PICKAXE,
                            lore = listOf(
                                "Breaks a 5-high pillar of obsidian",
                                "when 2-tapping it."
                            ).map(::replaceChatColorTags),
                            flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES)
                        )
                    )
                    player.sendMessage(replaceChatColorTags("<green><bold>PURCHASE!</bold></green> <gold>Golden Pickaxe</gold>"))
                    player.playSound(player.location, Sound.LEVEL_UP, 1.0f, 1.0f)

                },
                14 to { ctx ->
                    val player = ctx.player
                    val gold = player.data.gold
                    val price = 500.0

                    if (gold < price) {
                        player.sendMessage(replaceChatColorTags("<red>Not enough gold!</red>"))
                        player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
                        return@to
                    }

                    player.data.gold -= price
                    if (player.inventory.chestplate != ItemStack(Material.DIAMOND_CHESTPLATE)) {
                        player.inventory.chestplate = ItemStack(Material.DIAMOND_CHESTPLATE)
                    } else {
                        player.inventory.addItem(ItemStack(Material.DIAMOND_CHESTPLATE, 1))
                    }
                    player.sendMessage(replaceChatColorTags("<green><bold>PURCHASE!</bold></green> <gold>Diamond Chestplate</gold>"))
                    player.playSound(player.location, Sound.HORSE_ARMOR, 1.0f, 1.0f)
                },
                15 to { ctx ->
                    val player = ctx.player
                    val gold = player.data.gold
                    val price = 300.0

                    if (gold < price) {
                        player.sendMessage(replaceChatColorTags("<red>Not enough gold!</red>"))
                        player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
                        return@to
                    }

                    player.data.gold -= price
                    if (player.inventory.boots != ItemStack(Material.DIAMOND_BOOTS)) {
                        player.inventory.boots = ItemStack(Material.DIAMOND_BOOTS, 1)
                    } else {
                        player.inventory.addItem(ItemStack(Material.DIAMOND_BOOTS, 1))
                    }
                    player.sendMessage(replaceChatColorTags("<bold><green>PURCHASE!</green></bold> <gold>Diamond Boots</gold>"))
                    player.playSound(player.location, Sound.HORSE_ARMOR, 1.0f, 1.0f)
                }
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
        level = 10,
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
            "<gray>Prestige & Renown</gray>"
        ).map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        level = 120,
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
        level = 30,
        location = CurrentWorldConfig.questMaster.toLocation(),
        gui = GUI(
            title = "Quests & Contracts",
            rows = 5,
            contents = mapOf(),
            readOnly = true
        ),
        nameHeight = 1.9,
        initialRotation = 75f
    ),
    NPC(
        name = listOf(
            "<dark-aqua:bold>STATS VILLAGER</dark-aqua:bold>",
            "<gray>My pit stats</gray>"
        ).map(::replaceChatColorTags),
        type = EntityType.VILLAGER,
        level = 50,
        location = CurrentWorldConfig.statsVillager.toLocation(),
        gui = GUI(
            title = "Stats",
            rows = 5,
            contents = mapOf(),
            readOnly = true
        ),
        nameHeight = 1.9,
        initialRotation = 105f
    ),
)

object NPCClickHandler : Listener {
    @EventHandler
    fun onNPCRightClick(event: NPCRightClickEvent) {
        val player = event.clicker
        val clickedNpcId = event.npc.id
        val customNpc = customNpcRegistry[clickedNpcId] ?: return

        customNpc.handleClick(player)
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
    private val nameHologram = Hologram(name, location.clone().add(org.bukkit.util.Vector(0.0, nameHeight, 0.0)))

    fun spawn() {
        npc.setAlwaysUseNameHologram(false)

        location.yaw = initialRotation
        npc.spawn(location)
        npc.entity.isCustomNameVisible = false

        nameHologram.show()
        customNpcRegistry[npc.id] = this
    }

    fun handleClick(player: Player) {

        val playerLevel = player.data.level

        if (playerLevel < this.level) {
            player.sendMessage(replaceChatColorTags("<red>You must to be level $level to access this.</red>"))
            player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
            // UNCOMMENT FOR PRODUCTION (TODO: ADD GLOBAL CONFIG)
//            return
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
