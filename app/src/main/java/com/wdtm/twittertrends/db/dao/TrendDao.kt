package com.wdtm.twittertrends.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wdtm.twittertrends.db.models.TrendDb

@Dao
interface TrendDao {
    @Insert
    fun insertAll(vararg queries: TrendDb)

    @Query("DELETE FROM trendDb")
    fun truncate()
}
