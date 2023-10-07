package com.nitro4friends.model.discord

import kotlinx.serialization.Serializable

@Serializable
data class DiscordUser(
    val accent_color: Int,
    val avatar: String,
    val avatar_decoration_data: AvatarDecorationData?,
    val banner: String,
    val banner_color: String,
    val discriminator: String,
    val email: String?,
    val flags: Int,
    val global_name: String,
    val id: String,
    val locale: String,
    val mfa_enabled: Boolean,
    val premium_type: Int,
    val public_flags: Int,
    val username: String,
    val verified: Boolean
)

@Serializable
data class AvatarDecorationData(
    val asset: String,
    val sku_id: String
)