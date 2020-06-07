package com.esrc.biosignal;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.esrc.biosignal.Food.Feel_chococake;
import com.esrc.biosignal.Food.Feel_curry;
import com.esrc.biosignal.Food.Feel_hodupie;
import com.esrc.biosignal.Food.Feel_mapatofu;
import com.esrc.biosignal.Food.Feel_mushroom;
import com.esrc.biosignal.Food.Feel_oister;
import com.esrc.biosignal.Food.Feel_origogi;
import com.esrc.biosignal.Food.Feel_tomato;
import com.esrc.biosignal.Food.Feel_yeoneo;

import java.util.Random;
public class Feel_Sad extends AppCompatActivity {
    int[] simg={R.drawable.c_jajang, R.drawable.c_mapa, R.drawable.k_origogi, R.drawable.k_juk,
            R.drawable.j_sake, R.drawable.j_curry, R.drawable.w_oyster, R.drawable.w_pasta,
            R.drawable.e_hodu, R.drawable.e_choco};
    //슬픔 에서 이미지를 넣은 배열

    Class[] sView={com.esrc.biosignal.Food.Feel_jjajang.class, Feel_mapatofu.class, Feel_origogi.class, Feel_mushroom.class,
    Feel_yeoneo.class, Feel_curry.class, Feel_oister.class, Feel_tomato.class,
    Feel_hodupie.class, Feel_chococake.class};
    //이미지의 인덱스에 맞게 class를 넣은 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel__sad);

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

        image1.setImageResource(simg[num[0]]);
        image2.setImageResource(simg[num[1]]);
        image3.setImageResource(simg[num[2]]);

        Button button2=(Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (sView[num[0]]));
                startActivity(intent);
            }
        });
        Button button3=(Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (sView[num[1]]));
                startActivity(intent);
            }
        });
        Button button4=(Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (sView[num[2]]));
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
        Intent home = new Intent(Feel_Sad.this, Feel_Result_Sad.class);
        startActivity(home);
    }
}