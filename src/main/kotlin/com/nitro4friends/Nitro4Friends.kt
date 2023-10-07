package com.nitro4friends

import dev.fruxz.ascend.extension.logging.getItsLogger
import io.github.cdimascio.dotenv.dotenv
import io.javalin.Javalin

class Nitro4Friends {

    private val environment = dotenv {
        ignoreIfMissing = true
    }

    init {
        getItsLogger().info("Starting Nitro4Friends Backend Service...")
        Javalin.create().start(environment["WEBSERVER_PORT"]?.toInt() ?: 8080)
    }

}