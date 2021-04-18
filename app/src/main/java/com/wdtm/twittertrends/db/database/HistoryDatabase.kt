package com.wdtm.twittertrends.db.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.wdtm.twittertrends.db.dao.DateDao
import com.wdtm.twittertrends.db.dao.LocationDao
import com.wdtm.twittertrends.db.dao.TrendDao
import com.wdtm.twittertrends.db.models.DateDb
import com.wdtm.twittertrends.db.models.LocationDb
import com.wdtm.twittertrends.db.models.TrendDb

@Database(entities = [DateDb::class, LocationDb::class, TrendDb::class], version = 1)
abstract class HistoryDatabase  : RoomDatabase() {
    abstract fun dateDao(): DateDao
    abstract fun locationDao(): LocationDao
    abstract fun trendDao(): TrendDao
}
