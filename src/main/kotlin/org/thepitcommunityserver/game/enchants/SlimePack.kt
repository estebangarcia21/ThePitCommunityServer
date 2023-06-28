package org.thepitcommunityserver.game.enchants

import org.bukkit.Effect
import org.bukkit.EntityEffect
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.Slime
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.SlimeSplitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.TICK
import org.thepitcommunityserver.util.Timer

object SlimePack : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Slime Pack",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.BOW
        ) { "Embrace the malevolent power of <red>'Slime Pack'</red>:<br/>Summon colossal terror every 5s, as a<br/>nightmarish abomination devours all in its path." }

    private val tracker = LeggingsEnchantTracker(this, ::onEquip, ::onUnequip)
    private val timer = Timer<Player>()
    private val slimeTimer = Timer<Slime>()

    private fun onEquip(player: Player, tier: Int) {
        spawnSlimy(player, tier)
    }

    private fun spawnSlimy(player: Player, tier: Int) {
        timer.after(player, 2 * TICK) {
            val slime = player.world.spawnEntity(player.location, EntityType.SLIME) as Slime
            slime.size = 1

            slime.addPotionEffect(PotionEffect(PotionEffectType.SPEED, (60L * SECONDS).toInt(), 50))
            slimeTimer.after(slime, 5 * SECONDS) {
                if (slime.isValid) {
                    slime.world.playEffect(slime.location, Effect.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE.data, 100)
                }

                slime.remove()
            }

            spawnSlimy(player, tier)
        }
    }

    private fun onUnequip(player: Player, tier: Int) {
        timer.stop(player)
    }

    @EventHandler
    fun onArmorChange(event: ArmorChangeEvent) {
        tracker.bind(event)
    }

    @EventHandler
    fun onSlimeSplit(event: SlimeSplitEvent) {
        event.isCancelled = true
    }
}
