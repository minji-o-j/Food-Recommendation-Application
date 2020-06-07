package com.esrc.biosignal;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;
import android.widget.MediaController;

import com.esrc.biosignal.FFT.Complex;
import com.esrc.biosignal.FFT.FastFourierTransform;
import com.esrc.biosignal.commonutils.CommonVariables;
import com.esrc.biosignal.libs.BiosignalConsumer;
import com.esrc.biosignal.libs.BiosignalManager;
import com.esrc.biosignal.libs.SignalNotifier;
import com.esrc.biosignal.libs.StateNotifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Feel_Start2 extends Activity implements BiosignalConsumer {
    private static final String TAG = "Feel_Start2";
    private final Activity act = this;

    // PPG 장비 관련 변수
    private BiosignalManager mBIosignalManager = null;  // PPG 장비 관리 객체

    // 인터페이스 관련 변수
    private Button mStopBtn;  // 종료 버튼 레이아웃


    //파일 작성 관련
    final static String foldername = Environment.getExternalStorageDirectory().getAbsolutePath()+"/TestLog";
    //final static String file_ppg = "ppg2.txt";
    //final static String file_bpm = "bpm2.txt";
    //final static String file_ppi  ="ppi2.txt";
//    final static String file_LF="LF2.txt";
//    final static String file_HF="HF2.txt";
    final static String file_FFT="FFT2.txt";    //실험 데이터 받는 파일

    //FFT 관련
    private int countPPI=0;
    private double[] ppiList2=new double[32];    //0~31까지의 index, ppi 정보 들어있는 배열
    private double[] FFTList2=new double[32];   //FFT한 결과 받아옴

    //값 처리 변수
    private double[] BPMList2=new double[32];   //BPM 평균 계산 위해 BPM 저장
    private double BPM_sum;
    private double BPM_average;
    private double LF;
    private double HF;
    private double LFperHF1;//감정 관련 상태
    private double LFperHF2;//안정 상태일때


    //상태
    private int state=3;

    //다음 페이지로 넘어가게끔 함
    Handler handler2 = new Handler();
    Runnable r2 = new Runnable() {
        @Override
        public void run() {
            //데이터를 저장
            Data_Cal();
            State();
            //감정 상태 판별하여 창 전환

            /**angry*/
            if(state==1) {
                Intent angry = new Intent(getApplicationContext(), Feel_Result_Angry.class);
                startActivity(angry); // 다음화면으로 넘어가기
            }

            /**sad*/
            else if(state==2) {
                Intent sad = new Intent(getApplicationContext(), Feel_Result_Sad.class);
                startActivity(sad); // 다음화면으로 넘어가기
            }


            /**happy*/
            else { //if(state==3) {
                Intent happy = new Intent(getApplicationContext(), Feel_Result_Happy.class);
                startActivity(happy); // 다음화면으로 넘어가기
            }

            finish(); // Activity 화면 제거
        }
    };

    //상태 결정
    public void State(){

        //LF/HF 구하기
        LFperHF1=CommonVariables.LF1/CommonVariables.HF1;
        LFperHF2=CommonVariables.LF2/CommonVariables.HF2;

        /**angry
         * LF/HF가 진정보다 작고, BPM이 진정보다 더 높음*/
        if(LFperHF1<=LFperHF2 && CommonVariables.bpm1>=CommonVariables.bpm2){
            state=1;
        }
        /**
         * LF/HF가 진정보다 작고 BPM이 진정보다 더 낮음sad*/
        else if(LFperHF1<=LFperHF2 && CommonVariables.bpm1<=CommonVariables.bpm2){
            state=2;
        }
        /**
         * LF/HF가 진정보다 큼happy*/
        else{       //(LFperHF1<=LFperHF2)
            state=3;
        }
    }

    //데이터 처리
    public void Data_Cal(){
        //FFT 결과값 저장
        for(int i=0;i<32;i++) {
            FFTList2[i]=FFT_Cal(ppiList2)[i];   //FFT 계산
            String contents2=FFTList2[i]+"\n";
            WriteTextFile(foldername,file_FFT,contents2);
            BPM_sum+=BPMList2[i];   //평균 계산 위해 bpm 값 더함
        }
        BPM_average=BPM_sum/32;     //BPM 평균 구함

        CommonVariables.bpm2 = BPM_average;

        /**LF 계산*/
        //LF영역 2~4번
        for(int j=2;j<=4;j++){
            LF+=FFTList2[j];
        }

        /**HF 계산*/
        //HF영역 5~12번
        for(int k=5;k<=12;k++){
            HF+=FFTList2[k];
        }

        CommonVariables.LF2=LF;
        CommonVariables.HF2=HF;


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
            //System.out.printf("%.9f\n",power_spectrum[i]);
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
        handler2.postDelayed(r2, 50000); //50초 뒤에 Runnable 객체 수행, 35초동안 PPI 측정

    }

    @Override
    protected void onPause() {
        super.onPause();
        // 화면을 벗어나면, handler 에 예약해놓은 작업을 취소
        handler2.removeCallbacks(r2); // 예약 취소

    }

    /**
     * 앱 시작 시에 실행되는 함수
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feel__start2);

        // 전체 화면으로 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 화면 꺼지지 않도록 설정
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // 인터페이스 초기화
        initialize();

        //연결
        bind();

        //비디오 재생
        final VideoView videoView =
                (VideoView) findViewById(R.id.forest);

        videoView.setVideoPath("android.resource://" + getPackageName() + "/" + R.raw.forest_s);


        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        mediaController.setPadding(0, 0, 0, 0); //
        videoView.setMediaController(mediaController);

        videoView.start();

    }

    /**
     * 앱 종료 시에 실행되는 함수
     */
    @Override
    public void onDestroy() {
        // 앱 종료 시 PPG 장비 연결 해제
        unbind();
        super.onDestroy();
    }

    /**
     * 인터페이스 초기화 관련 함수
     */
    private void initialize() {
        // 인터페이스 초기화
        //mPPGGraph = new LineChartGraph(this, (RelativeLayout) findViewById(R.id.ppg_view), 300, 2);
        //mBpmTv = (TextView) findViewById(R.id.bpm_tv);

        //mStartBtn = (Button) findViewById(R.id.start_btn);
        mStopBtn = (Button) findViewById(R.id.stop_btn);

//        mStartBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                bind();
//                Toast.makeText(act, "측정이 시작되었습니다.", Toast.LENGTH_SHORT).show();
//            }
//        });

        //취소 버튼 누르면 메인으로 돌아감
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Feel_Start2.this)
                        //뒤로 가기 확인여부 묻는 창
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setTitle("Closing Activity")
                        .setMessage("감성 측정을 중단하시겠습니까?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //넘어가는 부분
                                Intent home = new Intent(Feel_Start2.this, Feel_Angry.class);
                                onDestroy();//연결종료
                                handler2.removeCallbacks(r2); // 예약 취소
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
    }

    /**
     * BPM 콜백 함수
     */
    private void onCallbackReceivedBPM(double bpm) {
//        String contents = (bpm) + "\n";
//        WriteTextFile(foldername, file_bpm, contents);
//        String contents2=(60/bpm)+"\n";
//        WriteTextFile(foldername,file_ppi,contents2);

        //FFT위해 배열에 저장
        if(countPPI<32) {
            ppiList2[countPPI] = (60 / bpm);   //0~34까지의 index에 ppi값 저장
            BPMList2[countPPI]=bpm;
            //Log.d(TAG, "\nindex"+countPPI+" : " + ppiList2[countPPI]); //확인위해 Log찍어봄
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
        if (bind) {
            if (mBIosignalManager == null)
                mBIosignalManager = BiosignalManager.getInstanceForApplication(this);
            mBIosignalManager.bind(this);
        } else {
            if (mBIosignalManager == null) return;
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
        if (address == null) return;
        try {
            if (mBIosignalManager != null) {
                Log.d(TAG, "onConnectBiosignal : " + address);
                mBIosignalManager.connect(0, address);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
                        Intent home = new Intent(Feel_Start2.this, SelectMode.class);
                        onDestroy();//
                        handler2.removeCallbacks(r2); // 예약 취소
                        startActivity(home);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}