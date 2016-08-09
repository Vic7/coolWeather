package com.vic.coolweather.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.vic.coolweather.R;

public class MainActivity extends AppCompatActivity {
    private static int BACK_LEVEL = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        switch (--BACK_LEVEL){
            case 1:
            case 2:
                Toast.makeText(this,"再按："+BACK_LEVEL+"次退出程序",Toast.LENGTH_SHORT)
                        .show();
                break;
            case 0:
                BACK_LEVEL=3;
                finish();
                break;

        }

    }
}
