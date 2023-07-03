package org.thepitcommunityserver.util

import net.minecraft.server.v1_8_R3.NBTTagByte
import net.minecraft.server.v1_8_R3.NBTTagByteArray
import net.minecraft.server.v1_8_R3.NBTTagCompound
import net.minecraft.server.v1_8_R3.NBTTagDouble
import net.minecraft.server.v1_8_R3.NBTTagFloat
import net.minecraft.server.v1_8_R3.NBTTagInt
import net.minecraft.server.v1_8_R3.NBTTagLong
import net.minecraft.server.v1_8_R3.NBTTagString
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

enum class NBT(val key: String, val value: Any) {
    UNDROPPABLE("pit:undroppable", true),
    LOSE_ON_DEATH("pit:lose-on-death", true);

    val entry: Pair<String, Any>
        get() = key to value
}

fun setNBTFloat(base: NBTTagCompound?, key: String, value: Float?) {
    if (base == null || value == null) return

    base.setFloat(key, value)
}

fun setNBTDouble(base: NBTTagCompound?, key: String, value: Double?) {
    if (base == null || value == null) return

    base.setDouble(key, value)
}

fun setNBTInt(base: NBTTagCompound?, key: String, value: Int?) {
    if (base == null || value == null) return

    base.setInt(key, value)
}

fun setNBTString(base: NBTTagCompound?, key: String, value: String?) {
    if (base == null || value == null) return

    base.setString(key, value)
}

fun setNBTByte(base: NBTTagCompound?, key: String, value: Byte?) {
    if (base == null || value == null) return

    base.setByte(key, value)
}

fun setNBTByteArray(base: NBTTagCompound?, key: String, value: ByteArray?) {
    if (base == null || value == null) return

    base.setByteArray(key, value)
}

fun getNBTFloat(base: NBTTagCompound?, key: String): Float? {
    if (base == null || !base.hasKey(key)) return null

    return base.getFloat(key)
}

fun getNBTDouble(base: NBTTagCompound?, key: String): Double? {
    if (base == null || !base.hasKey(key)) return null

    return base.getDouble(key)
}

fun getNBTInt(base: NBTTagCompound?, key: String): Int? {
    if (base == null || !base.hasKey(key)) return null

    return base.getInt(key)
}

fun getNBTString(base: NBTTagCompound?, key: String): String? {
    if (base == null || !base.hasKey(key)) return null

    return base.getString(key)
}

fun getNBTByte(base: NBTTagCompound?, key: String): Byte? {
    if (base == null || !base.hasKey(key)) return null

    return base.getByte(key)
}

fun getNBTByteArray(base: NBTTagCompound?, key: String): ByteArray? {
    if (base == null || !base.hasKey(key)) return null

    return base.getByteArray(key)
}

fun getNBTCompound(base: NBTTagCompound?, key: String): NBTTagCompound? {
    if (base == null || !base.hasKey(key)) return null

    return base.getCompound(key)
}

fun setNBTCompound(base: NBTTagCompound?, key: String, compound: NBTTagCompound?) {
    if (base == null || compound == null) return

    base.set(key, compound)
}

fun setNBTBoolean(base: NBTTagCompound?, key: String, value: Boolean?) {
    if (base == null || value == null) return

    base.setBoolean(key, value)
}

fun getNBTBoolean(base: NBTTagCompound?, key: String): Boolean? {
    if (base == null || !base.hasKey(key)) return null

    return base.getBoolean(key)
}

fun hasNBTEntryFor(base: NBTTagCompound?, key: String): Boolean {
    if (base == null) return false

    return base.hasKey(key)
}

typealias DeserializedNBTMap = Map<String, Any>

fun buildNBTCompound(map: DeserializedNBTMap): NBTTagCompound {
    val compound = NBTTagCompound()

    map.entries.forEach { (key, value) ->
        when (value) {
            is Byte -> compound.setByte(key, value)
            is Short -> compound.setShort(key, value)
            is Int -> compound.setInt(key, value)
            is Long -> compound.setLong(key, value)
            is Float -> compound.setFloat(key, value)
            is Double -> compound.setDouble(key, value)
            is String -> compound.setString(key, value)
            is ByteArray -> compound.setByteArray(key, value)
            is Boolean -> compound.setBoolean(key, value)
            is NBTTagCompound -> compound.set(key, value)
            else -> throw IllegalArgumentException("Unsupported NBT data type: ${value.javaClass}")
        }
    }

    return compound
}

/**
 * Merges the `compound` into the `target` `NBTTagCompound`.
 *
 * All entries from `compound` will override duplicate entries from `target`.
 */
fun mergeNBTCompounds(target: NBTTagCompound?, compound: NBTTagCompound?): NBTTagCompound {
    val mergedCompound = target ?: compound ?: NBTTagCompound()

    if (compound != null) {
        for (key in compound.keys) {
            val value = compound.get(key)
            mergedCompound.set(key, value)
        }
    }

    return mergedCompound
}

fun readNBTCompoundAsMap(compound: NBTTagCompound?): DeserializedNBTMap {
    if (compound == null) return emptyMap()

    val map = mutableMapOf<String, Any>()

    compound.keys.forEach { key ->
        val result: Any = when (val value = compound.get(key)) {
            is NBTTagString -> value.a_()
            is NBTTagInt -> value.d()
            is NBTTagFloat -> value.h()
            is NBTTagLong -> value.c()
            is NBTTagDouble -> value.g()
            is NBTTagByte -> value.f()
            is NBTTagByteArray -> value.c()
            is NBTTagCompound -> readNBTCompoundAsMap(value)
            else -> return@forEach
        }

        map[key] = result
    }

    return map
}

val NBTTagCompound.keys: List<String>
    get() = this.c().toList()

var ItemStack?.nbt: NBTTagCompound?
    get() {
        if (this == null) return null
        val nmsItemStack = CraftItemStack.asNMSCopy(this) ?: return null

        return nmsItemStack.tag ?: return null
    }
    set(tag) {
        if (this == null) return
        val nmsItemStack = CraftItemStack.asNMSCopy(this) ?: return

        nmsItemStack.tag = tag

        this.itemMeta = CraftItemStack.getItemMeta(nmsItemStack)
    }
