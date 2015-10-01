package ru.fmparty.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ru.fmparty.apiaccess.Consts;
import ru.fmparty.entity.User;

public class InnerDB {
    private static DatabaseHelper databaseHelper;
    private static SQLiteDatabase mSqLiteDatabase;
    private static Activity currentActivity;

    private static final String TAG = "FlashMob InnerDB";

    private static void loadHelper(Activity activity){
        if(activity.equals(currentActivity))
            return;

        currentActivity = activity;
        databaseHelper = new DatabaseHelper(activity, Consts.SQLiteDB.get(), null, Integer.valueOf(Consts.DbVersion.get()));
        mSqLiteDatabase = databaseHelper.getWritableDatabase();
    }

    public static String getInnerUserId(Activity activity, long outerId) {
        loadHelper(activity);

        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.AUX_TABLE, new String[]{
                DatabaseHelper.AUX_SIGN_COLUMN, DatabaseHelper.AUX_VALUE_COLUMN}
                , DatabaseHelper.AUX_SIGN_COLUMN + " = 'innerUserId' and " + DatabaseHelper.AUX_OPTIONAl_COLUMN + " = '" + outerId + "'", null, null, null, null) ;

        String result = null;

        Log.d(TAG, "cursor.getCount() = " + cursor.getCount());

        if(cursor.getCount() != 0 ) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(DatabaseHelper.AUX_VALUE_COLUMN));
            Log.d(TAG, "userId InnerValueId = " + result);
        }

        return result;
    }

    public static void setInnerUserId(Activity activity, int id, long outerId) {
        loadHelper(activity);

        ContentValues newValues = new ContentValues();

        newValues.put(DatabaseHelper.AUX_SIGN_COLUMN, "innerUserId");
        newValues.put(DatabaseHelper.AUX_VALUE_COLUMN, String.valueOf(id));
        newValues.put(DatabaseHelper.AUX_OPTIONAl_COLUMN, String.valueOf(outerId));

        long result = mSqLiteDatabase.insert(DatabaseHelper.AUX_TABLE, null, newValues);
        Log.d(TAG, "result = " + result);
    }

    public static void clearData(Activity activity) {
        loadHelper(activity);

        mSqLiteDatabase.execSQL("DELETE FROM " + DatabaseHelper.CHATS_TABLE);
        mSqLiteDatabase.execSQL("DELETE FROM " + DatabaseHelper.MSGS_TABLE);
        mSqLiteDatabase.execSQL("DELETE FROM " + DatabaseHelper.AUX_TABLE);
    }


    public static String getUserImage(Activity activity, long userId) {
        loadHelper(activity);

        Cursor cursor = mSqLiteDatabase.query(DatabaseHelper.USERS_TABLE, new String[]{
                DatabaseHelper.USER_ID_COLUMN, DatabaseHelper.USER_IMAGE_COLUMN}
                , DatabaseHelper.USER_ID_COLUMN + " = " + userId, null, null, null, null) ;

        String result = null;

        if(cursor.getCount() != 0 ) {
            cursor.moveToFirst();
            result = cursor.getString(cursor.getColumnIndex(DatabaseHelper.USER_IMAGE_COLUMN));
        }

        return result;
    }

    public static void setUserImage(Activity activity, User user) {
        loadHelper(activity);

        ContentValues newValues = new ContentValues();

        newValues.put(DatabaseHelper.USER_ID_COLUMN, user.getId());
        newValues.put(DatabaseHelper.USER_IMAGE_COLUMN, user.getImage());

        long result = mSqLiteDatabase.insert(DatabaseHelper.USERS_TABLE, null, newValues);
        Log.d(TAG, "result = " + result);
    }
}
