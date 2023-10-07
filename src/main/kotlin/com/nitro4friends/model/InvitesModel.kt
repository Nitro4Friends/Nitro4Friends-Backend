package com.nitro4friends.model

import com.nitro4friends.utils.toCalendar
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

/**
 * Represents an invite model.
 * This class is used to store information about an invitation.
 *
 * @property uid The unique identifier for the invite. Default value is -1.
 * @property inviterIdentifier The identifier of the inviter.
 * @property invitedIdentifier The identifier of the invited person.
 * @property invitedDate The date when the person was invited. Default value is the current date and time.
 */
@Serializable
data class InvitesModel(
    val uid: Long = -1,
    val inviterIdentifier: String,
    val invitedIdentifier: String,
    val invitedDate: Calendar = Calendar.now()
)

/**
 * Represents the database table "invites".
 */
internal object InvitesModelTable : Table("invites") {
    val uid = long("uid").autoIncrement()
    val inviterIdentifier = uuid("inviter_identifier").references(UserModelTable.identifier, onDelete = ReferenceOption.CASCADE)
    val invitedIdentifier = uuid("invited_identifier").references(UserModelTable.identifier, onDelete = ReferenceOption.CASCADE)
    val invitedDate = timestamp("invited_date").defaultExpression(CurrentTimestamp())

    override val primaryKey = PrimaryKey(uid)
}


/**
 * Retrieves a list of invites from the database for a given identifier.
 *
 * @param identifier The identifier of the inviter.
 *
 * @return The list of invites for the given identifier.
 */
fun getInvites(identifier: UUID): List<InvitesModel> = transaction {
    InvitesModelTable.select { InvitesModelTable.inviterIdentifier eq identifier }
        .map {
            InvitesModel(
                it[InvitesModelTable.uid],
                it[InvitesModelTable.inviterIdentifier].toString(),
                it[InvitesModelTable.invitedIdentifier].toString(),
                it[InvitesModelTable.invitedDate].toCalendar()
            )
        }
}

/**
 * Returns the count of invites for a given identifier.
 *
 * @param identifier The identifier to get invites count for.
 * @return The count of invites for the given identifier.
 */
fun getInvitesCount(identifier: UUID): Long = transaction {
    InvitesModelTable.select { InvitesModelTable.inviterIdentifier eq identifier }.count()
}

/**
 * Retrieves an [InvitesModel] by the provided identifier.
 *
 * @param identifier The identifier to search for an invite.
 * @return The [InvitesModel] matching the specified identifier, or null if not found.
 */
fun getInvitedById(identifier: UUID): InvitesModel? = transaction {
    InvitesModelTable.select { InvitesModelTable.invitedIdentifier eq identifier }
        .map {
            InvitesModel(
                it[InvitesModelTable.uid],
                it[InvitesModelTable.inviterIdentifier].toString(),
                it[InvitesModelTable.invitedIdentifier].toString(),
                it[InvitesModelTable.invitedDate].toCalendar()
            )
        }.firstOrNull()
}

/**
 * Adds an invite to the InvitesModelTable.
 *
 * @param inviterIdentifier the identifier of the inviter
 * @param invitedIdentifier the identifier of the invited person
 */
fun addInvite(inviterIdentifier: UUID, invitedIdentifier: UUID) = transaction {
    InvitesModelTable.insert {
        it[InvitesModelTable.inviterIdentifier] = inviterIdentifier
        it[InvitesModelTable.invitedIdentifier] = invitedIdentifier
    }
}
