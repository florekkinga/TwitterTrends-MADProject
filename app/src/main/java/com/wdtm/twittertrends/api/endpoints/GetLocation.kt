package com.wdtm.twittertrends.api.endpoints

import com.wdtm.twittertrends.api.models.LocationWithoutCoordinates
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GetLocation {
    @GET("1.1/trends/closest.json")
    fun getByCoordinates(@Query("lat") latitude: String, @Query("long") longitude: String): Call<LocationWithoutCoordinates>
}