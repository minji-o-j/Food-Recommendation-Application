package com.esrc.biosignal;

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
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esrc.biosignal.FFT.Complex;
import com.esrc.biosignal.FFT.FastFourierTransform;
import com.esrc.biosignal.commonutils.CommonVariables;
import com.esrc.biosignal.graphutils.LineChartGraph;
import com.esrc.biosignal.libs.BiosignalConsumer;
import com.esrc.biosignal.libs.BiosignalManager;
import com.esrc.biosignal.libs.SignalNotifier;
import com.esrc.biosignal.libs.StateNotifier;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Feel_Start1 extends Activity implements BiosignalConsumer {
    private static final String TAG = "Feel_Start1";
    private final Activity act = this;

    // PPG 장비 관련 변수
    private BiosignalManager mBIosignalManager = null;  // PPG 장비 관리 객체

    // 인터페이스 관련 변수
    private LineChartGraph mPPGGraph;  // PPG 그래프 레이아웃
    private TextView mBpmTv;  // BPM 텍스트 레이아웃
    private Button mStopBtn;  // 종료 버튼 레이아웃

    //파일 작성 관련
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
    //final static String file_ppg = "ppg1.txt";
//    final static String file_bpm = "bpm1.txt";
    //final static String file_ppi  ="ppi1.txt";
//    final static String file_LF="LF1.txt";
//    final static String file_HF="HF1.txt";
    final static String file_FFT="FFT1.txt";    //실험 데이터 받는 파일


    //FFT 관련
    private int countPPI=0;
    private double[] ppiList1=new double[32];    //0~31까지의 index, ppi 정보 들어있는 배열
    private double[] FFTList1=new double[32];   //FFT한 결과 받아옴
    //private double[] useFFT1=new double[16];     //FFT한 결과에서 0(DC)를 제외하고 1~16까지의 값을 저장할 예정

    //값 처리 변수
    private double[] BPMList1=new double[32];   //BPM 평균 계산 위해 BPM 저장
    private double BPM_sum;
    private double BPM_average;
    private double LF;
    private double HF;


    //다음 페이지로 넘어가게끔 함
    Handler handler = new Handler();
    Runnable r = new Runnable() {
        @Override
        public void run() {
            //데이터를 저장
            Data_Cal();
            Intent intent = new Intent(getApplicationContext(), Feel_Start2.class);
            startActivity(intent); // 다음화면으로 넘어가기
            unbind(); // 해제
        }
    };

    //데이터 처리
    public void Data_Cal(){
        //FFT 결과값 저장
        for(int i=0;i<32;i++) {
            FFTList1[i]=FFT_Cal(ppiList1)[i];   //FFT 계산
            BPM_sum+=BPMList1[i];   //평균 계산 위해 bpm 값 더함
            String contents2=FFTList1[i]+"\n";
            WriteTextFile(foldername,file_FFT,contents2);
        }
        BPM_average=BPM_sum/32;     //BPM 평균 구함

        CommonVariables.bpm1 = BPM_average;

        /**LF 계산*/
        //LF영역 2~4번
        for(int j=2;j<=4;j++){
            LF+=FFTList1[j];
        }

        /**HF 계산*/
        //HF영역 5~12번
        for(int k=5;k<=12;k++){
            HF+=FFTList1[k];
        }
        CommonVariables.LF1=LF;
        CommonVariables.HF1=HF;

    }

    //FFT 계산하는 함수
    public static double[] FFT_Cal(double[] input){
        FastFourierTransform FastFT=new FastFourierTransform();
        Complex[] cinput = new Complex[input.length]; //make complex arr
        for (int i = 0; i < input.length; i++)
            cinput[i] = new Complex(input[i], 0.0);//input re: input[i], im: 0,0,0...-->real!

        FastFT.fft(cinput);

//        System.out.println("Results:");
//        for (Complex c : cinput) {
//            System.out.println(c);
//        }

        double[] power_spectrum=new double[input.length];

        for(int i=0;i<input.length;i++){
            power_spectrum[i]=((cinput[i].re*cinput[i].re)+(cinput[i].im*cinput[i].im))/(input.length*input.length)*2;
           // System.out.printf("%.9f\n",power_spectrum[i]);
        }

        return power_spectrum;
    }


    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 다시 화면에 들어어왔을 때 예약 걸어주기
        handler.postDelayed(r, 47000); // 47초 뒤에 Runnable 객체 수행, 32초동안 PPI 측정

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 화면을 벗어나면, handler 에 예약해놓은 작업을 취소
        handler.removeCallbacks(r); // 예약 취소

    }


    /**
     * 앱 시작 시에 실행되는 함수
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel__start1);



        //시작 로그

        //Log.d(TAG,"START TIME*************************\n"+formatDate);
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

        bind();
        Toast.makeText(act, "측정이 시작되었습니다.", Toast.LENGTH_SHORT).show();
    }

    /**
     * 앱 종료 시에 실행되는 함수
     */
    @Override
    public void onDestroy() {
        // 앱 종료 시 PPG 장비 연결 해제
        unbind();
        super.onDestroy();
        Toast.makeText(this,"감성 측정을 종료합니다",Toast.LENGTH_SHORT).show();   //2초정도

    }

    /**
     * 인터페이스 초기화 관련 함수
     */
    private void initialize() {
        // 인터페이스 초기화
        mPPGGraph = new LineChartGraph(this, (RelativeLayout) findViewById(R.id.ppg_view), 300, 2);
        mBpmTv = (TextView) findViewById(R.id.bpm_tv);

        mStopBtn = (Button) findViewById(R.id.stop_btn);


        //취소 버튼 누르면 메인으로 돌아감
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Feel_Start1.this)
                        //뒤로 가기 확인여부 묻는 창
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Closing Activity")
                        .setMessage("감성 측정을 중단하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent home = new Intent(Feel_Start1.this, SelectMode.class);
                                onDestroy();//연결종료
                                handler.removeCallbacks(r); // 예약 취소
                                startActivity(home);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }

        });
    }

    /**
     * PPG 신호 콜백 함수
     */
    private void onCallbackReceivedPPG(int ppg) {
        mPPGGraph.add(ppg);
    }

    /**
     * BPM 콜백 함수
     */
    private void onCallbackReceivedBPM(double bpm) {
        mBpmTv.setText("HR = " + Long.toString(Math.round(bpm)));

        //FFT위해 배열에 저장
        if(countPPI<32) {
            BPMList1[countPPI]=bpm;
            ppiList1[countPPI] = (60 / bpm);   //0~34까지의 index에 ppi값 저장
            //Log.d(TAG, "\nindex"+countPPI+" : " + ppiList1[countPPI]); //확인위해 Log찍어봄
            countPPI++;
        }

    }


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
                    Toast.makeText(this, "Save address of biosignal : " + mDevice.getAddress(), Toast.LENGTH_SHORT).show();
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

        mBIosignalManager.setSignalNotifier(new SignalNotifier() {
            @Override
            public void onReceivedPPG(int ppg) {
                onCallbackReceivedPPG(ppg);
            }


            @Override
            public void onReceivedBPM(double bpm) {
                Log.d(TAG, "onReceivedBPM : " + bpm);
                onCallbackReceivedBPM(bpm);
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
        AlertDialog.Builder builder = new AlertDialog.Builder( Feel_Start1.this);
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

    //휴대폰 자체 BACK버튼, 아예 선택모드로 돌아감

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                //뒤로 가기 확인여부 묻는 창
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("Closing Activity")
                .setMessage("감성 측정을 중단하시겠습니까?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent home = new Intent(Feel_Start1.this, SelectMode.class);
                        onDestroy();//
                        handler.removeCallbacks(r); // 예약 취소
                        startActivity(home);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}