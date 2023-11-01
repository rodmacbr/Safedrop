package org.wit.safedrop.models

interface SafedropStore {
    fun findAll(): List<SafedropModel>
    fun create(safedrop: SafedropModel)
    fun update(safedrop: SafedropModel)
    fun delete(safedrop: SafedropModel)
}