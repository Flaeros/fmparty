package ru.fmparty.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.fmparty.apiaccess.Consts;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_TABLE = "chats";

    public static final String CHAT_ID_COLUMN = "id";
    public static final String CHAT_ADMIN_ID_COLUMN = "admin_id";
    public static final String CHAT_NAME_COLUMN = "name";
    public static final String CHAT_IMAGE_COLUMN = "image";
    public static final String CHAT_DESCR_COLUMN = "descr";
    public static final String CHAT_FDATE_COLUMN = "fdate";
    public static final String CHAT_CITY_COLUMN = "city";

    public static final String TAG = "FMParty DatabaseHelper";

    private static final String DATABASE_CREATE_SCRIPT = "CREATE TABLE " + DATABASE_TABLE + " (\n" +
            "  " + CHAT_ID_COLUMN + " integer," +
            "  " + CHAT_ADMIN_ID_COLUMN + " integer," +
            "  " + CHAT_NAME_COLUMN + " text," +
            "  " + CHAT_IMAGE_COLUMN + " text," +
            "  " + CHAT_DESCR_COLUMN + " text," +
            "  " + CHAT_FDATE_COLUMN + " text," +
            "  " + CHAT_CITY_COLUMN + " text" +
            ");";

    DatabaseHelper(Context context) {
        super(context, Consts.SQLiteDB.get(), null, Integer.valueOf(Consts.DbVersion.get()));
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Запишем в журнал
        Log.w(TAG, "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(db);
    }
}
