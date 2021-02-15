package com.example.speechrecognizer;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.speechrecognizer.commnon.Util;
import com.example.speechrecognizer.commnon.components.Configure;
import com.example.speechrecognizer.commnon.components.Media;
import com.example.speechrecognizer.commnon.components.Recognizer;
import com.example.speechrecognizer.commnon.components.PermissionRequest;
import com.example.speechrecognizer.commnon.network.Dispatcher;
import com.example.speechrecognizer.commnon.network.TcpInfo;
import com.example.speechrecognizer.ui.visualizer.FftView;
import com.example.speechrecognizer.ui.visualizer.LevelMeter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private PermissionRequest permissionRequest = null;
    private Media media = null;
    private Recognizer recognizer = null;

    private FftView fftView = null;
    private LevelMeter levelMeter = null;

    private Dispatcher tClient = null;
    private Configure configure = null;
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        permissionRequest.requestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_configure)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        configure = new Configure(this);
        //初期値を一旦ローカルアドレスにする
        if (configure.getIPAddress().isEmpty()){
            configure.setIPAddress(TcpInfo.getWifiIPAddress(this));
        }
        tClient = new Dispatcher(this);

        permissionRequest = new PermissionRequest(this);
        media = new Media(this);
        if (permissionRequest.storagePermissionRequest()) {
            media.initialize();
        }

        recognizer = new Recognizer(this);
        findViewById(R.id.recognize_action_button).setOnClickListener(view -> {
            if (permissionRequest.recordPermissionCheck()) {
                if (recognizer.isStarted()){
                    recognizer.stopListening();
                }else {
                    recognizer.startListening();
                }
            }
        });

        //ボリューム表示イベントを追加
        levelMeter = findViewById(R.id.volume_visualizer);
        recognizer.onLevelChangeListener(new Recognizer.OnLevelChangeListener(){
            @Override
            public void onLevelChanged(float rmsdB) {
                levelMeter.update(rmsdB);
            }
        });

        //FFTグラフ表示イベントを追加
        fftView = findViewById(R.id.sound_visualizer);
        fftView.setSamplingRate(recognizer.getSamplingRate());
        recognizer.onDataCaptureListener(new Recognizer.OnDataCaptureListener() {
            @Override
            public void onFftDataCapture(byte[] fft) {
                fftView.update(fft);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.stopListening();
    }

    int ringVolume;
    @Override
    protected void onResume() {
        super.onResume();
        setMute();
        try {
            tClient.connect(configure.getIPAddress(), configure.getPort());
        }catch (Exception e){
            configure.setIPAddress(TcpInfo.getWifiIPAddress(this));
            Log.d(Util.getClassName(), e.getMessage());
        }
        //認識結果表示イベントを追加
        recognizer.OnResultsListener(new Recognizer.OnResultsListener(){
            @Override
            public void onResults(String results) {
                if (configure.getUseSuffix()) results += configure.getSuffix();
                TextView resultView = (TextView)findViewById(R.id.recognize_text_view);
                resultView.setText(results);
                tClient.send(results);
                media.commit(results);
            }
        });
    }

    @Override
    protected void onPause() {
        recognizer.stopListening();
        setUnMute();
        super.onPause();
    }

    private void setMute(){
        AudioManager alramManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        alramManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
    }

    private void setUnMute(){
        AudioManager alramManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        alramManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
    }
}