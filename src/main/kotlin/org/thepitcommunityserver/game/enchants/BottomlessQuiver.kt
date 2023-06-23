package org.thepitcommunityserver.game.enchants

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.*

object BottomlessQuiver : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Bottomless Quiver",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.BOW
        ) { "Get <white>${arrowsGiven[it]} arrows</white> on arrow hit" }

    private val arrowsGiven = mapOf(
        1 to 1,
        2 to 3,
        3 to 8
    )

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) {
            val arrows = arrowsGiven[it.enchantTier]?.let { ItemStack(Material.ARROW, it) } ?: error("arrows is undefined for tier ${it.enchantTier}")

            it.damager.inventory.addItem(arrows)
        }
    }
}
