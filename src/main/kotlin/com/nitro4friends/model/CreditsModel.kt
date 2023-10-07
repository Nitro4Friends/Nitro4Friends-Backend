package com.nitro4friends.model

import com.nitro4friends.utils.toCalendar
import dev.fruxz.ascend.tool.time.calendar.Calendar
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

/**
 * This class represents a credit modification model.
 *
 * @property uid The unique identifier of the credit modification. Default value is -1.
 * @property clientID The unique identifier associated with the credit modification.
 * @property amount The amount of the credit modification. Default value is 0.
 * @property modifyDate The modification date of the credit modification. Default value is the current date and time.
 * @property reason The reason for the credit modification. Default value is "ad-banner".
 */
@Serializable
data class CreditsModel(
    val uid: Long = -1,
    val clientID: String,
    val amount: Long = 0,
    val modifyDate: Calendar = Calendar.now(),
    val reason: String = "ad-banner"
)

/**
 * This class represents a database table for credit modifications.
 *
 * Table Name: credits
 *
 * Columns:
 * - uid: Auto-incrementing long column representing the unique identifier for each credit modification.
 * - identifier: UUID column referencing the identifier in UserModelTable. Deletes on reference cascade.
 * - amount: Long column representing the amount of credit modification.
 * - modifyDate: Timestamp column with a default expression of the current timestamp.
 * - reason: Varchar column with a maximum length of 255 characters representing the reason for the credit modification.
 *
 * Primary Key:
 * - uid: Primary key column for the table.
 */
internal object CreditsModelTable : Table("credits") {
    val uid = long("uid").autoIncrement()
    val clientID = varchar("client_id", 24)
    val amount = long("amount")
    val modifyDate = timestamp("modify_date").defaultExpression(CurrentTimestamp())
    val reason = varchar("reason", 255)

    override val primaryKey = PrimaryKey(uid)
}


/**
 * Retrieves the credit modifications for a given identifier.
 *
 * @param clientID The unique identifier for which to retrieve the credit modifications.
 * @return A list of [CreditsModel] objects representing the credit modifications.
 */
fun getCreditModifications(clientID: String): List<CreditsModel> = transaction {
    return@transaction CreditsModelTable.select { CreditsModelTable.clientID eq clientID }.map {
        CreditsModel(
            uid = it[CreditsModelTable.uid],
            clientID = it[CreditsModelTable.clientID].toString(),
            amount = it[CreditsModelTable.amount],
            modifyDate = it[CreditsModelTable.modifyDate].toCalendar(),
            reason = it[CreditsModelTable.reason]
        )
    }
}

/**
 * Updates the credit modification for the specified identifier with the given modification details.
 * The existing credit modification will be replaced with the new details in the database.
 *
 * @param clientID The unique identifier of the credit modification.
 * @param modification The new credit modification details.
 */
fun addCreditModification(clientID: String, modification: CreditsModel) = transaction {
    CreditsModelTable.insert {
        it[CreditsModelTable.clientID] = clientID
        it[CreditsModelTable.amount] = modification.amount
        it[CreditsModelTable.modifyDate] = modification.modifyDate.javaInstant
        it[CreditsModelTable.reason] = modification.reason
    }
}