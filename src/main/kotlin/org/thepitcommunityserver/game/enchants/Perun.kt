package org.thepitcommunityserver.game.enchants

import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.HitCounter
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr
import java.util.*

object Perun : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Perun's Wrath",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.A,
            rare = true,
            type = EnchantType.SWORD,
            description
        )

    private val damageAmount = mapOf(
        1 to 3.0,
        2 to 4.0,
        3 to 2.0
    )

    private val hitCounter = HitCounter<UUID>()

    private val hitsNeeded = mapOf(
        1 to 5,
        2 to 4,
        3 to 4
    )

    val hearts = damageAmount.mapValues { it.value / 2f}

    private val description: EnchantDescription = {
        val lastMessage = ChatColor.ITALIC.toString() + "Lightning deals true damage"

        val word = mapOf(
            1 to "fifth",
            2 to "fourth",
            3 to "fourth"
        )[it]

        if (it == 3) {
            "Every <yellow>$word</yellow> hit strikes<br/><yellow>lightning</yellow> for <red>${hearts[it]}❤</red> + " +
                    "<red>1❤</red><br/>per <aqua>diamond piece</aqua> on your<br/>victim.<br/>$lastMessage"
        } else {
            "Every <yellow>$word</yellow> hit strikes<br/><yellow>lightning</yellow> for <red>${hearts[it]}❤</red><br/>$lastMessage"
        }
    }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this){
            val damager = it.damager
            val damaged = it.damaged

            var damageAmount = damageAmount[it.enchantTier] ?: undefPropErr("damageAmount", it.enchantTier)
            val hitsNeeded = hitsNeeded[it.enchantTier] ?: undefPropErr("hitsNeeded", it.enchantTier)

            hitCounter.onNthHit(damager.uniqueId, hitsNeeded) {
                if (it.enchantTier == 3) {
                    damaged.inventory.armorContents.forEach{ a ->
                        if (a == null) return@forEach

                        damageAmount += if (a.type.name.split("_")[0].equals("diamond", ignoreCase = true)) 2 else 0
                    }
                }

                damager.world.strikeLightningEffect(damaged.location)
                DamageManager.applyTrueDamage(damaged, damager, damageAmount)
            }
        }
    }
}