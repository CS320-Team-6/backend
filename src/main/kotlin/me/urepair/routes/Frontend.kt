package me.urepair.routes

import io.ktor.server.http.content.react
import io.ktor.server.http.content.singlePageApplication
import io.ktor.server.routing.Route

fun Route.frontend() {
    singlePageApplication {
        useResources = true
        react("react-app")
    }
}
