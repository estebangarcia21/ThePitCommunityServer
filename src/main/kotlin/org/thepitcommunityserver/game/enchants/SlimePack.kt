package org.thepitcommunityserver.game.enchants

import org.bukkit.event.EventHandler
import org.thepitcommunityserver.game.enchants.lib.*

object SlimePack : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Slime Pack",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.BOW
        ) { "Embrace the malevolent power of <red>'Slime Pack'</red>: Summon colossal terror every 10s, as a nightmarish abomination devours all in its path." }

    @EventHandler
    fun onArmorChange(event: ArmorChangeEvent) {
        event.stream { e -> }
    }
}
