package org.thepitcommunityserver.game.items


import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.items.shopItems.*
import org.thepitcommunityserver.util.DeserializedNBTMap
import org.thepitcommunityserver.util.NBT
import org.thepitcommunityserver.util.buildItem

val Items = listOf(
    GoldenPickaxe,
    CombatSpade,
    BountySolvent,
    FirstAidEgg,
    JumpBoostPotion,
    TacticalInsertion,
    PantsBundle,
    Obsidian,
    DiamondBoots,
    DiamondSword,
    DiamondChestplate
)

interface Item : Listener {
    val config: ItemConfig
    fun build(player: Player): ItemStack {

        return buildItem(
            name = config.name,
            material = config.material,
            lore = config.lore,
            count = config.count,
            unbreakable = config.unbreakable,
            flags = config.flags,
            nbtTags = config.nbtTags,
            itemColor = config.itemColor,
            data = config.data,
            player = player
        )
    }
}

data class ItemConfig(
    val name: String,
    val material: Material,
    val lore: List<String> = emptyList(),
    val count: Int = 1,
    val itemColor: String? = null,
    val unbreakable: Boolean = false,
    val flags: List<ItemFlag> = listOf(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES),
    val nbtTags: DeserializedNBTMap = mapOf(NBT.LOSE_ON_DEATH.entry),
    val keptOnDeath: Boolean = false,
    val data: Byte? = 0,
)








