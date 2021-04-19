package com.wdtm.twittertrends.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TrendDb(
    @PrimaryKey val id: String,
    val name: String,
    val url: String,
    var queryId: Long,
)
