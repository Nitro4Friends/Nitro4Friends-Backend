package com.nitro4friends.model

import com.nitro4friends.model.discord.AccessPacket
import com.nitro4friends.model.discord.DiscordUser
import com.nitro4friends.utils.toCalendar
import dev.fruxz.ascend.extension.data.RandomTagType
import dev.fruxz.ascend.extension.data.buildRandomTag
import dev.fruxz.ascend.extension.data.generateRandomTag
import dev.fruxz.ascend.extension.switch
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.replace
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Serializable
data class UserModel (
    val identifier: String,
    val clientID: String,
    val userName: String,
    val email: String?,
    val avatar: String,
    val avatarDecoration: String? = null,

    val accessPacket: AccessPacket,

    val inviteCode: String,
    val joinDate: Long
)


internal object UserModelTable : Table("users") {
    val identifier = uuid("identifier")
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

    override val primaryKey = PrimaryKey(identifier)
}

fun getUser(identifier: UUID): UserModel? = transaction {
    val resultRow = UserModelTable.select { UserModelTable.identifier eq identifier }.firstOrNull() ?: return@transaction null

    return@transaction UserModel(
        resultRow[UserModelTable.identifier].toString(),
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

fun createOrUpdateUser(identifier: UUID, accessPacket: AccessPacket, discordUser: DiscordUser) = transaction {
    var user = getUser(identifier) ?: UserModel(
        identifier.toString(),
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
        it[UserModelTable.identifier] = UUID.fromString(user.identifier)
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
}