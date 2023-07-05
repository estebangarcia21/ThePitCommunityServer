package org.thepitcommunityserver.game.enchants

import net.minecraft.server.v1_8_R3.PacketPlayOutTitle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.enchants.lib.*
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object ComboStun : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Combo: Stun",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.SWORD,
            description
        )

    private val description: EnchantDescription = {
        if (it == 3) {
            "Every <yellow>fifth</yellow> strike on an enemy<br/>stuns them for ${seconds[it]} seconds"
        } else {
            "Every <yellow>fifth</yellow> strike on an enemy<br/>stuns them for ${seconds[it]} seconds<br/>(Can only be stunned every 8s)"
        }
    }

    private val seconds = mapOf(
        1 to 0.5f,
        2 to 0.8f,
        3 to 1.5f
    )

    private val duration = mapOf(
        1 to Time(10L),
        2 to Time(16L),
        3 to Time(30L)
    )

    private val cooldown = Time(8 * SECONDS)
    private val hitCounter = HitCounter<UUID>()
    private val timer = Timer<UUID>()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged

            val duration = duration[it.enchantTier] ?: undefPropErr("duration", it.enchantTier)

            hitCounter.onNthHit(damager.uniqueId, 5) {

                if (it.enchantTier == 3) {
                    damaged.addPotionEffect(PotionEffect(PotionEffectType.SLOW, duration.ticks().toInt(), 8), true)
                    damaged.addPotionEffect(PotionEffect(PotionEffectType.JUMP, duration.ticks().toInt(), -8), true)
                    damaged.world.playSound(damaged.location, Sound.ANVIL_LAND, 1f, 0.1f)
                    sendMessage(damaged)
                }
                else timer.cooldown(damager.uniqueId, cooldown.ticks()) {
                    damaged.addPotionEffect(PotionEffect(PotionEffectType.SLOW, duration.ticks().toInt(), 8), true)
                    damaged.addPotionEffect(PotionEffect(PotionEffectType.JUMP, duration.ticks().toInt(), -8), true)
                    damaged.world.playSound(damaged.location, Sound.ANVIL_LAND, 1f, 0.1f)
                    sendMessage(damaged)
                }
            }
        }
    }

    private fun sendMessage(player: Player) {
        val titleMessage = replaceChatColorTags("<red>STUNNED!</red>")
        val title = createChatComponent(titleMessage)
        val titlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title, 0, 60, 0)

        val subtitleMessage = replaceChatColorTags("<yellow>You cannot move!</yellow>")
        val subtitle = createChatComponent(subtitleMessage)
        val subtitlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle, 0, 60, 0)

        sendPacketToPlayer(player,titlePacket)
        sendPacketToPlayer(player,subtitlePacket)
    }
}