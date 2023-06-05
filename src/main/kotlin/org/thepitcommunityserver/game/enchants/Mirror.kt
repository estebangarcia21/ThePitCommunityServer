package org.thepitcommunityserver.game.enchants

import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.formatPercentage

object Mirror : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Mirror",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.PANTS,
            description
        )

    private val description: EnchantDescription = { tier ->
        if (tier == 1) {
            "You are immune to true damage"
        } else {
            "You do not take true damage and<br/>instead reflect <yellow>${formatPercentage(reflectionAmounts[tier])}</yellow> of it to<br/>your attacker"
        }
    }

    val reflectionAmounts = mapOf(
        1 to 0.0,
        2 to 0.25,
        3 to 0.5
    )
}
