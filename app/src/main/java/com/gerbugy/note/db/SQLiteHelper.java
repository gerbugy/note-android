package com.gerbugy.note.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static SQLiteHelper sInstance;

    private SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static void initialize(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        sInstance = new SQLiteHelper(context, name, factory, version);
    }

    public static SQLiteHelper getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createMemo(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void createMemo(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS memo (_id INTEGER PRIMARY KEY AUTOINCREMENT, content TEXT NOT NULL, important INTEGER NOT NULL DEFAULT 0, position INTEGER NOT NULL DEFAULT 0, created_at INTEGER NOT NULL DEFAULT 0, updated_at INTEGER NOT NULL DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.beginTransaction();
        try {
            switch (newVersion) {
                case 18030604:
                    upgrade_18030604(db);
                case 18030605:
                    upgrade_18030605(db);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void upgrade_18030604(SQLiteDatabase db) {

    }

    private void upgrade_18030605(SQLiteDatabase db) {

    }
}
