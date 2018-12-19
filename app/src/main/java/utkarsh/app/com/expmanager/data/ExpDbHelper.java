package utkarsh.app.com.expmanager.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import utkarsh.app.com.expmanager.data.ExpContract.ExpEntry;

public class ExpDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "exp.db";
    public static final int DATABASE_VERSION = 1;

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + ExpEntry.TABLE_NAME +
            "(" + ExpEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ExpEntry.COLUMN_NAME + " TEXT NOT NULL," +
            ExpEntry.COLUMN_DETAILS + " TEXT, " +
            ExpEntry.COLUMN_AMOUNT + " INTEGER NOT NULL, " +
            ExpEntry.COLUMN_DISCOUNT + " INTEGER DEFAULT 0);";

    public ExpDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

