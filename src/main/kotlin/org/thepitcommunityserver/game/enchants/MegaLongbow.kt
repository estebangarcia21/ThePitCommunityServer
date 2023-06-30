package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object MegaLongbow : Enchant {
    override val config: EnchantConfig
    get() = EnchantConfig(
        name = "Mega Longbow",
        tiers = listOf(1, 2, 3),
        group = EnchantGroup.B,
        rare = true,
        type = EnchantType.BOW
    ) { "One shot per second, this bow is<br/>automatically fully drawn and<br/>grants <green>Jump Boost ${intToRoman(amplifier[it]?.inc())}</green> (2s)" }

    private val amplifier = mapOf(
        1 to 1,
        2 to 2,
        3 to 3
    )

    private val timer = Timer<UUID>()
    private val cooldown = Time(1L * SECONDS)
    private val potionCooldown = Time(2L * SECONDS)

    @EventHandler
    fun onArrowShoot(event: EntityShootBowEvent) {
        event.arrowShotWithEnchant(this, getBowOnShoot = true) {
            val arrow = it.arrow
            val damager = it.shooter

            val amplifier = amplifier[it.enchantTier] ?: undefPropErr("amplifier", it.enchantTier)

            timer.cooldown(damager.uniqueId, cooldown.ticks()) {
                arrow.isCritical = true
                arrow.velocity = damager.location.direction.multiply(2.9)
                damager.removePotionEffect(PotionEffectType.JUMP)
                damager.addPotionEffect(PotionEffect(PotionEffectType.JUMP, potionCooldown.ticks().toInt(), amplifier, true))
            }
        }
    }
}
