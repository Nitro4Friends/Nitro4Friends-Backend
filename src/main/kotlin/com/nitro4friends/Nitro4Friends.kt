package com.nitro4friends

import com.nitro4friends.api.RestAPI
import com.nitro4friends.utils.Environment
import dev.fruxz.ascend.extension.logging.getItsLogger

/**
 * This class represents the Nitro4Friends application backend service.
 *
 * The Nitro4Friends class starts and manages the REST API server using Javalin.
 * It initializes the RestAPI class and provides methods to start and stop the server.
 *
 * @property environment The Dotenv instance used for managing environment variables.
 * @property restAPI The RestAPI instance representing the REST API server.
 *
 * @constructor Initializes the Nitro4Friends backend service.
 */
class Nitro4Friends {

    /**
     * Represents a variable that holds a RestAPI instance.
     *
     * The RestAPI class is responsible for starting and stopping the REST API server.
     *
     * @property restAPI The RestAPI instance representing the REST API server.
     */
    private var restAPI: RestAPI

    init {
        getItsLogger().info("Starting Nitro4Friends Backend Service...")

        restAPI = RestAPI()
        restAPI.startUp(Environment.getEnv("WEBSERVER_PORT")?.toInt() ?: 8080)

        // Add a shutdown hook to stop the server when the JVM is shutting down.
        Runtime.getRuntime().addShutdownHook(Thread {
            restAPI.shutDown()
        })

        getItsLogger().info("Nitro4Friends Backend Service started.")
    }

}