package com.nitro4friends.api.handlers

import com.nitro4friends.cache.UserStorage
import com.nitro4friends.model.createOrUpdateUser
import com.nitro4friends.utils.Environment
import com.nitro4friends.utils.getAccessToken
import com.nitro4friends.utils.getUserInfos
import dev.fruxz.ascend.extension.logging.getItsLogger
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.HttpStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.launch
import java.util.UUID

class DiscordRedirect : EndpointGroup {

    /**
     * Adds the necessary endpoints for handling Discord authentication.
     * This endpoint is responsible for handling the callback when a user returns from the Discord OAuth2 authorization page.
     * It exchanges the authorization code received from Discord for an access token and saves it in the session.
     * It also logs relevant information and redirects the user to the specified URL.
     */
    override fun addEndpoints() {

        get { ctx ->
            val code = ctx.queryParam("code")
            val uid = ctx.queryParam("state")

            if (code == null || uid == null) {
                ctx.status(400)
                return@get
            }

            ctx.status(HttpStatus.OK)
            getItsLogger().info("Uid $uid authenticated with Discord.")
            getItsLogger().info("Trying to exchange code for access token...")

            try {
                ctx.future {
                    CoroutineScope(Dispatchers.Default).launch {
                        val accessPacket = getAccessToken(code)
                        getItsLogger().info("Successfully exchanged code for access token.")

                        getItsLogger().info("Trying to retrieve user information...")
                        val discordUser = getUserInfos(accessPacket)
                        getItsLogger().info("Successfully retrieved user information. (ID: ${discordUser.id}, Username: ${discordUser.username})")

                        val userModel = createOrUpdateUser(accessPacket, discordUser)
                        UserStorage.addUser(uid, userModel)
                        getItsLogger().info("Successfully created or updated user in database.")

                        Environment.getEnv("AUTH_URL")?.let { ctx.redirect(it) } ?: ctx.status(500)
                    }.asCompletableFuture()
                }
            } catch (e: Exception) {
                ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                getItsLogger().info("Failed to exchange code for access token because of ${e.message}")
            }
        }

    }

}