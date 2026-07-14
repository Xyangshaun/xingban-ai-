package com.example.myspatial.spatial

import com.example.myspatial.data.Companion

interface SpatialObject {
    fun load()
    fun unload()
    fun moveTo(x: Float, y: Float, z: Float)
    fun rotate(yaw: Float, pitch: Float, roll: Float)
    fun scale(factor: Float)
    fun setVisible(visible: Boolean)
    fun isLoaded(): Boolean
}

interface SpatialManager {
    fun createCompanionObject(companion: Companion): SpatialObject
    fun getCompanionObject(id: String): SpatialObject?
    fun removeCompanionObject(id: String)
    fun clearAll()
}

class MockSpatialManager : SpatialManager {
    private val objects = mutableMapOf<String, SpatialObject>()

    override fun createCompanionObject(companion: Companion): SpatialObject {
        return MockSpatialObject(companion.id).also {
            objects[companion.id] = it
        }
    }

    override fun getCompanionObject(id: String): SpatialObject? {
        return objects[id]
    }

    override fun removeCompanionObject(id: String) {
        objects[id]?.unload()
        objects.remove(id)
    }

    override fun clearAll() {
        objects.values.forEach { it.unload() }
        objects.clear()
    }
}

class MockSpatialObject(private val id: String) : SpatialObject {
    private var loaded = false
    private var visible = true

    override fun load() {
        loaded = true
    }

    override fun unload() {
        loaded = false
    }

    override fun moveTo(x: Float, y: Float, z: Float) {
    }

    override fun rotate(yaw: Float, pitch: Float, roll: Float) {
    }

    override fun scale(factor: Float) {
    }

    override fun setVisible(visible: Boolean) {
        this.visible = visible
    }

    override fun isLoaded(): Boolean = loaded
}