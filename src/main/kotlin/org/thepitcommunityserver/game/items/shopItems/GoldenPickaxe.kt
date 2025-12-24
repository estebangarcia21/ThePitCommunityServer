package org.thepitcommunityserver.game.items.shopItems

import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.thepitcommunityserver.game.items.Item
import org.thepitcommunityserver.game.items.ItemConfig
import org.thepitcommunityserver.util.HitCounter
import org.thepitcommunityserver.util.Timer
import org.thepitcommunityserver.util.playerHitBlock
import java.util.*

object GoldenPickaxe : Item {
    override val config: ItemConfig
        get() = ItemConfig(
            name = "Golden Pickaxe",
            material = Material.GOLD_PICKAXE,
            lore = listOf(
                "Breaks a 5-high pillar of",
                "obsidian when 2-tapping it"
            ),
            itemColor = "gold",
            unbreakable = true,
            flags = emptyList(),
        )

    private val hitCounter = HitCounter<UUID>()
    private val timer = Timer<Block>()
    
    @EventHandler
    fun onLeftClickBlock(event: PlayerInteractEvent) {
        event.playerHitBlock {
            val player = it.player
            val block = it.block
            val world = block.world
            val location = block.location

            if (block.type == Material.OBSIDIAN && player.itemInHand.type == Material.GOLD_PICKAXE) {
                hitCounter.onNthHit(player.uniqueId, 3) {

                    val blocksToBreak = mutableListOf<Block>()

                    for (y in 0..4) {
                        val targetBlock = location.clone().add(0.0, y.toDouble(), 0.0).block
                        if (targetBlock.type == Material.OBSIDIAN) {
                            blocksToBreak.add(targetBlock)
                        } else {
                            break
                        }
                    }

                    blocksToBreak.forEachIndexed { index, block ->
                        // Block break
                        timer.after(block, (index + 1) * 2L) {
                            block.type = Material.AIR
                            player.playSound(block.location, Sound.DIG_STONE, 1.0f, 1.0f)

                            // Particle effect create abstraction for this later
                            val centerLoc = block.location.add(0.5, 0.5, 0.5)
                            world.spigot().playEffect(
                                centerLoc,
                                Effect.CLOUD,
                                0,
                                0,
                                0.05f,
                                0.05f,
                                0.05f,
                                0.01f,
                                1,
                                5
                            )
                        }
                    }
                }
            }
        }
    }
}