package com.esrc.biosignal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.esrc.biosignal.Food.Feel_bbq;
import com.esrc.biosignal.Food.Feel_beefsushi;
import com.esrc.biosignal.Food.Feel_cheesecake;
import com.esrc.biosignal.Food.Feel_chicken;
import com.esrc.biosignal.Food.Feel_donkas;
import com.esrc.biosignal.Food.Feel_jeyuk;
import com.esrc.biosignal.Food.Feel_kkanpungi;
import com.esrc.biosignal.Food.Feel_macaron;
import com.esrc.biosignal.Food.Feel_steak;

public class Feel_Happy extends AppCompatActivity {
    int[] himg={R.drawable.c_tang,R.drawable.c_ggan,R.drawable.k_jeyuk,R.drawable.k_chicken,
            R.drawable.j_katz,R.drawable.j_sushi,R.drawable.w_bar,R.drawable.w_steak,
            R.drawable.e_maca,R.drawable.e_cheese};
    //행복 에서 이미지를 넣은 배열

    Class[] hView={com.esrc.biosignal.Food.Feel_tangsuyuk.class, Feel_kkanpungi.class, Feel_jeyuk.class, Feel_chicken.class,
    Feel_donkas.class, Feel_beefsushi.class, Feel_bbq.class, Feel_steak.class,
    Feel_macaron.class, Feel_cheesecake.class};
    //이미지의 인덱스에 맞게 class를 넣은 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel__happy);

        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        final int num[]=new int[3];
        for(int i=0;i<num.length;i++){
            num[i]=(int)(Math.random()*10);
            for(int j=0;j<i;j++){
                if(num[i]==num[j]){
                    i--;
                }
            }
        }
        ImageView image1=(ImageView)findViewById(R.id.layout1);
        ImageView image2=(ImageView)findViewById(R.id.layout2);
        ImageView image3=(ImageView)findViewById(R.id.layout3);

        image1.setImageResource(himg[num[0]]);
        image2.setImageResource(himg[num[1]]);
        image3.setImageResource(himg[num[2]]);

        Button button2=(Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (hView[num[0]]));
                startActivity(intent);
            }
        });
        Button button3=(Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (hView[num[1]]));
                startActivity(intent);
            }
        });
        Button button4=(Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (hView[num[2]]));
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

    //휴대폰 자체 BACK버튼
    @Override
    public void onBackPressed() {
        Intent home = new Intent(Feel_Happy.this, Feel_Result_Happy.class);
        startActivity(home);
    }
}