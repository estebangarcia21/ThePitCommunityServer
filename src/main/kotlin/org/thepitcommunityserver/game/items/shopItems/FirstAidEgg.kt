import org.bukkit.Bukkit
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import org.thepitcommunityserver.game.events.DamageManager
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.*
import org.thepitcommunityserver.util.Timer
import java.util.*

object FirstAidEgg : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "First-Aid Egg",
            material = Material.MONSTER_EGG,
            lore = READY_DESCRIPTION,
            itemColor = READY_COLOR,
            unbreakable = true,
            data = READY_DATA,
        )

    private val timer = Timer<UUID>()
    private val cooldown = 30 * SECONDS
    private val cooldownReduction = 5 * SECONDS
    private val healAmount = 5.0

    private const val READY_DATA: Byte = 96
    private const val COOLDOWN_DATA: Byte = 51

    private const val READY_COLOR = "red"
    private const val COOLDOWN_COLOR = "gray"

    private val READY_DESCRIPTION = listOf(
        "Heals <red>2.5${Text.HEART}</red>",
        "30 seconds cooldown",
        "-5 seconds on kill.",
        "<dark-gray>Lose speed on use</dark-gray>"
    )

    private val COOLDOWN_DESCRIPTION = listOf(
        "Heals <red>2.5${Text.HEART}</red>",
        "<gray>On Cooldown!</gray>",
    )

    private fun setReadyState(item: ItemStack) {
        item.durability = READY_DATA.toShort()
        val meta = item.itemMeta
        meta.displayName = "<reset><$READY_COLOR>First-Aid Egg</$READY_COLOR>".parseChatColors()
        meta.lore = READY_DESCRIPTION.map { it.parseChatColors() }
        item.itemMeta = meta
    }

    private fun setCooldownState(item: ItemStack) {
        item.durability = COOLDOWN_DATA.toShort()
        val meta = item.itemMeta
        meta.displayName = "<reset><$COOLDOWN_COLOR>First-Aid Egg</$COOLDOWN_COLOR>".parseChatColors()
        meta.lore = COOLDOWN_DESCRIPTION.map { it.parseChatColors() }
        item.itemMeta = meta
    }

    private fun findEggInInventory(playerId: UUID): ItemStack? {
        val player = Bukkit.getPlayer(playerId) ?: return null
        return player.inventory.contents.firstOrNull {
            it != null && it.type == Material.MONSTER_EGG && it.durability == COOLDOWN_DATA.toShort()
        }
    }

    @EventHandler
    fun onRightClick(event: PlayerInteractEvent) {
        event.playerRightClickItem {
            val player = it.player
            val item = it.item

            if (item.type != Material.MONSTER_EGG) return@playerRightClickItem

            event.isCancelled = true

            timer.cooldown(
                id = player.uniqueId,
                ticks = cooldown,
                cooldownAction = {
                    player.playSound(player.location, Sound.VILLAGER_NO, 1.0f, 1.0f)
                },
                post = {
                    val egg = findEggInInventory(player.uniqueId)
                    if (egg != null) {
                        setReadyState(egg)
                        player.updateInventory()
                    }
                }
            ) {
                setCooldownState(item)
                player.updateInventory()

                DamageManager.applyHeal(player, healAmount)
                player.removePotionEffect(PotionEffectType.SPEED)

                player.playSound(player.location, Sound.ZOMBIE_PIG_HURT, 1.0f, 1.0f)
                player.playSound(player.location, Sound.CAT_HISS, 1.0f, 1.5f)
                player.playEffect(player.location, Effect.HAPPY_VILLAGER, 0)
                player.sendMessage("<green>+2.5${Text.HEART} healed!</green>".parseChatColors())
            }
        }
    }

    @EventHandler
    fun onPlayerKill(event: PlayerDeathEvent) {
        event.playerKillPlayer {
            val killer = it.killer

            if (timer.getCooldown(killer.uniqueId) == null) return@playerKillPlayer

            timer.reduceCooldown(killer.uniqueId, cooldownReduction)
            killer.sendMessage("<yellow>First-Aid Egg cooldown reduced by 5s!</yellow>".parseChatColors())
        }
    }
}