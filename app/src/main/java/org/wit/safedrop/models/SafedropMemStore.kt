package org.wit.safedrop.models

import timber.log.Timber.Forest.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class SafedropMemStore : SafedropStore {

    val safedrops = ArrayList<SafedropModel>()

    override fun findAll(): List<SafedropModel> {
        return safedrops
    }

    override fun create(safedrop: SafedropModel) {
        safedrop.id = getId()
        safedrops.add(safedrop)
        logAll()
    }

    override fun update(safedrop: SafedropModel) {
        val foundSafedrop: SafedropModel? = safedrops.find { p -> p.id == safedrop.id }
        if (foundSafedrop != null) {
            foundSafedrop.title = safedrop.title
            foundSafedrop.description = safedrop.description
            foundSafedrop.image = safedrop.image
            foundSafedrop.lat = safedrop.lat
            foundSafedrop.lng = safedrop.lng
            foundSafedrop.zoom = safedrop.zoom
            logAll()
        }
    }

    private fun logAll() {
        safedrops.forEach { i("$it") }
    }

    override fun delete(safedrop: SafedropModel) {
        safedrops.remove(safedrop)
    }
}