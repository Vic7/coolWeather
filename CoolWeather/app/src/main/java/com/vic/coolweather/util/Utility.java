package com.vic.coolweather.util;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vic.coolweather.db.CoolWeatherDB;
import com.vic.coolweather.model.City;
import com.vic.coolweather.model.County;
import com.vic.coolweather.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vic on 8/9/2016.
 */
public class Utility {

    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB){
        List<County> countyList = coolWeatherDB.loadCounties(null);
        if(countyList != null && countyList.size()>0){
            List<Province> provinces = new ArrayList<>();
            for(County c:countyList){
                if(c.cityCode.equals("01") && (c.countyCode.equals("01")||c.countyCode.equals("00"))){
                    Province province = new Province();
                    if(c.prov.equals("特别行政区")||c.prov.equals("直辖市")){
                        province.provinceName = c.city;
                    }else{
                        province.provinceName = c.prov;
                    }

                    province.provinceCode = c.provinceCode;
                    provinces.add(province);
                }
            }
            coolWeatherDB.saveProvinces(provinces);
        }
        return true;
    }

    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB){
        List<County> countyList = coolWeatherDB.loadCounties(null);
        if(countyList != null && countyList.size()>0){
            List<City> cityList = new ArrayList<>();
            for(County c:countyList){
                if(c.countyCode.equals("01") || c.countyCode.equals("00")){
                    City city = new City();
                    city.cityName = c.city;
                    city.cityCode = c.cityCode;
                    city.provinceCode = c.provinceCode;
                    cityList.add(city);
                }
            }
            coolWeatherDB.saveCities(cityList);
        }
        return true;
    }


    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,
                                                               String response){
        try {
            JSONObject object = new JSONObject(response);
            JSONArray county_infos = object.getJSONArray("city_info");
            Gson gson = new Gson();
            List<County> counties = gson.fromJson(county_infos.toString(),new TypeToken<List<County>>(){}.getType());
            coolWeatherDB.saveCounties(counties);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }
}
