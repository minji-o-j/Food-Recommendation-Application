package com.esrc.biosignal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class Finish extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    //어플리케이션 BACK버튼
    public void main(View v) {
        Intent main = new Intent(Finish.this, SelectMode.class);
        startActivity(main);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }

    //휴대폰 자체 BACK버튼
    @Override
    public void onBackPressed() {
        Intent home = new Intent(Finish.this, SelectMode.class);
        startActivity(home);
        finish();
    }
}
