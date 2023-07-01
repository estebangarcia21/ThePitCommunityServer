package org.thepitcommunityserver.game.enchants

import net.minecraft.server.v1_8_R3.ItemArmor
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.util.HitCounter
import org.thepitcommunityserver.util.intToRoman
import org.thepitcommunityserver.util.undefPropErr
import java.util.*

object Perun : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Combo: Perun's Wrath",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
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

    val hearts = damageAmount.mapValues { it.value / 2f }

    private val description: EnchantDescription = {
        val lastMessage = ChatColor.ITALIC.toString() + "Lightning deals true damage"

        val word = mapOf(
            1 to "fifth",
            2 to "fourth",
            3 to "fourth"
        )[it]

        if (it == 3) {
            "Every <yellow>$word</yellow> hit strikes<br/><yellow>lightning</yellow> for <red>${hearts[it]}❤</red> + <red>1❤</red><br/>per <aqua>diamond piece</aqua> on your<br/>victim.<br/>$lastMessage"
        } else {
            "Every <yellow>$word</yellow> hit strikes<br/><yellow>lightning</yellow> for <red>${hearts[it]}❤</red><br/>$lastMessage"
        }
    }

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged

            var damageAmount = damageAmount[it.enchantTier] ?: undefPropErr("damageAmount", it.enchantTier)
            val hitsNeeded = hitsNeeded[it.enchantTier] ?: undefPropErr("hitsNeeded", it.enchantTier)

            hitCounter.onNthHit(damager.uniqueId, hitsNeeded) {
                if (it.enchantTier == 3) {
                    damaged.inventory.armorContents.filterNotNull().forEach{ a ->

                        damageAmount += addArmorDamage(a)
                    }
                }

                damager.world.strikeLightningEffect(damaged.location)
                DamageManager.applyTrueDamage(damaged, damager, damageAmount)
            }
        }
    }
    private fun addArmorDamage(armor: ItemStack): Double {
        val name = armor.type.name.split("_")[0]

        if (!name.equals("diamond", ignoreCase = true)) return 0.0

        return 2.0
    }
}