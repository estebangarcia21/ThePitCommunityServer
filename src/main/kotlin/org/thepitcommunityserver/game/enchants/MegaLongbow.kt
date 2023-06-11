package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.seconds
import org.thepitcommunityserver.util.undefPropErr

object MegaLongbow : Enchant {
    override val config: EnchantConfig
    get() = EnchantConfig(
        name = "Mega Longbow",
        tiers = listOf(1, 2, 3),
        group = EnchantGroup.B,
        rare = false,
        type = EnchantType.BOW
    ) { "One shot per second, this bow is<br/>automatically fully drawn and<br/>grants <green>Jump Boost ${intToRoman(amplifier[it])}</green> (2s)" }

    private val amplifier = mapOf(
        1 to 2,
        2 to 3,
        3 to 4
    )

    private val timer = Timer()
    private val cooldown = 1L.seconds()

    @EventHandler
    fun onArrowShoot(event: EntityShootBowEvent) {
        event.arrowShotWithEnchant(this) { damager, tier, ctx ->
            val arrow = ctx.arrow
            val amplifier = amplifier[tier] ?: undefPropErr("amplifier", tier)

            timer.cooldown(damager.uniqueId, cooldown) {
                arrow.isCritical = true
                arrow.velocity = damager.location.direction.multiply(100.0)
                damager.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 2L.seconds().toInt(), amplifier, true))
            }
        }
    }
}
