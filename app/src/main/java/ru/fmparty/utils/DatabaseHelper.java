package ru.fmparty.utils;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ru.fmparty.apiaccess.Consts;

public class DatabaseHelper extends SQLiteOpenHelper {

    //TODO constants to ENUM refactoring
    public static final String CHATS_TABLE = "chats";
    public static final String MSGS_TABLE = "msgs";
    public static final String USERS_TABLE = "users";
    public static final String AUX_TABLE = "aux";

    public static final String CHAT_ID_COLUMN = "id";
    public static final String CHAT_ADMIN_ID_COLUMN = "admin_id";
    public static final String CHAT_NAME_COLUMN = "name";
    public static final String CHAT_IMAGE_COLUMN = "image";
    public static final String CHAT_DESCR_COLUMN = "descr";
    public static final String CHAT_FDATE_COLUMN = "fdate";
    public static final String CHAT_CITY_COLUMN = "city";

    public static final String MSG_ID_COLUMN = "id";
    public static final String MSG_CHAT_ID_COLUMN = "chat_id";
    public static final String MSG_USER_ID_COLUMN = "user_id";
    public static final String MSG_USER_NAME_COLUMN = "user_name";
    public static final String MSG_TEXT_COLUMN = "text";

    public static final String USER_ID_COLUMN = "id";
    public static final String USER_IMAGE_COLUMN = "image";

    public static final String AUX_SIGN_COLUMN = "sign";
    public static final String AUX_VALUE_COLUMN = "value";
    public static final String AUX_OPTIONAl_COLUMN = "optional";

    public static final String TAG = "FMParty DatabaseHelper";

    private static final String CREATE_CHATS_TABLE =
            "CREATE TABLE " + CHATS_TABLE + " (\n " +
            CHAT_ID_COLUMN + " integer, " +
            CHAT_ADMIN_ID_COLUMN + " integer, " +
            CHAT_NAME_COLUMN + " text, " +
            CHAT_IMAGE_COLUMN + " text, " +
            CHAT_DESCR_COLUMN + " text, " +
            CHAT_FDATE_COLUMN + " text, " +
            CHAT_CITY_COLUMN + " text ); ";

    private static final String CREATE_MSGS_TABLE =
            "CREATE TABLE " + MSGS_TABLE + "(\n " +
            MSG_ID_COLUMN + " integer, " +
            MSG_CHAT_ID_COLUMN + " integer, " +
            MSG_USER_ID_COLUMN + " integer, " +
            MSG_USER_NAME_COLUMN + " text, " +
            MSG_TEXT_COLUMN + " text ); ";

    private static final String CREATE_USERS_TABLE =
            "CREATE TABLE " + USERS_TABLE + "(\n " +
                    USER_ID_COLUMN + " integer, " +
                    USER_IMAGE_COLUMN + " text ); ";

    private static final String CREATE_AUX_TABLE =
            "CREATE TABLE " + AUX_TABLE + "(\n " +
            AUX_SIGN_COLUMN + " text, " +
            AUX_VALUE_COLUMN + " text, " +
            AUX_OPTIONAl_COLUMN + " text ); ";


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
        db.execSQL(CREATE_CHATS_TABLE);
        db.execSQL(CREATE_MSGS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
        db.execSQL(CREATE_AUX_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Обновляемся с версии " + oldVersion + " на версию " + newVersion);

        db.execSQL("DROP TABLE IF EXISTS " + CHATS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MSGS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + AUX_TABLE);

        onCreate(db);
    }
}
