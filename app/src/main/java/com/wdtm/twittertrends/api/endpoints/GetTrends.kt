package com.wdtm.twittertrends.api.endpoints

import com.wdtm.twittertrends.api.models.Trends
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetTrends {
    @GET("1.1/trends/place.json")
    fun getByLocationId(@Query("id") id: String ): Call<Trends>
}