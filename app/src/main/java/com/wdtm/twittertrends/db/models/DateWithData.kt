package com.wdtm.twittertrends.db.models

import androidx.room.Embedded
import androidx.room.Relation

class DateWithData (
    @Embedded val dateDb: DateDb,
    @Relation(
        parentColumn = "date",
        entityColumn = "queryId"
    ) val location: LocationDb,
    @Relation(
        parentColumn = "date",
        entityColumn = "queryId"
    ) val trends: List<TrendDb>
)
