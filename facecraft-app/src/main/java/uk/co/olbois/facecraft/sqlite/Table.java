package uk.co.olbois.facecraft.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Table in an SQLite database
 * - currently only some integer primary keys are supported.
 *
 * @author Ian Clement (ian.clement@johnabbott.qc.ca)
 */
public abstract class Table<T extends Identifiable<Long>> implements CRUDRepository<Long, T> {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private final SQLiteOpenHelper dbh;

    private final String name;
    private final List<Column> columns;

    /**
     * Create a database table
     * @param dbh the handler that connects to the sqlite database.
     * @param name the table name.
     */
    public Table(SQLiteOpenHelper dbh, String name) {
        this.name = name;
        this.dbh = dbh;
        columns = new LinkedList<>();
        columns.add(new Column("_id", Column.Type.INTEGER).primaryKey().autoincrement());
    }

    /** Getters **/
    public SQLiteOpenHelper getDatabaseHandler() {
        return dbh;
    }

    public String getName() {
        return name;
    }

    /**
     * Add a column to the table.
     * @param column column to add to the table.
     */
    public void addColumn(Column column) {
        columns.add(column);
    }

    /**
     * Get the SQL CREATE TABLE statement for this table.
     * @return SQL CREATE TABLE statement.
     */
    public String getCreateTableStatement() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("CREATE TABLE %s (", name));

        boolean first = false;
        for(Column column : columns) {
            if(!first)
                first = true;
            else
                sb.append(", ");
            sb.append(column.toString());
        }
        sb.append(");");
        return sb.toString();
    }

    /**
     * Get the SQL DROP TABLE statement for this table.
     * @return SQL DROP TABLE statement.
     */
    public String getDropTableStatement() {
        return String.format("DROP TABLE IF EXISTS %s;", name);
    }

    /**
     * Get an array of column names to produce a SELECT * FROM ...
     * @return
     */
    public String[] getSelectAll() {
        String[] selection = new String[columns.size()];
        for(int i = 0; i < selection.length; i++)
            selection[i] = columns.get(i).getName();
        return selection;
    }

    /**
     * Check that the table has initial data.
     * @return
     */
    public boolean hasInitialData() {
        return false;
    }

    /**
     * Populate table with initial data.
     * Precondition: table has been created.
     * Note: this method is called during the handler's onCreate method where a writable database is opne
     *       trying to get a second writable database will throw an error, hence the parameter.
     * @param database
     */
    public void initialize(SQLiteDatabase database) {
    }

    /**
     * Create a ContentValues object from an element
     * @param element
     * @return
     */
    public abstract ContentValues toContentValues(T element) throws DatabaseException;

    /**
     * Create an element from a query Cursor
     * Precondition: the cursor is at the correct location in the dataset.
     * @param cursor
     * @return
     * @throws DatabaseException
     */
    public abstract T fromCursor(Cursor cursor) throws DatabaseException;


    /** CRUD Operations */

    @Override
    public Long create(T element) throws DatabaseException  {

        SQLiteDatabase database = dbh.getWritableDatabase();

        // Id of inserted element, -1 if error(?).
        long insertId = -1;

        // insert into DB
        try {
            ContentValues values = toContentValues(element);
            insertId = database.insertOrThrow(name, null, values);
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
        finally {
            database.close();
        }

        element.setId(insertId);

        return insertId;
    }

    @Override
    public T read(Long id) throws DatabaseException {

        SQLiteDatabase database = dbh.getReadableDatabase();

        // query database
        String[] projection = getSelectAll();
        Cursor cursor = database.query(name, projection, "_id =?", new String[] { String.valueOf(id) }, null, null, null, null);

        // check for result
        if(cursor == null)
            throw new DatabaseException("Operation read(" + id + "): no element with that id");

        // check that only a single row is returned.
        cursor.moveToFirst();
        if(!cursor.isLast())
            throw new DatabaseException("Operation read(" + id + "): more than one row matches. Aborting.");

        T element = fromCursor(cursor);
        element.setId(id);
        return element;
    }

    @Override
    public List<T> readAll() throws DatabaseException {
        SQLiteDatabase database = dbh.getReadableDatabase();

        List<T> elements = new ArrayList<>();

        String[] selection = getSelectAll();
        Cursor cursor = database.query(name, selection, null, null, null, null, null);
        if(cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                T element = fromCursor(cursor);
                element.setId(cursor.getLong(0));
                elements.add(element);
                cursor.moveToNext();
            }
            // makeFactory sure to close the cursor
            cursor.close();
        }
        return elements;
    }

    @Override
    public boolean update(T element) throws DatabaseException {

        SQLiteDatabase database = dbh.getWritableDatabase();

        ContentValues values = toContentValues(element);
        String idStr = String.valueOf(element.getId());
        int rows = database.update(name, values, "_id = ?", new String[]{idStr});
        database.close();

        return rows == 1;
    }

    @Override
    public boolean delete(T element) throws DatabaseException {
        SQLiteDatabase database = dbh.getWritableDatabase();
        String idStr = String.valueOf(element.getId());
        int rows = database.delete(name, "_id = ?", new String[]{idStr});
        database.close();
        if(rows > 1)
            throw new DatabaseException("More than 1 row deleted when deleting note.. possible database corruption.");
        return rows == 1;
    }
}
