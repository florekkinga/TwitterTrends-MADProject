package com.wdtm.twittertrends.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DateDb (
    @PrimaryKey val key: String,
    val date: Long
)
