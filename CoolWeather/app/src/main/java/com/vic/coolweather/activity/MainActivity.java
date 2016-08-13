package com.vic.coolweather.activity;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.vic.coolweather.R;
import com.vic.coolweather.db.CoolWeatherDB;
import com.vic.coolweather.model.City;
import com.vic.coolweather.model.County;
import com.vic.coolweather.model.Province;
import com.vic.coolweather.util.HttpCallbackListener;
import com.vic.coolweather.util.HttpUtil;
import com.vic.coolweather.util.Utility;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LEVEL_PROVINCE = 0;
    private static final int LEVEL_CITY = 1;
    private static final int LEVEL_COUNTY = 2;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private Province selectedProvince;
    private City selectedCity;
    private int currentLevel;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private TextView refresh;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> datalist = new ArrayList<>();
    private CoolWeatherDB coolWeatherDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title);
        refresh = (TextView)findViewById(R.id.refresh);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(adapter);

        coolWeatherDB = CoolWeatherDB.getInstance(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities(selectedProvince);
                }else if(currentLevel == LEVEL_CITY){
                    selectedCity = cityList.get(position);
                    queryCounties(selectedCity);
                }
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryFromServer();
//                switch (currentLevel){
//                    case LEVEL_PROVINCE:
//
//                        break;
//                    case LEVEL_CITY:
//
//                        break;
//                    case LEVEL_COUNTY:
//
//                        break;
//                }
            }
        });
        queryProvinces();

    }

    private void queryProvinces() {
        provinceList = coolWeatherDB.loadProvinces();
        if(provinceList.size()>0){
            datalist.clear();
            for(Province province : provinceList){
                datalist.add(province.provinceName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("选择省份");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer();
        }
    }

    private void queryCities(Province province) {
        cityList = coolWeatherDB.loadCities(province);
        if(cityList.size()>0){
            datalist.clear();
            for(City city : cityList){
                datalist.add(city.cityName);
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("选择城市");
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer();
        }
    }

    private void queryCounties(City city) {
        countyList = coolWeatherDB.loadCounties(city);
        if(countyList.size()>0){
            String provName = countyList.get(0).prov;
            if(!(provName.equals("直辖市")||provName.equals("特别行政区"))){
                datalist.clear();
                for(County county : countyList){
                    datalist.add(county.city);
                }
                adapter.notifyDataSetChanged();
                listView.setSelection(0);
                titleText.setText("选择县级市");
                currentLevel = LEVEL_COUNTY;
            }
        }else {
            queryFromServer();
        }
    }


    private void queryFromServer() {
        String address = "https://api.heweather.com/x3/citylist?search=allchina&key=dfa0cfaede7d4a6fa2aaa64ea8f1e963";
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                closeProgressDialog();
                try {
                    if(Utility.handleCountiesResponse(coolWeatherDB,response)){
                        Utility.handleProvincesResponse(coolWeatherDB);
                        Utility.handleCitiesResponse(coolWeatherDB);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (currentLevel){
                                case LEVEL_COUNTY:
                                    queryCounties(selectedCity);
                                    break;
                                case LEVEL_PROVINCE:
                                    queryProvinces();
                                    break;
                                case LEVEL_CITY:
                                    queryCities(selectedProvince);
                                    break;
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("onError",e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this,"加载失败",Toast.LENGTH_SHORT)
                                .show();

                    }
                });
            }
        });

    }

    private void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel == LEVEL_COUNTY){
            queryCities(selectedProvince);
        }else if(currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
