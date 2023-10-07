package com.nitro4friends.model.discord

import kotlinx.serialization.Serializable

/**
 * AccessPacket represents a packet of data that contains information about an access token.
 * It is used to store the access token, expiration time, refresh token, scope, and token type.
 *
 * @property access_token The access token string.
 * @property expires_in The expiration time of the access token in milliseconds.
 * @property refresh_token The refresh token string.
 * @property scope The scope of the access token.
 * @property token_type The type of the token.
 */
@Serializable
data class AccessPacket(val access_token: String, val expires_in: Long,
                        val refresh_token: String, val scope: String,
                        val token_type: String) {

    val accessString: String
        get() = "$token_type $access_token"

}