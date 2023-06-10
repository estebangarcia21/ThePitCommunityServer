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
            type = EnchantType.BOW,
            description
        )

    private val arrowsGiven = mapOf(
        1 to 1,
        2 to 3,
        3 to 8
    )

    private val description: EnchantDescription = { "Get <white>${arrowsGiven[it]} arrows</white> on arrow hit" }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerArrowHitPlayerWithEnchant(this) { damager, _, tier, _ ->
            val arrows = arrowsGiven[tier]?.let { ItemStack(Material.ARROW, it) } ?: error("arrows is undefined for tier $tier")

            damager.inventory.addItem(arrows)
        }
    }
}
