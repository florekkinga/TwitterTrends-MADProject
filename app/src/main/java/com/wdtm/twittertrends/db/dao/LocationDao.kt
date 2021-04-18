package com.wdtm.twittertrends.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wdtm.twittertrends.db.models.LocationDb

@Dao
interface LocationDao {
    @Insert
    fun insertAll(vararg queries: LocationDb)

    @Query("DELETE FROM locationDb")
    fun truncate()
}
