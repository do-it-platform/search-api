package de.doit.searchapi.domain.model

data class Query(val location: Location? = null, val distance: Double = 100.0) {
    data class Location(val lat: Double, val lon: Double)
}