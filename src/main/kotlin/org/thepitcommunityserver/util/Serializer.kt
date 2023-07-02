package org.thepitcommunityserver.util

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.*

// SOURCED FROM https://gist.github.com/graywolf336/8153678

/**
 * Converts the player inventory to a String array of Base64 strings. First string is the content and second string is the armor.
 *
 * @param playerInventory to turn into an array of strings.
 * @return Array of strings: [ main content, armor content ]
 * @throws IllegalStateException
 */
fun playerInventoryToBase64(playerInventory: PlayerInventory): Array<String> {
    // get the main content part, this doesn't return the armor
    val content = toBase64(playerInventory)
    val armor = itemStackArrayToBase64(playerInventory.armorContents)

    return arrayOf(content, armor)
}

/**
 * A method to serialize an [ItemStack] array to Base64 String.
 *
 * Based off of [toBase64].
 *
 * @param items to turn into a Base64 String.
 * @return Base64 string of the items.
 * @throws IllegalStateException
 */
fun itemStackArrayToBase64(items: Array<ItemStack>): String {
    try {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        // Write the size of the inventory
        dataOutput.writeInt(items.size)

        // Save every element in the list
        for (i in items.indices) {
            dataOutput.writeObject(items[i])
        }

        // Serialize that array
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    } catch (e: Exception) {
        throw IllegalStateException("Unable to save item stacks.", e)
    }
}

/**
 * A method to serialize an inventory to Base64 string.
 *
 * Special thanks to Comphenix in the Bukkit forums or also known
 * as aadnk on GitHub.
 *
 * [Original Source](https://gist.github.com/aadnk/8138186)
 *
 * @param inventory to serialize
 * @return Base64 string of the provided inventory
 * @throws IllegalStateException
 */
fun toBase64(inventory: Inventory): String {
    try {
        val outputStream = ByteArrayOutputStream()
        val dataOutput = BukkitObjectOutputStream(outputStream)

        // Write the size of the inventory
        dataOutput.writeInt(inventory.size)

        // Save every element in the list
        for (i in 0 until inventory.size) {
            dataOutput.writeObject(inventory.getItem(i))
        }

        // Serialize that array
        dataOutput.close()
        return Base64Coder.encodeLines(outputStream.toByteArray())
    } catch (e: Exception) {
        throw IllegalStateException("Unable to save item stacks.", e)
    }
}

/**
 * A method to get an [Inventory] from an encoded, Base64, string.
 *
 * Special thanks to Comphenix in the Bukkit forums or also known
 * as aadnk on GitHub.
 *
 * [Original Source](https://gist.github.com/aadnk/8138186)
 *
 * @param data Base64 string of data containing an inventory.
 * @return Inventory created from the Base64 string.
 * @throws IOException
 */
fun fromBase64(data: String): Inventory {
    try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val inventory = Bukkit.getServer().createInventory(null, dataInput.readInt())

        // Read the serialized inventory
        for (i in 0 until inventory.size) {
            inventory.setItem(i, dataInput.readObject() as ItemStack)
        }

        dataInput.close()
        return inventory
    } catch (e: ClassNotFoundException) {
        throw IOException("Unable to decode class type.", e)
    }
}

/**
 * Gets an array of ItemStacks from Base64 string.
 *
 * Base off of [fromBase64].
 *
 * @param data Base64 string to convert to ItemStack array.
 * @return ItemStack array created from the Base64 string.
 * @throws IOException
 */
fun itemStackArrayFromBase64(data: String): Array<ItemStack> {
    try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(dataInput.readInt())

        // Read the serialized inventory
        for (i in items.indices) {
            items[i] = dataInput.readObject() as ItemStack
        }

        dataInput.close()
        @Suppress("UNCHECKED_CAST")
        return items as Array<ItemStack>
    } catch (e: ClassNotFoundException) {
        throw IOException("Unable to decode class type.", e)
    }
}
