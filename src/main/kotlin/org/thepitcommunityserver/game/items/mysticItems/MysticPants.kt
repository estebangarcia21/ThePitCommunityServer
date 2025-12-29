package org.thepitcommunityserver.game.items.mysticItems

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.NBT
import org.thepitcommunityserver.util.buildItem

enum class PantsColor(val displayName: String, val colorKey: String, val textColor: String) {
    RED("Red", "red", "red"),
    BLUE("Blue", "blue", "blue"),
    GREEN("Green", "green", "green"),
    YELLOW("Yellow", "yellow", "yellow"),
    ORANGE("Orange", "orange", "gold"),
    BLACK("Black", "black", "dark-purple"),
    SEWER("Sewer", "sewer", "dark-aqua"),
    RAGE("Rage", "rage", "dark-red"),
    AqUA("Aqua", "aqua", "aqua"),
}

object MysticPants : Item {
    override val config = ItemConfig(
        name = "Fresh Pants",
        material = Material.LEATHER_LEGGINGS,
    )

    override fun build(player: Player): ItemStack {
        return buildFreshPants(player, PantsColor.RED)
    }

    fun buildFreshPants(player: Player, color: PantsColor): ItemStack {
        return buildItem(
            name = "Fresh ${color.displayName} Pants",
            material = Material.LEATHER_LEGGINGS,
            lore = listOf(
                "",
                "<${color.textColor}>Used in the mystic well",
                "<${color.textColor}>Also, a fashion statement",
            ),
            unbreakable = true,
            flags = listOf(ItemFlag.HIDE_ATTRIBUTES),
            nbtTags = mapOf(
                NBT.KEPT_ON_DEATH.entry,
            ),
            itemColor = color.textColor,
            player = player
        )
    }
}