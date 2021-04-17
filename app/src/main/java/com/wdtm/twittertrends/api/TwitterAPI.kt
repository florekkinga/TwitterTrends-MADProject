package com.wdtm.twittertrends.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.wdtm.twittertrends.R
import com.wdtm.twittertrends.api.deserializers.LocationDeserializer
import com.wdtm.twittertrends.api.deserializers.TrendsDeserializer
import com.wdtm.twittertrends.api.endpoints.GetLocation
import com.wdtm.twittertrends.api.endpoints.GetTrends
import com.wdtm.twittertrends.api.models.*
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

object TwitterAPI {
    private var retrofit: Retrofit? = null
    private var context: Context? = null

    fun init (context: Context) {
        this.context = context

        retrofit = Retrofit.Builder()
            .client(getAuthInterceptor())
            .baseUrl(context.getString(R.string.twitter_api_url))
            .addConverterFactory(getGsonFactory())
            .build()
    }

    fun fetchQuery(latitude: String, longitude: String, onSuccess: (data: Query) -> Unit, onFailure: () -> Unit) {
        if (context == null) {
            onFailure()
            return
        }

        fetchLocation(latitude, longitude, { location ->
            fetchTrends(location.id, { trends ->
                onSuccess(Query(Date(), location, trends))
            }, {
                onFailure()
            })
        }, {
            onFailure()
        })
    }

    fun fetchLocation(latitude: String, longitude: String, onSuccess: (data: Location) -> Unit, onFailure: () -> Unit) {
        if (context == null) {
            onFailure()
            return
        }

        retrofit?.let {
            it.create(GetLocation::class.java).getByCoordinates(latitude, longitude)
                .enqueue(object : Callback<LocationWithoutCoordinates> {
                    override fun onFailure(call: Call<LocationWithoutCoordinates>, t: Throwable) {
                        onFailure()
                    }

                    override fun onResponse(call: Call<LocationWithoutCoordinates>, response: Response<LocationWithoutCoordinates>) {
                        if (!response.isSuccessful || response.body() == null) {
                            onFailure()
                            return
                        }

                        onSuccess(Location(response.body()!!.id, response.body()!!.name, latitude, longitude))
                    }
                }
            )
        }
    }

    fun fetchTrends(locationId: String, onSuccess: (data: List<Trend>) -> Unit, onFailure: () -> Unit) {
        if (context == null) {
            onFailure()
            return
        }

        retrofit?.let {
            it.create(GetTrends::class.java).getByLocationId(locationId)
                .enqueue(object : Callback<Trends> {
                    override fun onFailure(call: Call<Trends>, t: Throwable) {
                        onFailure()
                    }

                    override fun onResponse(call: Call<Trends>, response: Response<Trends>) {
                        if (!response.isSuccessful || response.body() == null) {
                            onFailure()
                            return
                        }

                        onSuccess(response.body()!!.value)
                    }
                }
            )
        }
    }

    private fun getGsonFactory(): GsonConverterFactory {
        val gson = GsonBuilder()
        gson.registerTypeAdapter(Trends::class.java, TrendsDeserializer())
        gson.registerTypeAdapter(LocationWithoutCoordinates::class.java, LocationDeserializer())

        return GsonConverterFactory.create(gson.create())
    }

    private fun getAuthInterceptor(): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${context!!.getString(R.string.twitter_api_token)}")
                .build()
            chain.proceed(newRequest)
        }.build()
    }

}