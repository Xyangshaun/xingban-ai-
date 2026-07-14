package com.example.myspatial.spatial

object SpatialProvider {
    val spatialManager: SpatialManager by lazy { MockSpatialManager() }
}