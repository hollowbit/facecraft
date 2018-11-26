package uk.co.olbois.facecraft.model

import android.database.sqlite.SQLiteDatabase
import uk.co.olbois.facecraft.sqlite.TableFactory
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import uk.co.olbois.facecraft.sqlite.Table
import android.content.Context
import uk.co.olbois.facecraft.model.serverconnection.ServerConnection


class UniversalDatabaseHandler
/**
 * Construct a new database handler.
 * @param context The application context.
 */
(val context: Context) : SQLiteOpenHelper(context, DATABASE_FILE_NAME, null, DATABASE_VERSION) {

    /*  NoteDatabaseHandler Tables */
    val connectionsTable: Table<ServerConnection>

    init {
        val connections = mutableListOf(
                ServerConnection(0,"mc.hollowbit.net", 25565, 3, ServerConnection.Role.MEMBER),
                ServerConnection(1,"realserver.realdomain.com", 25565, 16, ServerConnection.Role.ADMIN)
        )
        connectionsTable = TableFactory.makeFactory(this, ServerConnection::class.java)
                .setSeedData(connections)
                .table
    }

    /**
     * Get the Category table.
     * @return The Category table.
     */
    /*public CategoryTable getCategoryTable() {
        return categoryTable;
    }*/

    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(connectionsTable.getCreateTableStatement())

        if (connectionsTable.hasInitialData())
            connectionsTable.initialize(database)
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.w(UniversalDatabaseHandler::class.java.name, "Upgrading database from version $oldVersion to $newVersion, which will destroy all old data")
        database.execSQL(connectionsTable.getDropTableStatement())
        onCreate(database)
    }

    companion object {

        /**
         * Filename to store the local database (on device).
         */
        private const val DATABASE_FILE_NAME = "notes.db"

        /**
         * Update this field for every structural change to the database.
         */
        private const val DATABASE_VERSION = 1
    }
}