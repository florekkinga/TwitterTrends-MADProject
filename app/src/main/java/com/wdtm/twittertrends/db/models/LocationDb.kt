package com.wdtm.twittertrends.db.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationDb(
    @PrimaryKey val dbId: String,
    val id: String,
    val name: String,
    var latitude: String,
    var longitude: String,
    var queryId: Long,
)
