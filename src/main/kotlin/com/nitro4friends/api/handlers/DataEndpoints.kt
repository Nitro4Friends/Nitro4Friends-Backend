package com.nitro4friends.api.handlers

import com.nitro4friends.cache.UserStorage
import io.javalin.apibuilder.ApiBuilder.before
import io.javalin.apibuilder.ApiBuilder.get
import io.javalin.apibuilder.EndpointGroup
import io.javalin.http.Context
import io.javalin.http.Header
import io.javalin.http.servlet.JavalinServletContext

/**
 * DataEndpoints class represents a group of endpoints for data fetching.
 * It implements the EndpointGroup interface.
 */
class DataEndpoints: EndpointGroup {

    /**
     * Adds the endpoints for data fetching to the existing group of endpoints.
     */
    override fun addEndpoints() {
        before { ctx ->
            val (tokenType, uid) = ctx.header(Header.AUTHORIZATION)?.split(" ")?.toTypedArray() ?: return@before ctx.denyAccess()
            if (tokenType != "Bearer") return@before ctx.denyAccess()

            if (UserStorage.exists(uid)) {
                ctx.attribute("uid", uid)
                return@before
            }
            ctx.denyAccess()
        }

        get("/@me") { ctx ->
            val uid = ctx.attribute<String>("uid") ?: return@get ctx.denyAccess()
            val user = UserStorage.getUser(uid) ?: return@get ctx.denyAccess()
            // Sending the user only public information back
            ctx.json(user.second)
        }
    }

    /**
     * This method denies access to the current endpoint.
     *
     * It sets the HTTP status code to 403 (Forbidden) and sends a response message indicating that the user is not allowed to access the endpoint. It also logs the IP address of the user and clears any pending tasks associated with the request.
     *
     * @receiver The context object representing the current HTTP request and response.
     */
    private fun Context.denyAccess() {
        status(403)
        result("You are not allowed to access this endpoint. Your IP has been logged. If you believe this is a mistake, please contact the server owner.")
        (this as JavalinServletContext).tasks.clear()
        val ip = header("X-Forwarded-For")
        println("Someone tried to access the data endpoint with wrong credentials from $ip")
    }
}