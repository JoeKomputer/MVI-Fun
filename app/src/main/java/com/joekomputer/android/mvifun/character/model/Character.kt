package com.joekomputer.android.mvifun.character.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Character(
    val name: String,
    val details: String,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
) : Parcelable {
    val id: String
        get() = name + details + imageUrl
}