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
import java.util.*

object ComboStun : Enchant {
    override val config: EnchantConfig
        get() = EnchantConfig(
            name = "Combo: Stun",
            tiers = listOf(1, 2, 3),
            group = EnchantGroup.B,
            rare = true,
            type = EnchantType.SWORD
        ) { "Every <yellow>${word[it]}</yellow> strike on an enemy<br/>stuns them for ${duration[it]?.seconds()} seconds" }

    private val word = mapOf(
        1 to "fifth",
        2 to "fourth",
        3 to "fourth"
    )

    private val duration = mapOf(
        1 to Time(10L),
        2 to Time(16L),
        3 to Time(30L)
    )

    private val hitsNeeded = mapOf(
        1 to 5,
        2 to 4,
        3 to 4
    )

    private val hitCounter = HitCounter<UUID>()

    @EventHandler
    fun onDamageEvent(event: EntityDamageByEntityEvent) {
        event.damagerMeleeHitPlayerWithEnchant(this) {
            val damager = it.damager
            val damaged = it.damaged

            val hitsNeeded = hitsNeeded[it.enchantTier] ?: undefPropErr("hitsNeeded", it.enchantTier)
            val duration = duration[it.enchantTier] ?: undefPropErr("duration", it.enchantTier)

            hitCounter.onNthHit(damager.uniqueId, hitsNeeded) {
                damaged.addPotionEffect(PotionEffect(PotionEffectType.SLOW, duration.ticks().toInt(), 8), true)
                damaged.addPotionEffect(PotionEffect(PotionEffectType.JUMP, duration.ticks().toInt(), -8), true)
                damaged.world.playSound(damaged.location, Sound.ANVIL_LAND, 1f, 0.1f)
                sendMessage(damaged)
            }
        }
    }

    private fun sendMessage(player: Player) {
        val titleMessage = replaceChatColorTags("<red>STUNNED!</red> <gold>${player.name.lowercase()}</gold>")
        val title = createChatComponent(titleMessage)
        val titlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, title, 0, 60, 0)

        val subtitleMessage = replaceChatColorTags("<yellow>You cannot move!</yellow> <gold${player.name.lowercase()}>")
        val subtitle = createChatComponent(subtitleMessage)
        val subtitlePacket = PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitle, 0, 60, 0)

        sendPacketToPlayer(player,titlePacket)
        sendPacketToPlayer(player,subtitlePacket)
    }
}