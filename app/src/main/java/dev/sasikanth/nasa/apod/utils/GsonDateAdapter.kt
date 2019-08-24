package dev.sasikanth.nasa.apod.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.util.Date

class GsonDateAdapter : JsonDeserializer<Date> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date {
        try {
            return DateUtils.parseDate(json!!.asString)
        } catch (e: ParseException) {
            throw JsonParseException(e)
        }
    }
}
