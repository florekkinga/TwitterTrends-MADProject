package com.wdtm.twittertrends.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.wdtm.twittertrends.db.models.DateDb
import com.wdtm.twittertrends.db.models.DateWithData

@Dao
interface DateDao {
    @Query("SELECT * FROM dateDb")
    fun getAll(): List<DateWithData>

    @Query("SELECT * FROM dateDb LIMIT :n")
    fun getFirst(n: Int): List<DateWithData>

    @Insert
    fun insertAll(vararg queries: DateDb)

    @Query("DELETE FROM dateDb")
    fun truncate()
}
