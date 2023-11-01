package org.wit.safedrop.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.safedrop.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "safedrops.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<SafedropModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class SafedropJSONStore(private val context: Context) : SafedropStore {

    var safedrops = mutableListOf<SafedropModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<SafedropModel> {
        logAll()
        return safedrops
    }

    override fun create(safedrop: SafedropModel) {
        safedrop.id = generateRandomId()
        safedrops.add(safedrop)
        serialize()
    }

    override fun update(safedrop: SafedropModel) {
        val safedropsList = findAll() as ArrayList<SafedropModel>
        var foundSafedrop: SafedropModel? = safedropsList.find { p -> p.id == safedrop.id }
        if (foundSafedrop != null) {
            foundSafedrop.title = safedrop.title
            foundSafedrop.description = safedrop.description
            foundSafedrop.image = safedrop.image
            foundSafedrop.lat = safedrop.lat
            foundSafedrop.lng = safedrop.lng
            foundSafedrop.zoom = safedrop.zoom
        }
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(safedrops, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        safedrops = gsonBuilder.fromJson(jsonString, listType)
    }

    override fun delete(safedrop: SafedropModel) {
        safedrops.remove(safedrop)
        serialize()
    }

    private fun logAll() {
        safedrops.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}