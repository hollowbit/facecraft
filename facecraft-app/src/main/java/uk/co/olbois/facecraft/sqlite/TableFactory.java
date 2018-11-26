/*
 * Copyright (c) 2018 Ian Clement.  All rights reserved.
 */


package uk.co.olbois.facecraft.sqlite;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * A Factory for automatically generating Table based on the structure of a Model class
 */
public class TableFactory<T extends Identifiable<Long>> {


    public static <T extends Identifiable<Long>> TableFactory<T> makeFactory(SQLiteOpenHelper dbh, Class<T> model) {
        return new TableFactory<>(dbh, model);
    }

    public static final SimpleDateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private SQLiteOpenHelper dbh;
    private Class<T> model;
    private List<T> seedData;
    private SimpleDateFormat dateFormat;

    private TableFactory(SQLiteOpenHelper dbh, Class<T> model) {
        this.dbh = dbh;
        this.model = model;
        dateFormat = DEFAULT_DATE_FORMAT;
    }

    public void useDateFormat(String dateFormatString) {
        dateFormat = new SimpleDateFormat(dateFormatString);
    }

    public TableFactory<T> setSeedData(List<T> seedData) {
        this.seedData = seedData;
        return this;
    }

    public Table<T> getTable() {

        final String tableName = stripPackageName(model.getName()).toLowerCase();

        Table<T> table = new Table<T>(dbh, tableName) {

            @Override
            public T fromCursor(Cursor cursor) throws DatabaseException {

                T element = null;
                try {
                    element = model.newInstance();
                } catch (InstantiationException e) {
                    throw new DatabaseException(e);
                } catch (IllegalAccessException e) {
                    throw new DatabaseException(e);
                }

                // get order to extract from cursor
                String[] columnNames = getSelectAll();

                for (Method method : getSetters(model)) {

                    String methodName = stripPackageName(method.getName());

                    // determine column name
                    String columnName;
                    if (methodName.equals("setId"))
                        columnName = "_id";
                    else {
                        methodName = methodName.replaceAll("^set", "");
                        // lowercase field name
                        char first = methodName.charAt(0);
                        columnName = Character.toLowerCase(first) + methodName.substring(1);
                    }

                    int pos = -1;
                    for (int i = 0; i < columnNames.length && pos < 0; i++)
                        if (columnNames[i].equals(columnName))
                            pos = i;

                    // setter does not correspond to a column skip
                    if (pos < 0)
                        continue;

                    // of the cursor is null, skip as there's nothing to do.
                    if(cursor.isNull(pos))
                        continue;

                    Class<?>[] parameters = method.getParameterTypes();
                    if (parameters.length != 1)
                        throw new DatabaseException("Error: setter " + method.toString() + " takes more than one argument...");

                    Class<?> type = parameters[0];

                    try {
                        switch (type.getName()) {
                            case "java.lang.Integer":
                            case "int":
                                method.invoke(element, cursor.getInt(pos));
                                break;

                            case "java.lang.Long":
                            case "long":
                                method.invoke(element, cursor.getLong(pos));
                                break;

                            case "java.lang.Boolean":
                            case "boolean":
                                method.invoke(element, cursor.getInt(pos) == 1);
                                break;

                            case "java.lang.Double":
                            case "double":
                                method.invoke(element, cursor.getDouble(pos));
                                break;

                            case "java.lang.Float":
                            case "float":
                                method.invoke(element, cursor.getFloat(pos));
                                break;

                            case "java.lang.String":
                                method.invoke(element, cursor.getString(pos));
                                break;

                            case "java.util.Date":
                                method.invoke(element, dateFormat.parse(cursor.getString(pos)));
                                break;

                            case "android.graphics.Bitmap":
                                byte[] bytes = cursor.getBlob(pos);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                method.invoke(element, bitmap);
                                break;

                            default:
                                if(type.isEnum())
                                    method.invoke(element, Enum.valueOf((Class<Enum>) type, cursor.getString(pos)));
                        }
                    } catch (InvocationTargetException e) {
                        throw new DatabaseException(e);
                    } catch (IllegalAccessException e) {
                        throw new DatabaseException(e);
                    } catch (ParseException e) {
                        throw new DatabaseException(e);
                    }
                }

                return element;
            }

            @Override
            public ContentValues toContentValues(T element) {
                ContentValues contentValues = new ContentValues();
                for(Method method : getGetters(model)) {
                    String methodName = stripPackageName(method.getName());

                    // determine column name
                    String columnName;
                    if (methodName.equals("getId") || methodName.equals("getClass")) {
                        continue;
                    }
                    else {
                        methodName = methodName.replaceAll("^get","").replaceAll("^is", "");
                        // lowercase field name
                        char first = methodName.charAt(0);
                        columnName = Character.toLowerCase(first) + methodName.substring(1);
                    }

                    Object value = null;
                    try {
                        value = method.invoke(element);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }

                    switch(method.getReturnType().getName()) {
                        case "java.lang.Integer":
                        case "int":
                            contentValues.put(columnName, (Integer) value);
                            break;

                        case "java.lang.Long":
                        case "long":
                            contentValues.put(columnName, (Long) value);
                            break;

                        case "java.lang.Boolean":
                        case "boolean":
                            if(value == null)  // see above
                                contentValues.put(columnName, (Boolean) null);
                            else
                                contentValues.put(columnName, (Boolean) value ? 1 : 0);
                            break;

                        case "java.lang.Double":
                        case "double":
                            contentValues.put(columnName, (Double) value);
                            break;

                        case "java.lang.Float":
                        case "float":
                            contentValues.put(columnName, (Float) value);
                            break;

                        case "java.lang.String":
                            contentValues.put(columnName, (String) value);
                            break;

                        case "java.util.Date":
                            if(value == null) // see above
                                contentValues.put(columnName, (String) null);
                            else
                                contentValues.put(columnName, dateFormat.format((Date) value));
                            break;

                        case "android.graphics.Bitmap":
                            Bitmap bitmap = (Bitmap) value;
                            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100,  outputStream);
                            contentValues.put(columnName, outputStream.toByteArray());

                        default:
                            if(method.getReturnType().isEnum() && value != null)
                                contentValues.put(columnName, value.toString());

                    }

                }
                return contentValues;
            }


            @Override
            public boolean hasInitialData() {
                return seedData != null;
            }

            @Override
            public void initialize(SQLiteDatabase database) {
                if(seedData != null) {
                    for(T element : seedData) {
                        ContentValues values = toContentValues(element);
                        database.insertOrThrow(tableName, null, values);
                    }
                }
            }

        };


        for(Method method : getGetters(model)) {

            String methodName = stripPackageName(method.getName());
            if(methodName.equals("getId") || methodName.equals("getClass"))
                continue;

            methodName = methodName.replaceAll("^get","").replaceAll("^is", "");

            // lowercase field name
            char first = methodName.charAt(0);
            methodName = Character.toLowerCase(first) + methodName.substring(1);

            // determine the sqlite datatype. Unsupported data-types are skipped.
            Column.Type dataType = null;
            switch(method.getReturnType().getName()) {
                case "java.lang.Integer":
                case "int":
                case "java.lang.Long":
                case "long":
                case "java.lang.Boolean":
                case "boolean":
                    dataType = Column.Type.INTEGER;
                    break;

                case "java.lang.Double":
                case "double":
                case "java.lang.Float":
                case "float":
                    dataType = Column.Type.REAL;
                    break;

                case "java.lang.String":
                case "java.util.Date":
                    dataType = Column.Type.TEXT;
                    break;

                case "android.graphics.Bitmap":
                    dataType = Column.Type.BLOB;
                    break;

                default:
                    if(method.getReturnType().isEnum())
                        dataType = Column.Type.TEXT;

            }

            Column column = new Column(methodName, dataType);

            // add not-null constraint if the NotNull is present on the getter.
            if(method.isAnnotationPresent(NotNull.class))
                column.notNull();

            table.addColumn(column);
        }

        return table;
    }

    /**
     * Removes the package name prefix from a Java class.
     * @param name
     * @return
     */
    private static String stripPackageName(String name) {
        return name.replaceFirst("^.*\\.", "");
    }

    /**
     * Get all the getters of a specific class.
     * @param model
     * @param <T>
     * @return
     */
    private static <T> List<Method> getGetters(Class<T> model) {
        List<Method> getters = new LinkedList<>();
        for(Method method : model.getMethods()) {
            String methodName = stripPackageName(method.getName());

            if (methodName.startsWith("get") || methodName.startsWith("is"))
                getters.add(method);
        }
        return getters;
    }

    /**
     * Get all the setters of a specific class.
     * @param model
     * @param <T>
     * @return
     */
    private static <T> List<Method> getSetters(Class<T> model) {
        List<Method> getters = new LinkedList<>();
        for(Method method : model.getMethods()) {
            String methodName = stripPackageName(method.getName());

            if (methodName.startsWith("set"))
                getters.add(method);
        }
        return getters;
    }
}

