package com.vic.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Vic on 8/9/2016.
 */
public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

    public static final String CREATE_PROVINCE =
            "create table Province ("
            +"id integer primary key autoincrement, "
            +"province_name text, "
            +"province_code text)";

    public static final String CREATE_CITY =
            "create table City ("
                    +"id integer primary key autoincrement, "
                    +"city_name text, "
                    +"city_code text,"
                    +"province_code text)";

    public static final String CREATE_COUNTY =
            "create table County ("
                    +"id integer primary key autoincrement, "
                    +"county_name text, "
                    +"country text, "
                    +"county_id text,"
                    +"lat text,"
                    +"lon text,"
                    +"countyCode text,"
                    +"cityCode text,"
                    +"provinceCode text,"
                    +"province text)";

    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
