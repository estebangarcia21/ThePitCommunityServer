package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.util.Vector
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr

object Knockback : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Knockback",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.SWORD
        ) { "Increases knockback taken by<br/>enemies by <white>${knockbackAmount[it]} blocks</white>" }

    private val knockbackAmount = mapOf(
        1 to 3,
        2 to 6,
        3 to 9
    )

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this){
            val damaged = it.damaged

            val knockbackAmount = knockbackAmount[it.enchantTier] ?: undefPropErr("knockbackAmount", it.enchantTier)

            damaged.velocity = Vector(
                damaged.velocity.x * knockbackAmount,
                damaged.velocity.y * knockbackAmount,
                damaged.velocity.z * knockbackAmount
            )
        }
    }
}
