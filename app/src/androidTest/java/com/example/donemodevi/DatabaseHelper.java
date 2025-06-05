package com.example.donemodevi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "notlar.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "notlar";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "baslik";
    private static final String COLUMN_DESCRIPTION = "aciklama";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Veritabanı ilk oluşturulunca çağrılır
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT)";
        db.execSQL(createTable);
    }

    // Veritabanı güncellenince çağrılır
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Veriyi ekleme
    public boolean veriEkle(String baslik, String aciklama) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, baslik);
        values.put(COLUMN_DESCRIPTION, aciklama);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1;
    }

    // Tüm veriyi alma
    public String verileriGetir() {
        StringBuilder data = new StringBuilder();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        while (cursor.moveToNext()) {
            data.append("Başlık: ").append(cursor.getString(1)).append("\n");
            data.append("Açıklama: ").append(cursor.getString(2)).append("\n\n");
        }

        cursor.close();
        db.close();
        return data.toString();
    }
}
