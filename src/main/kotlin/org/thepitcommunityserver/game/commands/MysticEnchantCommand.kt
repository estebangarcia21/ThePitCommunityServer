package org.thepitcommunityserver.game.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.thepitcommunityserver.game.enchants.lib.Enchants
import org.thepitcommunityserver.game.enchants.lib.getItemMysticEnchantments
import org.thepitcommunityserver.game.enchants.lib.setItemMysticEnchantments

object MysticEnchantCommand : PluginCommand {
    // /pitenchant <tier> <enchant name>
    override val name = "pitenchant"

    override fun onCommand(sender: CommandSender?, command: Command?, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) return false
        if (!label.equals(name, ignoreCase = true)) return true

        if (args.size < 2) {
            sender.sendMessage("Usage: /pitenchant <tier> <enchant name>")
            return true
        }
        val tier = args.first().toInt()

        val enchantName = args.drop(1).joinToString(" ")

        val targetItem = sender.inventory.itemInHand
        val enchant = Enchants.find { it.config.name.equals(enchantName, ignoreCase = true) }
        if (enchant == null) {
            sender.sendMessage("Could not find an enchant with the name $enchantName")
            return true
        }

        val enchants = getItemMysticEnchantments(targetItem)?.toMutableMap() ?: return true
        enchants[enchant.config.name] = tier

        setItemMysticEnchantments(targetItem, enchants)

        sender.sendMessage("Successfully enchanted.")

        return true
    }
}
