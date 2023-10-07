package com.nitro4friends.model

import kotlinx.serialization.Serializable

/**
 * Represents the public data model for a client.
 *
 * @property clientID The unique identifier of the client.
 * @property userName The username of the client.
 * @property avatarURL The URL of the client's avatar.
 * @property avatarDecorationURL The URL of the client's avatar decoration. Nullable.
 * @property inviteCode The invite code of the client.
 * @property joinDate The join date of the client in milliseconds.
 * @property creditModifications The list of public credit models representing the credit modifications of the client. Default value is an empty list.
 * @property inviteTimestamps The list of invite timestamps for the client. Default value is an empty list.
 * @property redeems The list of public redeem models representing the redeems of the client. Default value is an empty list.
 */
@Serializable
data class ClientPublicDataModel(
    val clientID: String,
    val userName: String, // Do we need this?
    val avatarURL: String,
    val avatarDecorationURL: String? = null,
    val inviteCode: String,
    val joinDate: Long,
    val creditModifications: List<PublicCreditsModel> = listOf(),
    val inviteTimestamps: List<Long> = listOf(),
    val redeems: List<PublicRedeemModel> = listOf(),
    val totalCredits: Long = creditModifications.sumOf { it.amount }
)

/**
 * Represents a public credits model.
 *
 * @property amount The amount of credits.
 * @property timestamp The timestamp when the credits were modified.
 * @property reason The reason for the credit modification.
 */
@Serializable
data class PublicCreditsModel(
    val amount: Long,
    val timestamp: Long,
    val reason: String
)

/**
 * Represents a public redeem request made by a user.
 *
 * @property redeemedDate The date when the redeem request was redeemed.
 * @property paidAmount The amount paid for the redeem request.
 * @property status The status of the redeem request.
 * @property message Optional message associated with the redeem request.
 */
@Serializable
data class PublicRedeemModel(
    val redeemedDate: Long,
    val paidAmount: Int,
    val status: RedeemStatus,
    val message: String? = null
)

/**
 * Converts a UserModel object to a ClientPublicDataModel object.
 *
 * @return The converted ClientPublicDataModel object.
 */
fun UserModel.toClientPublicDataModel(): ClientPublicDataModel = ClientPublicDataModel(
    clientID = this.clientID,
    userName = this.userName,
    avatarURL = "https://cdn.discordapp.com/avatars/${this.clientID}/${this.avatar}.png",
    avatarDecorationURL = this.avatarDecoration?.let { "https://cdn.discordapp.com/avatar-decoration-presets/${this.avatarDecoration}.png"},
    inviteCode = this.inviteCode,
    joinDate = this.joinDate,
    creditModifications = getCreditModifications(this.clientID).map { it.toPublicCreditsModel() },
    inviteTimestamps = getInvites(this.clientID).map { it.invitedDate.timeInMilliseconds },
    redeems = getRedeems(this.clientID).map { it.toPublicRedeemModel() }
)

/**
 * Converts a [CreditsModel] object to a [PublicCreditsModel] object.
 *
 * @return A [PublicCreditsModel] object with the converted values.
 */
fun CreditsModel.toPublicCreditsModel(): PublicCreditsModel = PublicCreditsModel(
    amount = this.amount,
    timestamp = this.modifyDate.timeInMilliseconds,
    reason = this.reason
)

/**
 * Converts a `RedeemModel` object to a `PublicRedeemModel` object.
 *
 * @return The converted `PublicRedeemModel` object.
 */
fun RedeemModel.toPublicRedeemModel(): PublicRedeemModel = PublicRedeemModel(
    redeemedDate = this.redeemedDate.timeInMilliseconds,
    paidAmount = this.paidAmount,
    status = this.status,
    message = this.message
)