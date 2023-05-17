package com.urbn.android.flickster.character.model

import java.util.ArrayList
import java.util.HashMap

object PlaceholderContent {

    val CHARACTERS: MutableList<Character> = ArrayList()

    private val CHARACTER_MAP: MutableMap<String, Character> = HashMap()

    private const val COUNT = 25

    init {
        for (i in 1..COUNT) {
            addItem(createPlaceholderItem(i))
        }
    }

    private fun addItem(item: Character) {
        CHARACTERS.add(item)
        CHARACTER_MAP[item.name] = item
    }

    private fun createPlaceholderItem(position: Int): Character {
        return Character("Character Name $position", makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Character: ").append(position)
        for (i in 0 until position) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }
}
