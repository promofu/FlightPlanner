package com.rafag.flightplanner.webapp.flights

import com.rafag.flightplanner.getDate
import com.rafag.flightplanner.model.Flight
import com.rafag.flightplanner.model.Price
import com.rafag.flightplanner.repository.Repository
import com.rafag.flightplanner.webapp.redirect
import io.ktor.application.call
import io.ktor.freemarker.FreeMarkerContent
import io.ktor.http.Parameters
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.Route

const val FLIGHTS = "flights"

@Location(FLIGHTS)
class Flights

fun Route.flights(repository: Repository) {
    get<Flights> {
        call.respond(
            FreeMarkerContent(
                template = "flights.ftl",
                model = mapOf(
                    "flights" to repository.getFlights().map()
                )
            )
        )
    }

    post<Flights> {
        val params = call.receiveParameters()
        val action = params["action"] ?: throw IllegalArgumentException("Missing action")

        when (action) {
            "delete" -> {
                val id = params["id"] ?: throw java.lang.IllegalArgumentException("Missing id")
                repository.remove(id)
            }
            "add" -> {
                repository.add(createFlightFromParams(params))
            }
        }

        call.redirect(Flights())
    }
}

private fun createFlightFromParams(params: Parameters): Flight {
    val bookingReference =
        params["bookingReference"] ?: throw IllegalArgumentException("Missing argument: bookingReference")
    val origin = params["origin"] ?: throw IllegalArgumentException("Missing argument: destination")
    val destination = params["destination"] ?: throw IllegalArgumentException("Missing argument: destination")
    val departingDate = params["departing_date"] ?: throw IllegalArgumentException("Missing argument: date")
    val arrivalDate = params["arrival_date"] ?: throw IllegalArgumentException("Missing argument: arrivalDate")
    val airline: String? = params["airline"]
    val people: String? = params["people"]
    val price: String? = params["price"]

    return Flight(
        bookingReference = bookingReference,
        departingDate = departingDate.getDate(),
        arrivalDate = arrivalDate.getDate(),
        origin = origin,
        destination = destination,
        airline = airline,
        people = people?.toInt(),
        price = price?.toDouble()?.let { Price(it) }
    )
}
