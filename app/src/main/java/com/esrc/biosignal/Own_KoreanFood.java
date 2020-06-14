package com.esrc.biosignal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;


public class Own_KoreanFood extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_own__korean_food);
        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button origogi=(Button)findViewById(R.id.origogi);
        origogi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        Pay.class);
                startActivity(intent);
            }
        });

        Button juk=(Button)findViewById(R.id.juk);
        juk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        Pay.class);
                startActivity(intent);
            }
        });

        Button jeyuk=(Button)findViewById(R.id.jeyuk);
        jeyuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        Pay.class);
                startActivity(intent);
            }
        });

        Button chicken=(Button)findViewById(R.id.chicken);
        chicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        Pay.class);
                startActivity(intent);
            }
        });

        Button dakbal=(Button)findViewById(R.id.dakbal);
        dakbal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        Pay.class);
                startActivity(intent);
            }
        });

        Button nakji=(Button)findViewById(R.id.nakji);
        nakji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        Pay.class);
                startActivity(intent);
            }
        });
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
    public void back_ko(View v) {
        Intent back = new Intent(Own_KoreanFood.this, Own_Menu.class);
        startActivity(back);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }

    //휴대폰 자체 BACK버튼
    @Override
    public void onBackPressed() {
        Intent home = new Intent(Own_KoreanFood.this, Own_Menu.class);
        startActivity(home);
    }



}