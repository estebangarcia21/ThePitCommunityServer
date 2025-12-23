package org.thepitcommunityserver.game.guis

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.db.data
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.defaultItems.IronBoots
import org.thepitcommunityserver.game.items.defaultItems.IronChestplate
import org.thepitcommunityserver.game.items.defaultItems.IronLeggings
import org.thepitcommunityserver.game.items.shopItems.*
import org.thepitcommunityserver.util.*


data class ShopItem(
    val name: String,
    val material: Material,
    val item: Item,
    val price: Double,
    val data: Byte? = 0,
    val count: Int = 1,
    val description: List<String> = emptyList(),
    val autoEquip: EquipSlot? = null,
    val onPurchase: ((Player) -> Unit)? = null,
    val requiredUnlock: String? = null,
    val dynamicBehavior: MultiItem? = null,
)

data class MultiItem(
    val getPrice: (Player) -> Double,
    val getDescription: (Player) -> List<String>,
    val getItems: (Player) -> List<Pair<Item, EquipSlot?>>,
    val canPurchase: (Player) -> Boolean = { getItems(it).isNotEmpty() },
)

data class ToggleItem(
    val isEnabled: (Player) -> Boolean,
    val onToggle: (Player, Boolean) -> Unit,
)

enum class EquipSlot { HELMET, CHESTPLATE, LEGGINGS, BOOTS }

private fun Player.canAfford(price: Double) = data.gold >= price

private fun Player.purchase(price: Double, itemName: String, action: () -> Boolean) {
    if (!canAfford(price)) {
        sendMessage(replaceChatColorTags("<red>Not enough gold!</red>"))
        playSound(location, Sound.VILLAGER_NO, 1.0f, 1.0f)
        return
    }

    if (action()) {
        data.gold -= price
        sendMessage(replaceChatColorTags("<green><bold>PURCHASE!</bold></green> <gold>$itemName</gold>"))
    }
}

private fun Player.giveItem(item: ItemStack, equipSlot: EquipSlot? = null): Boolean {
    fun ItemStack?.isEmpty() = this == null || this.type == Material.AIR

    fun addToInventory(): Boolean {
        if (inventory.firstEmpty() == -1) {
            sendMessage(replaceChatColorTags("<red>Your inventory is full!</red>"))
            return false
        }
        player.inventory.addItem(item)
        playSound(location, Sound.LEVEL_UP, 1f, 2f)
        return true
    }

    fun equipArmor(slot: EquipSlot): Boolean {
        val currentPiece = getEquippedArmorPiece(player, slot)

        if (currentPiece.isEmpty() || isArmorPieceStronger(
                this,
                item.type
            )
        ) {
            setArmorPiece(this, slot, item)
            if (!currentPiece.isEmpty()) {
                addItemToPlayerInventory(this, currentPiece)
            }
            playSound(location, Sound.HORSE_ARMOR, 1f, 1f)
            return true
        }
        return addToInventory()
    }

    return when (equipSlot) {
        EquipSlot.HELMET,
        EquipSlot.CHESTPLATE,
        EquipSlot.LEGGINGS,
        EquipSlot.BOOTS -> equipArmor(equipSlot)

        null -> addToInventory()
    }
}

private fun ShopItem.getEffectivePrice(player: Player): Double =
    dynamicBehavior?.getPrice?.invoke(player) ?: price

private fun ShopItem.getEffectiveDescription(player: Player): List<String> =
    dynamicBehavior?.getDescription?.invoke(player) ?: description

private fun ShopItem.canPurchase(player: Player): Boolean =
    dynamicBehavior?.canPurchase?.invoke(player) ?: true

private fun buildShopItemStack(shopItem: ShopItem, player: Player): ItemStack {
    val gold = player.data.gold
    val price = shopItem.getEffectivePrice(player)
    val description = shopItem.getEffectiveDescription(player)
    val canPurchase = shopItem.canPurchase(player)

    val (nameColor, statusMessage) = when {
        !canPurchase -> "red" to "<red>Cannot purchase</red>"
        gold >= price -> "yellow" to "<yellow>Click to purchase!</yellow>"
        else -> "red" to "<red>Not enough gold!</red>"
    }

    val lore = buildLore(
        *description.toTypedArray(),
        "",
        "<italic>Lost on death.</italic>",
        "Cost: <gold>${price.toInt()}g</gold>",
        statusMessage,
        defaultColor = ChatColor.GRAY
    )

    return buildItem(
        name = "<$nameColor>${shopItem.name}</$nameColor>".parseChatColors(),
        material = shopItem.material,
        data = shopItem.data,
        lore = lore,
        count = shopItem.count,
        flags = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS),
    )
}


private val IronPackBehavior = MultiItem(
    getPrice = { player ->
        var price = 100.0
        if (!isArmorPieceStronger(
                player,
                Material.IRON_CHESTPLATE
            )
        ) price -= 50.0
        if (!isArmorPieceStronger(
                player,
                Material.IRON_LEGGINGS
            )
        ) price -= 25.0
        if (!isArmorPieceStronger(
                player,
                Material.IRON_BOOTS
            )
        ) price -= 25.0
        price
    },
    getDescription = { player ->
        fun armorLine(has: Boolean, name: String) =
            if (has) "<gray><strikethrough>$name</strikethrough></gray>"
            else "<white>$name</white>"

        listOf(
            "Contains:",
            armorLine(
                !isArmorPieceStronger(
                    player,
                    Material.IRON_CHESTPLATE
                ), "Iron Chestplate"
            ),
            armorLine(
                !isArmorPieceStronger(
                    player,
                    Material.IRON_LEGGINGS
                ), "Iron Leggings"
            ),
            armorLine(
                !isArmorPieceStronger(
                    player,
                    Material.IRON_BOOTS
                ), "Iron Boots"
            ),
        )
    },
    getItems = { player ->
        buildList {
            if (isArmorPieceStronger(
                    player,
                    Material.IRON_CHESTPLATE
                )
            ) {
                add(IronChestplate to EquipSlot.CHESTPLATE)
            }
            if (isArmorPieceStronger(
                    player,
                    Material.IRON_LEGGINGS
                )
            ) {
                add(IronLeggings to EquipSlot.LEGGINGS)
            }
            if (isArmorPieceStronger(player, Material.IRON_BOOTS)) {
                add(IronBoots to EquipSlot.BOOTS)
            }
        }
    }
)


private val baseShopItems = listOf(
    if (true) {
        ShopItem(
            name = "Diamond Sword",
            material = Material.DIAMOND_SWORD,
            item = DiamondSword,
            price = 150.0,
            description = listOf("<blue>+20% damage vs bountied</blue>"),
            onPurchase = { player ->
                player.inventory.contents
                    .find { it?.type == Material.IRON_SWORD }
                    ?.let { player.inventory.remove(it) }
            }
        )
    } else {
        ShopItem(
            name = "Diamond Sword",
            material = Material.DIAMOND_SWORD,
            item = DiamondSword,
            price = 150.0,
            description = listOf("<blue>+20% damage vs bountied</blue>"),
            onPurchase = { player ->
                player.inventory.contents
                    .find { it?.type == Material.IRON_SWORD }
                    ?.let { player.inventory.remove(it) }
            }
        )
    },
    ShopItem(
        name = "Obsidian",
        material = Material.OBSIDIAN,
        item = Obsidian,
        count = 8,
        price = 40.0,
        description = listOf("Remains for 120 seconds"),
    ),
    ShopItem(
        name = "Golden Pickaxe",
        material = Material.GOLD_PICKAXE,
        item = GoldenPickaxe,
        price = 500.0,
        description = listOf("Breaks a 5-high pillar of", "obsidian when 2-tapping it"),
    ),
    ShopItem(
        name = "Diamond Chestplate",
        material = Material.DIAMOND_CHESTPLATE,
        item = DiamondChestplate,
        price = 500.0,
        description = listOf("Auto-equips on buy!"),
        autoEquip = EquipSlot.CHESTPLATE
    ),
    ShopItem(
        name = "Diamond Boots",
        material = Material.DIAMOND_BOOTS,
        item = DiamondBoots,
        price = 300.0,
        description = listOf("Auto-equips on buy!"),
        autoEquip = EquipSlot.BOOTS
    ),
)

private val unlockableShopItems = listOf(
    ShopItem(
        name = "Iron Pack",
        material = Material.IRON_CHESTPLATE,
        item = IronChestplate,
        price = 100.0,
        requiredUnlock = "iron_pack",
        dynamicBehavior = IronPackBehavior,
    ),
    ShopItem(
        name = "Obsidian Stack",
        material = Material.OBSIDIAN,
        item = ObsidianStack,
        price = 240.0,
        count = 64,
        description = listOf("A stack of 64 obsidian", "Remains for 120 seconds"),
        requiredUnlock = "obsidian_stack",
    ),
    ShopItem(
        name = "Diamond Leggings",
        material = Material.DIAMOND_LEGGINGS,
        item = DiamondLeggings,
        price = 1200.0,
        description = listOf("Auto-equips on buy!"),
        autoEquip = EquipSlot.LEGGINGS,
        requiredUnlock = "diamond_leggings",
    ),
    ShopItem(
        name = "Combat Spade",
        material = Material.DIAMOND_SPADE,
        item = CombatSpade,
        price = 750.0,
        description = listOf(
            "Deals <blue>+1 damage</blue> per",
            "<aqua>diamond piece</aqua> on enemy.",
            "",
            "<blue>+7 attack damage</blue>"
        ),
        requiredUnlock = "combat_spade",
    ),
    ShopItem(
        name = "Bounty Solvent",
        material = Material.POTION,
        data = 10,
        item = BountySolvent,
        price = 750.0,
        description = listOf(
            "Receive <blue>-30% damage</blue> from players",
            "with a <gold>1000g</gold> bounty.",
            "",
            "Earn <gold>+50% gold</gold> from claimed bounties."
        ),
        requiredUnlock = "bounty_solvent",
    ),
    ShopItem(
        name = "First-Aid Egg",
        material = Material.MONSTER_EGG,
        data = 96,
        item = FirstAidEgg,
        price = 200.0,
        description = listOf("Restores 50% health on use"),
        requiredUnlock = "first_aid_egg",
    ),
    ShopItem(
        name = "Jump Boost IV",
        material = Material.POTION,
        data = 10,
        item = JumpBoostPotion,
        price = 150.0,
        description = listOf("Grants Jump Boost II for 30 seconds"),
        requiredUnlock = "jump_boost_potion",
    ),
    ShopItem(
        name = "Tactical Insertion",
        material = Material.BLAZE_ROD,
        item = TacticalInsertion,
        price = 100.0,
        description = listOf("Sets a respawn point at", "your current location"),
        requiredUnlock = "tactical_insertion",
    ),
    ShopItem(
        name = "Pants Bundle",
        material = Material.MINECART,
        item = PantsBundle,
        price = 50.0,
        description = listOf("Contains 3 random pants items"),
        requiredUnlock = "pants_bundle",
    )
)


private val baseSlots = listOf(11, 12, 13, 14, 15)
private val unlockableSlots = listOf(29, 30, 31, 32, 33, 38, 39, 40, 41, 42)

private fun getShopItemsForPlayer(player: Player): List<Pair<Int, ShopItem>> {
    val unlocks = player.data.shopUnlocks
    val baseWithSlots = baseSlots.zip(baseShopItems)
    val unlockedItems = unlockableShopItems
        .filter { it.requiredUnlock in unlocks }
        .mapIndexed { index, item -> unlockableSlots[index] to item }

    return baseWithSlots + unlockedItems
}

private fun getRowsForPlayerGUI(player: Player): Int {
    val unlocks = player.data.shopUnlocks.size
    return when {
        unlocks > 5 -> 6
        unlocks >= 1 -> 5
        else -> 4
    }
}

fun buildShopGUI(): GUI {
    return GUI(
        title = "Non-permanent items",
        rows = { player -> getRowsForPlayerGUI(player) },
        onOpen = { player ->
            val contents = getShopItemsForPlayer(player).associate { (slot, shopItem) ->
                slot to buildShopItemStack(shopItem, player)
            }
            setContents(contents)
        },
        onClickBuilder = { player ->
            val gui = this
            getShopItemsForPlayer(player).associate { (slot, shopItem) ->
                slot to { ctx: ClickHandlerContext ->
                    val player = ctx.player


                    if (!shopItem.canPurchase(player)) {
                        if (player.inventory.firstEmpty() == -1) {
                            player.sendMessage(replaceChatColorTags("<red>Your inventory is full!</red>"))
                        } else {
                            player.sendMessage(replaceChatColorTags("<red>This item can't be purchased in your current state!</red>"))
                        }

                        player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
                        return@to
                    }

                    val price = shopItem.getEffectivePrice(player)

                    player.purchase(price, shopItem.name) {
                        shopItem.onPurchase?.invoke(player)

                        val success = if (shopItem.dynamicBehavior != null) {
                            val itemsToGive = shopItem.dynamicBehavior.getItems(player)
                            itemsToGive.all { (item, equipSlot) ->
                                player.giveItem(item.build(player), equipSlot)
                            }
                        } else {
                            player.giveItem(shopItem.item.build(player), shopItem.autoEquip)
                        }

                        if (success && shopItem.dynamicBehavior != null) {
                            gui.setContents(mapOf(slot to buildShopItemStack(shopItem, player)), player)
                        }

                        success
                    }
                }
            }
        },
        readOnly = true
    )
}
