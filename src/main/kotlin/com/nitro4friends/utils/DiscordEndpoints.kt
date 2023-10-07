package com.nitro4friends.utils

import com.nitro4friends.model.discord.AccessPacket
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

private const val DISCORD_BASE_URL = "https://discord.com/api/v10"
private val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}

/**
 * Retrieves an access token from Discord using the provided authorization code.
 *
 * @param code The authorization code provided by Discord.
 * @return An [AccessPacket] object containing the access token, expiration time, refresh token, scope, and token type.
 */
suspend fun getAccessToken(code: String): AccessPacket {
    val applicationID = Environment.getEnv("APPLICATION_ID")
    val applicationSecret = Environment.getEnv("APPLICATION_SECRET")
    val redirectURI = Environment.getEnv("REDIRECT_URL")

    assert(applicationID != null && applicationID != "your_id")
    assert(applicationSecret != null && applicationSecret != "your_secret")
    assert(redirectURI != null)

    val response = client.submitForm(
        url = "$DISCORD_BASE_URL/oauth2/token",
        formParameters = parameters {
            append("client_id", applicationID!!)
            append("client_secret", applicationSecret!!)
            append("grant_type", "authorization_code")
            append("code", code)
            append("redirect_uri", redirectURI!!)
            append("scope", "email identify")
        }
    )
    return response.body<AccessPacket>()
}