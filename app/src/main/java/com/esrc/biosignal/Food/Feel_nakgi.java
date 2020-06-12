package com.esrc.biosignal.Food;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.esrc.biosignal.Own_Menu;
import com.esrc.biosignal.Pay;
import com.esrc.biosignal.R;

public class Feel_nakgi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel_nakgi);
        //전체화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Button pay=(Button)findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        Pay.class);
                startActivity(intent);
            }
        });

        Button ok=(Button)findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent();
                finish();
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





//    //어플리케이션 BACK버튼
//    public void back_ch(View v) {
//        Intent back = new Intent(Feel_nakgi.this, Own_Menu.class);
//        startActivity(back);
//        finish();   //화면이 쌓이지 않고 액티비티 종료
//    }

    //휴대폰 자체 BACK버튼
    @Override
    public void onBackPressed() {
        finish();
    }
}