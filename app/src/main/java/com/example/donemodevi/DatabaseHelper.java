package com.example.donemodevi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String VERITABANI_ADI = "veritabani.db";
    private static final int SURUM = 1;

    private static final String TABLO_ADI = "bilgiler";
    private static final String SUTUN_ID = "id";
    private static final String SUTUN_BASLIK = "baslik";
    private static final String SUTUN_ACIKLAMA = "aciklama";

    public DatabaseHelper(Context context) {
        super(context, VERITABANI_ADI, null, SURUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tabloOlustur = "CREATE TABLE " + TABLO_ADI + " (" +
                SUTUN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SUTUN_BASLIK + " TEXT, " +
                SUTUN_ACIKLAMA + " TEXT)";
        db.execSQL(tabloOlustur);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_ADI);
        onCreate(db);
    }

    public boolean veriEkle(String baslik, String aciklama) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues veriler = new ContentValues();
        veriler.put(SUTUN_BASLIK, baslik);
        veriler.put(SUTUN_ACIKLAMA, aciklama);

        long sonuc = db.insert(TABLO_ADI, null, veriler);
        return sonuc != -1;
    }

    public Cursor veriGetir() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLO_ADI, null);
    }
}
