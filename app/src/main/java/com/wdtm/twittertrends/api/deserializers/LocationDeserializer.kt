package com.wdtm.twittertrends.api.deserializers

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.wdtm.twittertrends.api.models.LocationWithoutCoordinates
import java.lang.reflect.Type
import kotlin.jvm.Throws

class LocationDeserializer : JsonDeserializer<LocationWithoutCoordinates> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocationWithoutCoordinates {
        val location = json.asJsonArray[0].asJsonObject

        return LocationWithoutCoordinates(location.get("woeid").asString, location.get("name").asString)
    }
}