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
    val sampleUserTable: Table<SampleUser>

    init {
        val users = mutableListOf(
                SampleUser(1, "JJ", "Password123"),
                SampleUser(2, "Nate", "Password123"),
                SampleUser(3, "Alex", "a")
        )
        val connections = mutableListOf(
                ServerConnection(0,"mc.hollowbit.net", 25565, 3, ServerConnection.Role.MEMBER, 1),
                ServerConnection(1,"realserver.realdomain.com", 25565, 16, ServerConnection.Role.ADMIN, 1),
                ServerConnection(0,"mc.hollowbit.net", 25565, 3, ServerConnection.Role.OWNER, 2),
                ServerConnection(0,"realhost.realdomain.com", 25565, 3, ServerConnection.Role.MEMBER, 2)
        )



        connectionsTable = TableFactory.makeFactory(this, ServerConnection::class.java)
                .setSeedData(connections)
                .table

        sampleUserTable = TableFactory.makeFactory(this, SampleUser::class.java)
                .setSeedData(users)
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
        database.execSQL(sampleUserTable.getCreateTableStatement())

        if (connectionsTable.hasInitialData())
            connectionsTable.initialize(database)
        if(sampleUserTable.hasInitialData())
            sampleUserTable.initialize(database)
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