package com.esrc.biosignal;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class Pay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


    }

    //현금&카드
    public void cash(View v) {
        Intent cash = new Intent(Pay.this, Finish.class);
        startActivity(cash);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }

    public void credit(View v) {
        Intent credit = new Intent(Pay.this, Finish.class);
        startActivity(credit);
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

    //어플리케이션 BACK버튼
    public void back(View v) {
        new AlertDialog.Builder(this)
                //뒤로 가기 확인여부 묻는 창
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Closing Activity")
                .setMessage("결제를 하지 않고 메뉴로 돌아갈까요?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent home = new Intent(Pay.this, Own_Menu.class);
                        startActivity(home);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }


    //휴대폰 자체 BACK버튼
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                //뒤로 가기 확인여부 묻는 창
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("결제 취소")
                .setMessage("결제를 하지 않고 메뉴로 돌아갈까요?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent home = new Intent(Pay.this, Own_Menu.class);
                        startActivity(home);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
