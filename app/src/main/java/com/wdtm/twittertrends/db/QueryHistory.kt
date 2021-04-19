package com.wdtm.twittertrends.db

import android.content.Context
import androidx.room.Room
import com.wdtm.twittertrends.db.dao.DateDao
import com.wdtm.twittertrends.db.dao.LocationDao
import com.wdtm.twittertrends.db.dao.TrendDao
import com.wdtm.twittertrends.db.database.HistoryDatabase
import com.wdtm.twittertrends.db.models.DateDb
import com.wdtm.twittertrends.db.models.DateWithData
import com.wdtm.twittertrends.db.models.LocationDb
import com.wdtm.twittertrends.db.models.TrendDb
import com.wdtm.twittertrends.models.Location
import com.wdtm.twittertrends.models.Query
import com.wdtm.twittertrends.models.Trend
import java.lang.Error
import java.util.*
import kotlin.concurrent.thread

object QueryHistory {
    private var context: Context? = null
    private var dateDao: DateDao? = null
    private var trendDao: TrendDao? = null
    private var locationDao: LocationDao? = null

    fun init(context: Context) {
        this.context = context
        val db = Room.databaseBuilder(
            context,
            HistoryDatabase::class.java, "twitter-trends-history-database"
        ).build()
        dateDao = db.dateDao()
        trendDao = db.trendDao()
        locationDao = db.locationDao()
    }

    fun getAll(onLoad: (List<Query>) -> Unit, onError: () -> Unit) {
        if (context == null) throw Error("QueryHistory not initialized")

        thread {
            try {
                val data = dateDao!!.getAll().map { dbModelToModel(it) }
                onLoad(data)
            } catch (_: Exception) {
                onError()
            }
        }
    }

    fun getFirst(n: Int, onLoad: (List<Query>) -> Unit, onError: () -> Unit) {
        if (context == null) throw Error("QueryHistory not initialized")
        if (n < 1) throw Error("N should be positive number")

        thread {
            try {
                val data = dateDao!!.getFirst(n).map { dbModelToModel(it) }
                onLoad(data)
            } catch (_: Exception) {
                onError()
            }
        }
    }

    fun add(query: Query) {
        add(query, {}, {})
    }

    fun add(query: Query, onError: () -> Unit) {
        add(query, {}, onError)
    }

    fun add(query: Query, onSuccess: () -> Unit, onError: () -> Unit) {
        if (context == null) throw Error("QueryHistory not initialized")

        val model = modelToDbModel(query)

        thread {
            try {
                dateDao!!.insertAll(model.dateDb)
                trendDao!!.insertAll(*model.trends.toTypedArray())
                locationDao!!.insertAll(model.location)
                onSuccess()
            } catch (_: Exception) {
                onError()
            }
        }
    }

    fun clear() {
        clear {}
    }

    fun clear(onError: () -> Unit) {
        clear({}, onError)
    }

    fun clear(onSuccess: () -> Unit, onError: () -> Unit) {
        if (context == null) throw Error("QueryHistory not initialized")

        thread {
            try {
                dateDao!!.truncate()
                trendDao!!.truncate()
                locationDao!!.truncate()
                onSuccess()
            } catch (_: Exception) {
                onError()
            }
        }
    }

    private fun dbModelToModel(model: DateWithData): Query {
        return Query(
            Date(model.dateDb.date),
            Location(model.location.id, model.location.name, model.location.latitude, model.location.longitude),
            model.trends.map { trendDb ->  Trend(trendDb.name, trendDb.url) }
        )
    }

    private fun modelToDbModel(model: Query): DateWithData {
        return DateWithData(
            DateDb(UUID.randomUUID().toString(), model.date.time),
            LocationDb(UUID.randomUUID().toString(), model.location.id, model.location.name, model.location.latitude, model.location.longitude, model.date.time),
            model.trends.map { trend ->  TrendDb(UUID.randomUUID().toString(), trend.name, trend.url, model.date.time) }
        )
    }
}
