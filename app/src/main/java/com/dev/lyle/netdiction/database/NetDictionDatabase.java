package com.dev.lyle.netdiction.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Lyle on 6/27/2016.
 */
public class NetDictionDatabase extends SQLiteOpenHelper {

    public NetDictionDatabase(Context context) {
        super(context, "netdiction.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE known_computers(id INTEGER PRIMARY KEY, name TEXT, address TEXT," +
                "port INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS known_computers");
        onCreate(db);
    }

    public ArrayList<Computer> getAllKnownComputers() {
        ArrayList<Computer> computers = new ArrayList<Computer>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor rs = db.rawQuery("SELECT * FROM known_computers", null);
        rs.moveToFirst();
        while (!rs.isAfterLast()) {
            computers.add(new Computer(rs.getString(rs.getColumnIndex("name")),
                    rs.getString(rs.getColumnIndex("address")),
                    rs.getInt(rs.getColumnIndex("port"))));
            rs.moveToNext();
        }
        rs.close();
        return computers;
    }

    public void addComputer(String name, String address, int port) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("address", address);
        values.put("port", port);
        db.insert("known_computers", null, values);
    }

    public boolean isKnownComputer(String name, String address, int port) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor rs = db.rawQuery("SELECT * FROM known_computers WHERE " +
                "name = \"" + name + "\" AND " +
                "address = \"" + address + "\" AND " +
                "port = " + port, null);
        return rs.getCount() > 0;
    }
}
