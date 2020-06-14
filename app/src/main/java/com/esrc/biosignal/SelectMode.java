//직접 선택 or 감성따라 선택
package com.esrc.biosignal;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


public class SelectMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_mode);

        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    //직점 메뉴 선택 버튼
    public void select_menu(View v) {
        Intent menu = new Intent(SelectMode.this, Own_Menu.class);
        //Intent menu = new Intent(SelectMode.this, Feel_Angry.class);
        startActivity(menu);
    }

    public void select_feeling(View v) {
        Intent feel = new Intent(SelectMode.this, Feel_Connect.class);
        //Intent feel = new Intent(SelectMode.this, Feel_Happy.class);
        startActivity(feel);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }

    public void testpage(View v) {
        Intent test = new Intent(SelectMode.this, Feel_Angry.class);
        startActivity(test);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }


    //화면 전환효과 없애기
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0,0);
    }

    //뒤로가기 두번이면 앱 종료, 여기만 필요
    private final long FINISH_INTERVAL_TIME = 1000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            ActivityCompat.finishAffinity(SelectMode.this);
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번더 뒤로가기를 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();

        }
    }



    // ############# Permission 관련 #############
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.ACCESS_COARSE_LOCATION"};
    String[] PERMISSIONS2 = {"android.permission.WRITE_EXTERNAL_STORAGE"};

    /**
     * Permission 허용 여부 확인 함수
     */
    private boolean hasPermissions(String[] permissions) {
        int result;

        for(String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);

            if(result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }

        return true;
    }

    /**
     * Permission 허용 요청 함수
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch(requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if(grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;

                    if (!cameraPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                }
                break;
        }
    }

    /**
     * Permission 다이어그램 호출 함수
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder( SelectMode.this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id){
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
    // ###########################################
}

