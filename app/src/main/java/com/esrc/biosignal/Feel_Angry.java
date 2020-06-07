package com.esrc.biosignal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.esrc.biosignal.Food.Feel_applepie;
import com.esrc.biosignal.Food.Feel_buldakpizza;
import com.esrc.biosignal.Food.Feel_chickenfeet;
import com.esrc.biosignal.Food.Feel_jjambbong;
import com.esrc.biosignal.Food.Feel_karaimen;
import com.esrc.biosignal.Food.Feel_maratang;
import com.esrc.biosignal.Food.Feel_mentaiko;
import com.esrc.biosignal.Food.Feel_nakgi;
import com.esrc.biosignal.Food.Feel_spicycreampasta;
import com.esrc.biosignal.Food.Feel_ttokboki;

public class Feel_Angry extends AppCompatActivity{

        int[] mimg={R.drawable.c_jjam,R.drawable.c_mara,R.drawable.k_dakbal,R.drawable.k_nakji,
            R.drawable.j_menta,R.drawable.j_ramen,R.drawable.w_pizza,R.drawable.w_spasta,
            R.drawable.e_dduk,R.drawable.e_apple};
        //화남에서 이미지 를 넣은 배열

        Class[] mView={com.esrc.biosignal.Food.Feel_jjambbong.class, com.esrc.biosignal.Food.Feel_maratang.class, com.esrc.biosignal.Food.Feel_chickenfeet.class,
                com.esrc.biosignal.Food.Feel_nakgi.class, com.esrc.biosignal.Food.Feel_mentaiko.class, com.esrc.biosignal.Food.Feel_karaimen.class,
                com.esrc.biosignal.Food.Feel_buldakpizza.class, com.esrc.biosignal.Food.Feel_spicycreampasta.class,
                com.esrc.biosignal.Food.Feel_ttokboki.class, com.esrc.biosignal.Food.Feel_applepie.class};
        //이미지의 인덱스와 맞게 class를 넣은 배열


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel__angry);

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
        ImageView image1=(ImageView)findViewById(R.id.layout1); //이미지를 띄우는 소스
        ImageView image2=(ImageView)findViewById(R.id.layout2);
        ImageView image3=(ImageView)findViewById(R.id.layout3);

        image1.setImageResource(mimg[num[0]]);
        image2.setImageResource(mimg[num[1]]);
        image3.setImageResource(mimg[num[2]]);

        Button button2=(Button)findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (mView[num[0]]));
                startActivity(intent);
            }
        });
        Button button3=(Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (mView[num[1]]));
                startActivity(intent);
            }
        });
        Button button4=(Button)findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent=new Intent(
                        getApplicationContext(),
                        (mView[num[2]]));
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
        Intent home = new Intent(Feel_Angry.this, Feel_Result_Angry.class);
        startActivity(home);

    }
}