package com.theoghn.mtgtreasury.utility

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

class Constants {
    object MainGradient{
        val gradientColors = listOf(Color(0xFFA774FA), Color(0xFF6115DB))
        val brush = Brush.horizontalGradient(gradientColors)
    }

    object SearchFilterValues {
        val TYPE = listOf(
            "Artifact",
            "Battle",
            "Conspiracy",
            "Creature",
            "Dungeon",
            "Emblem",
            "Enchantment",
            "Hero",
            "Instant",
            "Kindred",
            "Land",
            "Phenomenon",
            "Plane",
            "Planeswalker",
            "Scheme",
            "Sorcery",
            "Vanguard"
        )
        val SUPERTYPE = listOf("Basic", "Legendary", "Ongoing", "Snow", "World")
        val RARITY = listOf("Common", "Uncommon", "Rare", "Mythic", "Special", "Bonus")
        val COLOR = listOf("W", "U", "B", "R", "G")
        val MANA_COST = mapOf(
            "X" to "X",
            "Y" to "Y",
            "Z" to "Z",
            "0" to "0",
            "½" to "HALF",
            "1" to "1",
            "2" to "2",
            "3" to "3",
            "4" to "4",
            "5" to "5",
            "6" to "6",
            "7" to "7",
            "8" to "8",
            "9" to "9",
            "10" to "10",
            "11" to "11",
            "12" to "12",
            "13" to "13",
            "14" to "14",
            "15" to "15",
            "16" to "16",
            "17" to "17",
            "18" to "18",
            "19" to "19",
            "20" to "20",
            "∞" to "INFINITY",
            "W/U" to "WU",
            "W/B" to "WB",
            "B/R" to "BR",
            "B/G" to "BG",
            "U/B" to "UB",
            "U/R" to "UR",
            "R/G" to "RG",
            "R/W" to "RW",
            "G/W" to "GW",
            "G/U" to "GU",
            "B/G/P" to "BGP",
            "B/R/P" to "BRP",
            "G/U/P" to "GUP",
            "G/W/P" to "GWP",
            "R/G/P" to "RGP",
            "R/W/P" to "RWP",
            "U/B/P" to "UBP",
            "U/R/P" to "URP",
            "W/B/P" to "WBP",
            "W/U/P" to "WUP",
            "C/W" to "CW",
            "C/U" to "CU",
            "C/B" to "CB",
            "C/R" to "CR",
            "C/G" to "CG",
            "2/W" to "2W",
            "2/U" to "2U",
            "2/B" to "2B",
            "2/R" to "2R",
            "2/G" to "2G",
            "H" to "H",
            "W/P" to "WP",
            "U/P" to "UP",
            "B/P" to "BP",
            "R/P" to "RP",
            "G/P" to "GP",
            "C/P" to "CP",
            "HW" to "HW",
            "HR" to "HR",
            "W" to "W",
            "U" to "U",
            "B" to "B",
            "R" to "R",
            "G" to "G",
            "C" to "C",
            "S" to "S",
            "L" to "L",
            "D" to "D"
        )

    }

}