package com.wdtm.twittertrends.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.wdtm.twittertrends.models.Trend
import com.wdtm.twittertrends.api.models.Trends
import java.lang.reflect.Type
import kotlin.jvm.Throws

class TrendsDeserializer : JsonDeserializer<Trends> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Trends {
        val responseArray = json.asJsonArray[0].asJsonObject.getAsJsonArray("trends")

        return Trends(responseArray.map { trend ->
            Trend(trend.asJsonObject.get("name").asString, trend.asJsonObject.get("url").asString)
        })
    }
}