//직접 메뉴 선택시 한중일 인기메뉴 등...
package com.esrc.biosignal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class Own_Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own__menu);
        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    //인기메뉴
    public void Popular(View v) {
        Intent popular = new Intent(Own_Menu.this, Own_Popular.class);
        startActivity(popular);
    }

    //한식
    public void Korean(View v) {
        Intent korean = new Intent(Own_Menu.this, Own_KoreanFood.class);
        startActivity(korean);
    }

    //중식
    public void Chinese(View v) {
        Intent Chinese = new Intent(Own_Menu.this, Own_ChineseFood.class);
        startActivity(Chinese);
    }

    //일식
    public void Japanese(View v) {
        Intent Japanese = new Intent(Own_Menu.this, Own_JapaneseFood.class);
        startActivity(Japanese);
    }

    //양식
    public void Western(View v) {
        Intent Western = new Intent(Own_Menu.this, Own_WesternFood.class);
        startActivity(Western);
    }

    //기타
    public void ETC(View v) {
        Intent ETC = new Intent(Own_Menu.this, Own_Etc.class);
        startActivity(ETC);
    }

    //어플리케이션 BACK버튼
    public void back(View v) {
        Intent back = new Intent(Own_Menu.this, SelectMode.class);
        startActivity(back);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }

    //화면 전환효과 없애기
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0,0);
    }

    //화면 종료효과 없애기
    @Override
    public void onPause(){
        super.onPause();
        overridePendingTransition(0,0);
    }

    //휴대폰 자체 BACK버튼
    @Override
    public void onBackPressed() {
        Intent home = new Intent(Own_Menu.this, SelectMode.class);
        startActivity(home);
    }
}
