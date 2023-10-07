package com.nitro4friends.utils

import io.github.cdimascio.dotenv.dotenv

object Environment {

    private val env = System.getenv()
    private val dotEnv = dotenv {
        ignoreIfMissing = true
    }

    /**
     * Retrieves the value of the environment variable with the specified key.
     *
     * @param key The key of the environment variable.
     * @return The value of the environment variable, or null if the variable is not found.
     */
    fun getEnv(key: String): String? {
        return dotEnv[key] ?: env[key]
    }

}