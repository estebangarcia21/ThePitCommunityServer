package org.thepitcommunityserver.game.enchants

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.Text
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr

object Executioner : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Executioner",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.SWORD
        ) { "Hitting an enemy below <red>${hearts[it]}${Text.HEART}</red><br/>instantly kills them" }

    private val killAmount = mapOf(
        1 to 3.0,
        2 to 4.0,
        3 to 5.0,
    )

    private val hearts = killAmount.mapValues { it.value / 2f }

    @EventHandler
    fun onDamageEvent(event : EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged

            val killAmount = killAmount[it.enchantTier] ?: undefPropErr("hearts", it.enchantTier)

            if (damaged.health <= killAmount) {
                damaged.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "EXECUTED!" + ChatColor.GRAY + " by "
                        + damager.name
                        + damaged.name + ChatColor.GRAY + " (insta-kill below " + ChatColor.RED
                        + killAmount / 2f + "❤" + ChatColor.GRAY + ")"
                )

                damaged.world.playSound(damaged.location, Sound.VILLAGER_DEATH, 1f, 0.5f)
                damaged.health = 0.0
            }
        }
    }
}