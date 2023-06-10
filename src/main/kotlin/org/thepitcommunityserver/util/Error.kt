package org.thepitcommunityserver.util

fun undefPropErr(mapName: String, tier: Int): Nothing {
    error("Attribute map $mapName has an undefined value for $tier")
}
