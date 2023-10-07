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
 * RedeemModel represents a redeem request made by a user.
 *
 * @property uid The unique identifier of the redeem request. Default value is -1 if not specified.
 * @property clientID The identifier of the redeem request.
 * @property redeemedDate The date when the redeem request was redeemed. Defaults to the current date and time.
 * @property paidAmount The amount paid for the redeem request. Defaults to 0 if not specified.
 * @property status The status of the redeem request. Defaults to RedeemStatus.PENDING if not specified.
 * @property message Optional message associated with the redeem request.
 */
@Serializable
data class RedeemModel(
    val uid: Long = -1,
    val clientID: String,
    val redeemedDate: Calendar = Calendar.now(),
    val paidAmount: Int = 0,
    val status: RedeemStatus = RedeemStatus.PENDING,
    val message: String? = null
)

/**
 * Enumeration class representing the status of a redeem request.
 * It can have three possible values: PENDING, APPROVED, REJECTED.
 */
@Serializable
enum class RedeemStatus {
    PENDING,
    APPROVED,
    REJECTED
}

/**
 * This class represents the database table for redeem models.
 */
internal object RedeemModelTable : Table("redeem") {
    val uid = long("uid").autoIncrement()
    val clientID = varchar("client_id", 24).references(UserModelTable.clientID, onDelete = ReferenceOption.CASCADE)
    val redeemedDate = timestamp("redeemed_date").defaultExpression(CurrentTimestamp())
    val paidAmount = integer("paid_amount")
    val status = enumerationByName("status", 10, RedeemStatus::class)
    val message = varchar("message", 255).nullable()

    override val primaryKey = PrimaryKey(uid)
}

/**
 * Retrieves a list of redeem models based on the provided identifier.
 *
 * @param identifier The identifier used to filter the redeem models.
 * @return A list of redeem models matching the provided identifier.
 */
fun getRedeems(clientID: String): List<RedeemModel> = transaction {
    RedeemModelTable.select { RedeemModelTable.clientID eq clientID }
        .map {
            RedeemModel(
                it[RedeemModelTable.uid],
                it[RedeemModelTable.clientID].toString(),
                it[RedeemModelTable.redeemedDate].toCalendar(),
                it[RedeemModelTable.paidAmount],
                it[RedeemModelTable.status],
                it[RedeemModelTable.message]
            )
        }
}

/**
 * Inserts a RedeemModel into the RedeemModelTable
 *
 * @param model The RedeemModel to be added
 */
fun addRedeem(model: RedeemModel) = transaction {
    RedeemModelTable.insert {
        it[clientID] = model.clientID
        it[redeemedDate] = model.redeemedDate.javaInstant
        it[paidAmount] = model.paidAmount
        it[status] = model.status
        it[message] = model.message
    }
}