@file:Suppress("unused")

package online.viestudio.paperkit.exposed

import online.viestudio.paperkit.koin.plugin
import online.viestudio.paperkit.plugin.KitPlugin
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.koin.core.component.KoinComponent

abstract class ExposedRepository : KoinComponent {

    protected val plugin: KitPlugin by plugin()
    protected val database: Database by plugin()

    protected inline infix fun Expression<Boolean>.and(block: SqlExpressionBuilder.() -> Op<Boolean>): Op<Boolean> {
        return and(block(SqlExpressionBuilder))
    }

    protected inline infix fun Expression<Boolean>.or(block: SqlExpressionBuilder.() -> Op<Boolean>): Op<Boolean> {
        return or(block(SqlExpressionBuilder))
    }

    protected suspend inline fun <T : Any> transaction(t: T, crossinline block: Transaction.() -> Unit): T =
        transaction {
            block()
            t
        }

    protected suspend inline fun <T> transaction(crossinline block: Transaction.() -> T): T =
        newSuspendedTransaction(context = plugin.context, db = database) { block() }
}
