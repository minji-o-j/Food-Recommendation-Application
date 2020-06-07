package com.esrc.biosignal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esrc.biosignal.graphutils.LineChartGraph;
import com.esrc.biosignal.libs.BiosignalConsumer;
import com.esrc.biosignal.libs.BiosignalManager;
import com.esrc.biosignal.libs.SignalNotifier;
import com.esrc.biosignal.libs.StateNotifier;

public class Feel_Connect extends Activity implements BiosignalConsumer {
    private static final String TAG = "Feel_Connect";
    private final Activity act = this;
    private boolean PPGstate=false ;    //연결상태 확인

    // PPG 장비 관련 변수
    public BiosignalManager mBIosignalManager = null;  // PPG 장비 관리 객체

    // 인터페이스 관련 변수
    private Button mConnectBtn;  // 장비 연결 버튼 레이아웃

    /*
     * 앱 시작 시에 실행되는 함수
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel__connect);

        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 인터페이스 초기화
        initialize();

        // Permission 요청
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    /*
     * 앱 종료 시에 실행되는 함수
     */
    @Override
    public void onDestroy() {
        // 앱 종료 시 PPG 장비 연결 해제
        unbind();
        super.onDestroy();
        Toast.makeText(this,"감성 측정을 종료합니다",Toast.LENGTH_SHORT).show();   //2초정도
    }

    /*
     * 인터페이스 초기화 관련 함수
     */
    private void initialize() {
        // 인터페이스 초기화
        mConnectBtn = (Button) findViewById(R.id.connect_btn);
        mConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newIntent = new Intent(act, com.esrc.biosignal.client.DeviceListActivity.class);
                startActivityForResult(newIntent, 0);
            }
        });
    }



    //////////////////////////////////////////////////////// 이하는 수정 X ////////////////////////////////////////////////////////

    // ############# PPG 장비 연결 관련 #############
    /*
     * PPG 장비 연결을 위한 콜백 함수
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                // When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    BluetoothDevice mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);

                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
                    pref.edit().putString("biosignal", mDevice.getAddress()).apply();
                  //  Toast.makeText(this, "Save address of biosignal : " + mDevice.getAddress(), Toast.LENGTH_SHORT).show();
                    PPGstate=true;
                    Toast.makeText(this, "장비가 연결되었습니다", Toast.LENGTH_SHORT).show(); //2초인듯?
                }
                break;
            default:
                break;
        }
    }

    /**
     * PPG 장비 관리 객체 설정 함수
     */
    private void bind() {
        onBindBiosignalService(true);
    }

    /**
     * PPG 장비 관리 객체 해제 함수
     */
    private void unbind() {
        PPGstate=false;
        onBindBiosignalService(false);
    }

    /**
     * PPG 장비 관리 객체 연결 콜백 함수
     */
    private void onBindBiosignalService(boolean bind) {
        if(bind) {
            if(mBIosignalManager == null) mBIosignalManager = BiosignalManager.getInstanceForApplication(this);
            mBIosignalManager.bind(this);
        } else {
            if(mBIosignalManager == null) return;
            try {
                mBIosignalManager.stopSignaling(0);
                mBIosignalManager.disconnect(0);
                mBIosignalManager.unBind(this);
                mBIosignalManager = null;
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * PPG 장비 관리 객체 콜백 함수
     */
    @Override
    public void onBiosignalServiceConnect() {
        mBIosignalManager.setStateNotifier(new StateNotifier() {
            @Override
            public void didChangedState(int state) {
                if (state == BiosignalManager.STATE_CONNECTED) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            try {
                                if (mBIosignalManager != null) {
                                    mBIosignalManager.startSignaling(0);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }, 5000);
                }
            }
        });

        onConnectSociaLBand();
    }

    /**
     * PPG 장비 연결 함수
     */
    private void onConnectSociaLBand() {
        String address = PreferenceManager.getDefaultSharedPreferences(this).getString("biosignal", null);
        if(address == null) return;
        try {
            if(mBIosignalManager != null) {
                Log.d(TAG, "onConnectBiosignal : " + address);
                mBIosignalManager.connect(0, address);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
    // ###########################################

    // ############# Permission 관련 #############
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.ACCESS_COARSE_LOCATION"};

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
        AlertDialog.Builder builder = new AlertDialog.Builder( Feel_Connect.this);
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
        Intent back = new Intent(Feel_Connect.this, SelectMode.class);
        onDestroy();//연결종료
        startActivity(back);
        finish();   //화면이 쌓이지 않고 액티비티 종료
    }

    //휴대폰 자체 BACK버튼
    @Override
    public void onBackPressed() {
        Intent home = new Intent(Feel_Connect.this, SelectMode.class);
        onDestroy();//연결종료
        startActivity(home);
    }

    //측정시작
    public void start(View v) {
        if(PPGstate==true) {
            Intent start = new Intent(Feel_Connect.this, Feel_Start1.class);
            startActivity(start);
        }
        else{
            Toast.makeText(this, "장비를 연결해주세요", Toast.LENGTH_SHORT).show();
        }
    }
    // ###########################################
}

