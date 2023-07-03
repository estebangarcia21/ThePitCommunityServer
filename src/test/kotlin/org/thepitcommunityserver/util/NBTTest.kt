package org.thepitcommunityserver.util

import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class NBTTest {
    @Test
    fun testSetNBTFloat() {
        val base = NBTTagCompound()
        setNBTFloat(base, "test", 3.14f)
        assertEquals(3.14f, getNBTFloat(base, "test"))
    }

    @Test
    fun testSetNBTDouble() {
        val base = NBTTagCompound()
        setNBTDouble(base, "test", 3.14159)
        assertEquals(3.14159, getNBTDouble(base, "test"))
    }

    @Test
    fun testSetNBTInt() {
        val base = NBTTagCompound()
        setNBTInt(base, "test", 42)
        assertEquals(42, getNBTInt(base, "test"))
    }

    @Test
    fun testSetNBTString() {
        val base = NBTTagCompound()
        setNBTString(base, "test", "Hello, World!")
        assertEquals("Hello, World!", getNBTString(base, "test"))
    }

    @Test
    fun testSetNBTByte() {
        val base = NBTTagCompound()
        setNBTByte(base, "test", 1.toByte())
        assertEquals(1.toByte(), getNBTByte(base, "test"))
    }

    @Test
    fun testSetNBTByteArray() {
        val base = NBTTagCompound()
        val byteArray = byteArrayOf(1, 2, 3)
        setNBTByteArray(base, "test", byteArray)
        assertEquals(byteArray.toList(), getNBTByteArray(base, "test")?.toList())
    }

    @Test
    fun testSetNBTCompound() {
        val base = NBTTagCompound()
        val compound = NBTTagCompound()
        compound.setString("subKey", "SubValue")
        setNBTCompound(base, "test", compound)
        assertEquals(compound, getNBTCompound(base, "test"))
    }

    @Test
    fun testBuildNBTCompound() {
        val data = mapOf(
            "float" to 3.14f,
            "string" to "Hello, World!",
            "int" to 42,
            "compound" to NBTTagCompound().apply { setString("subKey", "SubValue") }
        )
        val compound = buildNBTCompound(data)

        assertEquals(3.14f, compound.getFloat("float"))
        assertEquals("Hello, World!", compound.getString("string"))
        assertEquals(42, compound.getInt("int"))
        assertEquals("SubValue", compound.getCompound("compound").getString("subKey"))
    }

    @Test
    fun testReadNBTCompoundAsMap() {
        val compound = NBTTagCompound()
        compound.setFloat("float", 3.14f)
        compound.setDouble("double", 2.718)
        compound.setInt("int", 42)
        compound.setLong("long", 123456789L)
        compound.setByte("byte", 127.toByte())
        compound.setByteArray("byteArray", byteArrayOf(1, 2, 3))
        compound.setString("string", "Hello, World!")
        compound.setBoolean("boolean", true)
        compound.set("compound", NBTTagCompound().apply { setString("subKey", "SubValue") })

        val data = readNBTCompoundAsMap(compound)

        assertEquals(3.14f, data["float"])
        assertEquals(2.718, data["double"])
        assertEquals(42, data["int"])
        assertEquals(123456789L, data["long"])
        assertEquals(1.toByte(), data["boolean"])
        assertEquals(127.toByte(), data["byte"])
        assertArrayEquals(byteArrayOf(1, 2, 3), data["byteArray"] as ByteArray)
        assertEquals("Hello, World!", data["string"])
        assertEquals(mapOf("subKey" to "SubValue"), data["compound"])
    }

    @Test
    fun testNBTTagCompoundKeys() {
        val compound = NBTTagCompound()
        compound.setFloat("float", 3.14f)
        compound.setString("string", "Hello, World!")
        compound.setInt("int", 42)

        val expectedKeys = listOf("float", "string", "int")
        val keys = compound.keys

        assertTrue(keys.containsAll(expectedKeys) && expectedKeys.containsAll(keys))
    }
}
