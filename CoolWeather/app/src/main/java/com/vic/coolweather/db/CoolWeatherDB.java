package com.vic.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vic.coolweather.model.City;
import com.vic.coolweather.model.County;
import com.vic.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vic on 8/9/2016.
 */
public class CoolWeatherDB {
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION = 1;

    private static CoolWeatherDB coolWeatherDB;
    private SQLiteDatabase db;

    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDB getInstance(Context context){
        if(coolWeatherDB == null)
            coolWeatherDB = new CoolWeatherDB(context);
        return coolWeatherDB;
    }

    public void saveProvinces(List<Province> provinces){
        if(provinces != null){
            ContentValues values = new ContentValues();
            db.beginTransaction();
            try{
                db.execSQL("drop table if exists Province");
                db.execSQL(CoolWeatherOpenHelper.CREATE_PROVINCE);
                for(Province province : provinces){
                    values.put("province_name",province.provinceName);
                    values.put("province_code",province.provinceCode);
                    db.insert("Province",null,values);
                    values.clear();
                }
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
        }
    }

    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("Province",null,null,null,null,null,"province_code");
        if(cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.provinceName = cursor.getString(cursor.getColumnIndex("province_name"));
                province.provinceCode = cursor.getString(cursor.getColumnIndex("province_code"));
                list.add(province);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void saveCities(List<City> cities){
        if(cities != null){
            ContentValues values = new ContentValues();
            db.beginTransaction();
            try{
                db.execSQL("drop table if exists City");
                db.execSQL(CoolWeatherOpenHelper.CREATE_CITY);
                for(City city : cities){
                    values.put("city_name",city.cityName);
                    values.put("city_code",city.cityCode);
                    values.put("province_code",city.provinceCode);
                    db.insert("City",null,values);
                    values.clear();
                }
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }
        }
    }

    public List<City> loadCities(Province selectedProvince){
        List<City> list = new ArrayList<>();
        Cursor cursor;
        if(selectedProvince != null){
            cursor = db.query("City",null,
                    "province_code = ?",
                    new String[]{selectedProvince.provinceCode},
                    null,null,null);
        }else {
            cursor = db.query("City",null, null,null, null,null,null);
        }
        if(cursor.moveToFirst()){
            do{
                City city = new City();
                city.cityName = cursor.getString(cursor.getColumnIndex("city_name"));
                city.cityCode = cursor.getString(cursor.getColumnIndex("city_code"));
                city.provinceCode = cursor.getString(cursor.getColumnIndex("province_code"));
                Log.d("City",city.cityName);
                list.add(city);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void saveCounties(List<County> counties){
        if(counties != null){
            ContentValues values = new ContentValues();
            String temp;
            db.beginTransaction();
            try{
                db.execSQL("drop table if exists County");
                db.execSQL(CoolWeatherOpenHelper.CREATE_COUNTY);
                for(County county:counties){
                    temp = county.id.substring(5,7);
                    values.put("provinceCode",temp);
                    temp = county.id.substring(7,9);
                    values.put("cityCode",temp);
                    temp = county.id.substring(9,11);
                    values.put("countyCode",temp);
                    values.put("county_name",county.city);
                    values.put("county_id",county.id);
                    values.put("country",county.cnty);
                    values.put("lat",county.lat);
                    values.put("lon",county.lon);
                    values.put("province",county.prov);
                    db.insert("County",null,values);
                    values.clear();
                }
                db.setTransactionSuccessful();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                db.endTransaction();
            }

        }
    }

    public List<County> loadCounties(City selectedCity){
        List<County> list = new ArrayList<>();
        Cursor cursor;
        if(selectedCity != null){
            cursor = db.query("County",null,
                    "cityCode = ? and provinceCode = ?",
                    new String[]{selectedCity.cityCode,selectedCity.provinceCode},
                    null,null,null);
//            cursor = db.rawQuery("select * from County where cityCode=? and provinceCode=?",
//                    new String[]{selectedCity.cityCode,selectedCity.provinceCode});
        }
        else {
            cursor = db.query("County",null, null,null, null,null,null);
        }
        if(cursor.moveToFirst()){
            do{
                County county = new County();
                county.city = cursor.getString(cursor.getColumnIndex("county_name"));
                county.prov = cursor.getString(cursor.getColumnIndex("province"));
                county.countyCode = cursor.getString(cursor.getColumnIndex("countyCode"));
                county.cityCode = cursor.getString(cursor.getColumnIndex("cityCode"));
                county.provinceCode = cursor.getString(cursor.getColumnIndex("provinceCode"));
                list.add(county);
            }while (cursor.moveToNext());
        }
        if(cursor != null){
            cursor.close();
        }
        return list;
    }
}
