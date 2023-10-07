package com.nitro4friends.model

import com.nitro4friends.utils.toCalendar
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * Represents an invitation model.
 * This class is used to store information about an invitation.
 *
 * @property uid The unique identifier for the invite. Default value is -1.
 * @property inviterId The identifier of the inviter.
 * @property invitedId The identifier of the invited person.
 * @property invitedDate The date when the person was invited. Default value is the current date and time.
 */
@Serializable
data class InvitesModel(
    val uid: Long = -1,
    val inviterId: String,
    val invitedId: String,
    val invitedDate: Calendar = Calendar.now()
)

/**
 * Represents the database table "invites".
 */
internal object InvitesModelTable : Table("invites") {
    val uid = long("uid").autoIncrement()
    val inviterId = varchar("inviter_id", 24)
    val invitedId = varchar("invited_id", 24)
    val invitedDate = timestamp("invited_date").defaultExpression(CurrentTimestamp())

    override val primaryKey = PrimaryKey(uid)
}


/**
 * Retrieves a list of invites from the database for a given identifier.
 *
 * @param clientID The identifier of the inviter.
 *
 * @return The list of invites for the given identifier.
 */
fun getInvites(clientID: String): List<InvitesModel> = transaction {
    InvitesModelTable.select { InvitesModelTable.inviterId eq clientID }
        .map {
            InvitesModel(
                it[InvitesModelTable.uid],
                it[InvitesModelTable.inviterId].toString(),
                it[InvitesModelTable.invitedId].toString(),
                it[InvitesModelTable.invitedDate].toCalendar()
            )
        }
}

/**
 * Returns the count of invites for a given identifier.
 *
 * @param clientID The identifier to get invites count for.
 * @return The count of invites for the given identifier.
 */
fun getInvitesCount(clientID: String): Long = transaction {
    InvitesModelTable.select { InvitesModelTable.inviterId eq clientID }.count()
}

/**
 * Retrieves an [InvitesModel] by the provided identifier.
 *
 * @param clientID The identifier to search for an invite.
 * @return The [InvitesModel] matching the specified identifier, or null if not found.
 */
fun getInvitedById(clientID: String): InvitesModel? = transaction {
    InvitesModelTable.select { InvitesModelTable.invitedId eq clientID }
        .map {
            InvitesModel(
                it[InvitesModelTable.uid],
                it[InvitesModelTable.inviterId].toString(),
                it[InvitesModelTable.invitedId].toString(),
                it[InvitesModelTable.invitedDate].toCalendar()
            )
        }.firstOrNull()
}

/**
 * Adds an invitation to the InvitesModelTable.
 *
 * @param inviterIdentifier the identifier of the inviter
 * @param invitedIdentifier the identifier of the invited person
 */
fun addInvite(inviterId: String, invitedId: String) = transaction {
    InvitesModelTable.insert {
        it[InvitesModelTable.inviterId] = inviterId
        it[InvitesModelTable.invitedId] = invitedId
    }
}
