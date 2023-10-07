package com.nitro4friends.model

import com.nitro4friends.model.discord.AccessPacket
import com.nitro4friends.model.discord.DiscordUser
import com.nitro4friends.utils.toCalendar
import dev.fruxz.ascend.extension.data.RandomTagType
import dev.fruxz.ascend.extension.data.generateRandomTag
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*
import kotlin.time.Duration.Companion.milliseconds


/**
 * UserModel represents a user model in the system.
 *
 * @property identifier The unique identifier of the user.
 * @property clientID The client ID of the user.
 * @property userName The username of the user.
 * @property email The email of the user, nullable.
 * @property avatar The avatar URL of the user.
 * @property avatarDecoration The decoration of the avatar, nullable.
 * @property accessPacket The access packet containing access token information.
 * @property inviteCode The invite code of the user.
 * @property joinDate The join date of the user in milliseconds.
 */
@Serializable
data class UserModel (
    val clientID: String,
    val userName: String,
    val email: String?,
    val avatar: String,
    val avatarDecoration: String? = null,

    val accessPacket: AccessPacket,

    val inviteCode: String,
    val joinDate: Long
)


/**
 * UserModelTable is a database table representation of the "users" table.
 * It defines the structure of the table and provides access to its columns.
 */
internal object UserModelTable : Table("users") {
    val clientID = varchar("client_id", 24)
    val userName = varchar("user_name", 32)
    val email = varchar("email", 255).nullable()
    val avatar = varchar("avatar_url", 255)
    val avatarDecoration = varchar("avatar_decoration", 255).nullable()

    val accessToken = varchar("access_token", 255)
    val expires = timestamp("expires")
    val refreshToken = varchar("refresh_token", 255)
    val scope = varchar("scope", 255)
    val tokenType = varchar("token_type", 50)

    val inviteCode = varchar("invite_code", 24)
    val joinDate = timestamp("join_date").defaultExpression(CurrentTimestamp())

    override val primaryKey = PrimaryKey(clientID)
}

/**
 * Retrieves a user from the database based on the given identifier.
 *
 * @param identifier The unique identifier of the user.
 * @return The UserModel object representing the user, or null if the user is not found.
 */
fun getUser(clientID: String): UserModel? = transaction {
    val resultRow = UserModelTable.select { UserModelTable.clientID eq clientID }.firstOrNull() ?: return@transaction null

    return@transaction UserModel(
        resultRow[UserModelTable.clientID],
        resultRow[UserModelTable.userName],
        resultRow[UserModelTable.email],
        resultRow[UserModelTable.avatar],
        resultRow[UserModelTable.avatarDecoration],
        AccessPacket(
            resultRow[UserModelTable.accessToken],
            resultRow[UserModelTable.expires].toCalendar().durationToNow().inWholeMilliseconds,
            resultRow[UserModelTable.refreshToken],
            resultRow[UserModelTable.scope],
            resultRow[UserModelTable.tokenType]
        ),
        resultRow[UserModelTable.inviteCode],
        resultRow[UserModelTable.joinDate].toCalendar().timeInMilliseconds
    )
}

/**
 * Creates or updates a user in the database based on the given identifier, access packet, and Discord user information.
 * If a user with the given identifier already exists, the existing user will be updated with the provided information.
 * If no user with the given identifier exists, a new user will be created with the provided information.
 *
 * @param accessPacket The access packet containing access token information.
 * @param discordUser The Discord user information.
 */
fun createOrUpdateUser(accessPacket: AccessPacket, discordUser: DiscordUser): UserModel = transaction {
    var user = getUser(discordUser.id) ?: UserModel(
        discordUser.id,
        discordUser.username,
        discordUser.email,
        discordUser.avatar,
        discordUser.avatar_decoration_data?.asset,
        accessPacket,
        generateRandomTag(size = 24, prefix = "", case = RandomTagType.MIXED_CASE),
        System.currentTimeMillis()
    )

    user = user.copy(
        userName = discordUser.username,
        email = discordUser.email,
        avatar = discordUser.avatar,
        avatarDecoration = discordUser.avatar_decoration_data?.asset,
        accessPacket = accessPacket,
    )

    UserModelTable.replace {
        it[UserModelTable.clientID] = user.clientID
        it[UserModelTable.userName] = user.userName
        it[UserModelTable.email] = user.email
        it[UserModelTable.avatar] = user.avatar
        it[UserModelTable.avatarDecoration] = user.avatarDecoration

        it[UserModelTable.accessToken] = user.accessPacket.access_token
        it[UserModelTable.expires] = Calendar.now().plus(user.accessPacket.expires_in.milliseconds).javaInstant
        it[UserModelTable.refreshToken] = user.accessPacket.refresh_token
        it[UserModelTable.scope] = user.accessPacket.scope
        it[UserModelTable.tokenType] = user.accessPacket.token_type

        it[UserModelTable.inviteCode] = user.inviteCode
    }

    return@transaction user
}