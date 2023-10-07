package com.nitro4friends.model.discord

import kotlinx.serialization.Serializable

/**
 * Represents a Discord user.
 *
 * @property accent_color The accent color of the user.
 * @property avatar The URL of the user's avatar.
 * @property avatar_decoration_data The data for the avatar decoration, if any.
 * @property banner The URL of the user's banner.
 * @property banner_color The color of the user's banner.
 * @property discriminator The discriminator of the user.
 * @property email The email address of the user, nullable.
 * @property flags The user's flags.
 * @property global_name The global name of the user.
 * @property id The unique ID of the user.
 * @property locale The user's locale.
 * @property mfa_enabled Indicates whether Multi-Factor Authentication (MFA) is enabled for the user.
 * @property premium_type The type of premium subscription for the user.
 * @property public_flags The public flags of the user.
 * @property username The username of the user.
 * @property verified Indicates whether the user's email address is verified.
 */
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

/**
 * Represents the data for an avatar decoration.
 *
 * @property asset The asset used for the decoration.
 * @property sku_id The SKU ID of the decoration.
 */
@Serializable
data class AvatarDecorationData(
    val asset: String,
    val sku_id: String
)