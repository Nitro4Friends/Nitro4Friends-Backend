package com.nitro4friends.api

import com.nitro4friends.api.handlers.DiscordRedirect
import io.javalin.Javalin
import io.javalin.apibuilder.ApiBuilder.path

/**
 * This class represents a REST API server.
 *
 * The RestAPI class provides methods to start and stop the server.
 * The server is based on Javalin, a simple and lightweight web framework for Java and Kotlin.
 * It allows defining routes and handling HTTP requests and responses.
 *
 * @property apiServer The Javalin instance representing the API server.
 */
class RestAPI {

    private lateinit var apiServer: Javalin

    fun startUp(port: Int) {
        apiServer = Javalin.create()
            .get("/") { ctx -> ctx.html("<p>Nothing here yet.</p><br /><h2>Todo:</h2><small>Add OpenAPI Docs</small>") }
            .routes {
                path("/redirect", DiscordRedirect())
            }
            .start(port)
    }

    fun shutDown() {
        apiServer.stop()
    }

}