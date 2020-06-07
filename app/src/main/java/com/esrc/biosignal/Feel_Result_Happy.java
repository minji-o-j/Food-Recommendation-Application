package com.esrc.biosignal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.esrc.biosignal.commonutils.CommonVariables;


public class Feel_Result_Happy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel__result__happy);
        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //결과 띄우기
        TextView showBPM2=(TextView) findViewById(R.id.bpm2);
        TextView showBPM1=(TextView) findViewById(R.id.bpm1);
        TextView LFperHF2=(TextView) findViewById(R.id.HRV2);
        TextView LFperHF1=(TextView) findViewById(R.id.HRV1);

        showBPM2.setText("중립 상태 :  "+ String.format("%.3f", CommonVariables.bpm2)+" BPM");
        showBPM1.setText("현재 상태 :  "+ String.format("%.3f",CommonVariables.bpm1)+" BPM");
        LFperHF2.setText("중립 상태 LF/HF :  "+ String.format("%.3f",CommonVariables.LF2/CommonVariables.HF2));
        LFperHF1.setText("현재 상태 LF/HF :  "+ String.format("%.3f",CommonVariables.LF1/CommonVariables.HF1));

    }


    //추천메뉴 보러가기
    public void showMenu(View v) {
        Intent showMenu = new Intent(Feel_Result_Happy.this, Feel_Happy.class);
        startActivity(showMenu);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }

    //화면 전환효과 없애기
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    //화면 종료효과 없애기
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


    //휴대폰 자체 BACK버튼, 감성 결과 확인 후 메인으로 돌아가는것임
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                //뒤로 가기 확인여부 묻는 창
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Closing Activity")
                .setMessage("음식를 주문하지 않고 메뉴로 돌아갈까요?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent home = new Intent(Feel_Result_Happy.this, SelectMode.class);
                        startActivity(home);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}