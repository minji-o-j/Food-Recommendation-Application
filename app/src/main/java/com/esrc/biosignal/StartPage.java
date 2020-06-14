//시작할때 타이틀 화면
package com.esrc.biosignal;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

public class StartPage extends Activity{
    Handler handler = new Handler();

    //2초후에 넘길수있도록 할 예정
    Runnable r = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), SelectMode.class);
            startActivity(intent);
            finish(); // 화면 종료를 위해
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    @Override
    protected void onResume() {
        super.onResume();
    // 다시 화면에 들어어왔을 때 예약 걸어주기
        handler.postDelayed(r, 2000); // 2초 뒤에 Runnable 객체 수행
    }

    @Override
    protected void onPause() {
        super.onPause();
    // 화면을 벗어나면, handler 에 예약해놓은 작업을 취소하자
        handler.removeCallbacks(r); // 예약 취소
    }


    //화면 전환효과 없애기
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0,0);
    }


    //뒤로가기 두번이면 앱 종료
    private final long FINISH_INTERVAL_TIME = 1000;//1초안에 2번
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            ActivityCompat.finishAffinity(StartPage.this);
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 뒤로가기를 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();

        }
    }
}
