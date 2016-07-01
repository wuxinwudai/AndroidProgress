package com.wuxinwudai.ap.sample;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.wuxinwudai.ap.ArcProgress;
import com.wuxinwudai.ap.RingProgress;
import com.wuxinwudai.ap.HorizontalProgress;
import com.wuxinwudai.ap.OnProgressCompletedListener;
import com.wuxinwudai.ap.RoundProgress;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private RingProgress mCircleProgress;
    private HorizontalProgress mHorizontalProgress;
    private RoundProgress mRoundProgress;
    private ArcProgress mArcProgress;
    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 11){
                mCircleProgress.progress();
                mHorizontalProgress.progress();
                mRoundProgress.progress();
                mArcProgress.progress();
            }
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCircleProgress = (RingProgress)findViewById(R.id.cp);
        mHorizontalProgress = (HorizontalProgress)findViewById(R.id.hp);
        mRoundProgress = (RoundProgress)findViewById(R.id.rp);
        mArcProgress = (ArcProgress)findViewById(R.id.arcp);
        mCircleProgress.setOnProgressCompletedListener(new OnProgressCompletedListener() {
            @Override
            public void complete() {
                mExecutor.shutdown();
                Toast.makeText(MainActivity.this,"进度已完成",Toast.LENGTH_SHORT).show();
            }
        });
        mExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
            mHandler.sendEmptyMessage(11);
        }
        },3000,500, TimeUnit.MILLISECONDS);
    }
}
