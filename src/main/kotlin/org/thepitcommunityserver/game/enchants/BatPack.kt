package org.thepitcommunityserver.game.enchants

import org.bukkit.Effect
import org.bukkit.entity.Endermite
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.SlimeSplitEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.SECONDS
import org.thepitcommunityserver.util.TICK
import org.thepitcommunityserver.util.Timer

object BatPack : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Bat Pack",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = false,
            type = EnchantType.BOW
        ) { "Embrace the malevolent power of <red>'Bat Pack'</red>:<br/>Summon colossal terror every 5s, as a<br/>nightmarish abomination devours all in its path." }

    private val tracker = LeggingsEnchantTracker(this, ::onEquip, ::onUnequip)
    private val timer = Timer<Player>()
    private val entityTimer = Timer<Entity>()

    private fun onEquip(player: Player, tier: Int) {
        spawnEntity(player, tier)
    }

    private fun spawnEntity(player: Player, tier: Int) {
        timer.after(player, 2 * TICK) {
            val entity = player.world.spawnEntity(player.location, EntityType.ENDERMITE) as Endermite

            entity.addPotionEffect(PotionEffect(PotionEffectType.SPEED, (60L * SECONDS).toInt(), 50))
            entityTimer.after(entity, 5 * SECONDS) {
                if (entity.isValid) {
                    entity.world.playEffect(entity.location, Effect.EXPLOSION_LARGE, Effect.EXPLOSION_LARGE.data, 100)
                }

                entity.remove()
            }

            spawnEntity(player, tier)
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
