package com.sunzhiming.mobileplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sunzhiming.mobileplayer.R;
import com.sunzhiming.mobileplayer.base.BaseeActivity;
import com.sunzhiming.mobileplayer.bean.VideoInfo;
import com.sunzhiming.mobileplayer.util.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by sunzhiming on 2016/8/14.
 */
public class VideoPlayActivity extends BaseeActivity {
    @Bind(R.id.videoview)
    VideoView mVideoview;
    @Bind(R.id.tv_title)
    TextView mTvTitle;
    @Bind(R.id.iv_battery)
    ImageView mIvBattery;
    @Bind(R.id.tv_titme)
    TextView mTvTitme;
    @Bind(R.id.btn_voice)
    Button mBtnVoice;
    @Bind(R.id.sb_seekbar)
    SeekBar mSbSeekbar;
    @Bind(R.id.fl_overlay)
    FrameLayout mFlOverlay;
    @Bind(R.id.tv_progress)
    TextView mTvProgress;
    @Bind(R.id.sb_video)
    SeekBar mSbVideo;
    @Bind(R.id.tv_duration)
    TextView mTvDuration;
    @Bind(R.id.btn_exit)
    Button mBtnExit;
    @Bind(R.id.btn_back)
    Button mBtnBack;
    @Bind(R.id.btn_start)
    Button mBtnStart;
    @Bind(R.id.btn_next)
    Button mBtnNext;
    @Bind(R.id.btn_full)
    Button mBtnFull;
    @Bind(R.id.ll_top)
    LinearLayout mLlTop;
    @Bind(R.id.fl_bottom)
    FrameLayout mFlBottom;
    private int mCurrntVideo;
    private boolean isMute = false;//是否静音

    private BatterChangeReservice batterChangeReservice = null;
    private VideoInfo mCurrentVideoInfo;

    private final int MSG_UPDATA_SYSTEM_TIME = 1;//更新新系统时间
    private final int MSG_UPDATA_VIDEO = 2;
    private  final int MSG_CLOSE_CONTOLOR = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATA_SYSTEM_TIME:
                    updataSystemTime();
                    break;
                case MSG_UPDATA_VIDEO:
                    updataPalyTimeAndProgress();
                    break;
                case MSG_CLOSE_CONTOLOR:
                    animationHideContolot();
                    break;
            }

        }
    };
    private int mCurrentVolum;
    private int mMaxVolume;
    private AudioManager mAudioManager;
    private int mScrrenWidth;
    private ArrayList<VideoInfo> mVideoList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initData() {
        //初始化屏幕宽度
        mScrrenWidth = getWindowManager().getDefaultDisplay().getWidth();


        //显示系统时间
        updataSystemTime();
        //注册广播接收者监听系统电量的变化
        registerBatteryChangReceiver();

        //初始化音量
        initVolume();

        //刚进来时隐藏控制面板
        hideContollerLayout();

        //判断vitamin有没有初始化
        if (!Vitamio.isInitialized(getApplicationContext())){
            return;
        }

        //获取initent判断是否是第三方的视频
        Uri uri = getIntent().getData();
        if (uri  != null){
            mVideoview.setVideoURI(uri);
            //设置标题
            mTvTitle.setText(uri.getPath());
            //设置监听器
            mVideoview.setOnPreparedListener(mOnPreparedListener);
        }else{
            //取出当前 位置和视频列表数据
            mCurrntVideo = getIntent().getIntExtra("currntVideo", 0);
            mVideoList = (ArrayList<VideoInfo>) getIntent().getSerializableExtra("videoList");

            playVideo();
        }
    }

    /*
    隐藏控制面板
     */
    private void hideContollerLayout() {
        mLlTop.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLlTop.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mLlTop.setTranslationY(-mLlTop.getMeasuredHeight());
            }
        });

        mFlBottom.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mFlBottom.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mFlBottom.setTranslationY(mFlBottom.getMeasuredHeight());
            }
        });


    }

    private void playVideo() {
        mCurrentVideoInfo = mVideoList.get(mCurrntVideo);
        //显示标题
        mTvTitle.setText(mCurrentVideoInfo.title);

        mVideoview.setVideoPath(mCurrentVideoInfo.path);
        //设置准备播放监听器
        mVideoview.setOnPreparedListener(mOnPreparedListener);
    }

    MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //开始播放
            mVideoview.start();

            //给播放按钮更换图片
            mBtnStart.setBackgroundResource(R.drawable.selector_btn_pause);
            //更新播放时间和进度
            updataPalyTimeAndProgress();
        }
    };

    /**
     * 更新播放时间和进度
     */
    private void updataPalyTimeAndProgress() {
        mTvProgress.setText(Utils.formatDuration(mVideoview.getCurrentPosition()));
        mTvDuration.setText(Utils.formatDuration(mVideoview.getDuration()));

        //更新进度条
        mSbVideo.setMax((int) mVideoview.getDuration());
        mSbVideo.setProgress((int) mVideoview.getCurrentPosition());
        handler.sendEmptyMessageDelayed(MSG_UPDATA_VIDEO, 200);
    }

    //初始化音量
    private void initVolume() {
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //当前音量
        mCurrentVolum = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        //最大音量  15
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        mSbSeekbar.setMax(mMaxVolume);//设置进度条的最大值
        mSbSeekbar.setProgress(mCurrentVolum);


    }

    /**
     * 注册广播接收者监听系统电量的变化
     */
    private void registerBatteryChangReceiver() {
        batterChangeReservice = new BatterChangeReservice();
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batterChangeReservice, filter);
    }

    /**
     * 更新系统时间
     */
    private void updataSystemTime() {
        mTvTitme.setText(Utils.formartSystemTime());
        //定时更新，做延时任务--
        handler.sendEmptyMessageDelayed(MSG_UPDATA_SYSTEM_TIME, 1000);

    }

    @Override
    public void setListener() {
        mSbSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             *
             * @param seekBar
             * @param progress
             * @param b 是否是手指拖动，拖动就走这个，不是就不走
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    mCurrentVolum = progress;
                    upDataVolum();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isMute = false;//只要手指触摸seekbar就改为飞静音模式
                //开始滑动的时候移除消息
                handler.removeMessages(MSG_CLOSE_CONTOLOR);
            }

            /**
             * 手指抬起
             * @param seekBar
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止滑动的时候发送信息
                handler.sendEmptyMessageDelayed(MSG_CLOSE_CONTOLOR,4000);
            }
        });

        /**
         * 设置视频播放完成的监听
         */
        mVideoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                handler.removeMessages(MSG_UPDATA_VIDEO);
                mBtnStart.setBackgroundResource(R.drawable.selector_btn_play);
                //因为时间有延迟，所以在播放完成的时候，移除了消息，不能及时更新时间，需要手动设置
                mTvProgress.setText(Utils.formatDuration(mVideoview.getDuration()));
            }
        });
        /**
         * 播放视频的seekbar进度条监听
         */
        mSbVideo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    mVideoview.seekTo(progress);
                    //手动跟新播放时间
                    mTvProgress.setText(Utils.formatDuration(mVideoview.getCurrentPosition()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //开始滑动的时候移除消息
                handler.removeMessages(MSG_CLOSE_CONTOLOR);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止滑动的时候发送信息
                handler.sendEmptyMessageDelayed(MSG_CLOSE_CONTOLOR,4000);
            }
        });

        //设置缓冲进度的监听
        mVideoview.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                int buf = (int) (i / 100f * mVideoview.getDuration());
                mSbVideo.setSecondaryProgress(buf);
            }
        });

        //设置播放卡顿的监听器
        mVideoview.setOnInfoListener(new MediaPlayer.OnInfoListener() {

            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int what, int extra) {
                switch (what){
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START://开始卡斯
                        Toast.makeText(getApplicationContext(),"大哥慢点...",Toast.LENGTH_SHORT).show();
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END://结束卡斯
                        Toast.makeText(getApplicationContext(),"大哥快......",Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });

        //设置播放失败监听器
        mVideoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                switch (i){
                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED://未知格式
                        Toast.makeText(getApplicationContext(),"未知格式",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                    case MediaPlayer.MEDIA_ERROR_UNKNOWN://不支持格式
                        Toast.makeText(getApplicationContext(),"不支持格式",Toast.LENGTH_SHORT).show();
                        finish();
                        break;
                }
                return false;
            }
        });
    }

    /**
     * 跟新音量
     * @param
     */
    private void upDataVolum() {


        if (isMute) {
            //将系统音量置为0，progress置为0
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            mSbSeekbar.setProgress(0);
        } else {
            //非静音将系统音量恢复，progress恢复
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolum, 0);
            mSbSeekbar.setProgress(mCurrentVolum);
        }


    }


    private float downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                
                totgleChangeContollorLayout();
                break;
            case MotionEvent.ACTION_MOVE:
                //1.计算移动的坐标
                float moveX = event.getX();
                float moveY = event.getY();
                //2.计算移动的距离
                float deltaX = moveX - downX;
                float deltaY = moveY - downX;

                if (moveX < mScrrenWidth / 2) {
                    //说明在左边，需要改变明暗度
                    float alpha = mFlOverlay.getAlpha();
                    //往下是变暗
                    if (deltaY > 0) {
                        alpha += 0.01;
                        if (alpha > 1) alpha = 0.7f;
                        mFlOverlay.setAlpha(alpha);
                    } else if (deltaY < 0) {
                        alpha -= 0.02;
                        if (alpha > 0) alpha = 0f;
                        mFlOverlay.setAlpha(alpha);
                    }
                } else {
                    //说明在右边，改变音量
                    changeVolume(deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
                //延时隐藏控制面板
                handler.sendEmptyMessageDelayed(MSG_CLOSE_CONTOLOR,2000);
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isShowContollor;
    /**
     * 点击切换控制面板
     */
    private void totgleChangeContollorLayout() {
        if (isShowContollor){
            //就该隐藏
            animationHideContolot();
        }else{
            //显示
            animationShowContolot();
        }
    }

    /**
     * 显示控制面板
     */
    private void animationShowContolot() {
        isShowContollor = true;
        ViewCompat.animate(mLlTop).translationY(0).setDuration(400).start();
        ViewCompat.animate(mFlBottom).translationY(0).setDuration(400).start();
    }

    /**
     * 隐藏控制面板
     */
    private void animationHideContolot() {
        handler.removeMessages(MSG_CLOSE_CONTOLOR);
        isShowContollor = false;
        ViewCompat.animate(mLlTop).translationY(-mLlTop.getHeight()).setDuration(400).start();
        ViewCompat.animate(mFlBottom).translationY(mFlBottom.getHeight()).setDuration(400).start();
    }

    /**
     * 根据是指触摸改变音量
     * @param deltaY
     */
    private void changeVolume(float deltaY) {
        isMute = false;
        if (deltaY > 0) {
            //向下，缩小
            mCurrentVolum -= 1;//每次递减1个音量
            if (mCurrentVolum < 0) mCurrentVolum = 0;

        } else {
            //向上放大
            mCurrentVolum += 1;//每次递增1个音量
            if (mCurrentVolum > mMaxVolume) mCurrentVolum = mMaxVolume;
        }
        upDataVolum();
    }

    @OnClick({R.id.btn_voice, R.id.btn_exit, R.id.btn_back, R.id.btn_start, R.id.btn_next, R.id.btn_full})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_voice:
                isMute = !isMute;
                upDataVolum();
                break;
            case R.id.btn_exit:
                //退出播放界面
                finish();
                break;
            case R.id.btn_back:
                //播放上一个
                backPaly();
                break;
            case R.id.btn_start:
                toglePlay();
                break;
            case R.id.btn_next:
                //播放下一个
                playNext();
                break;
            case R.id.btn_full:
                mVideoview.toggleFullScreen();
                mBtnFull.setBackgroundResource(mVideoview.isFullScreen()?
                        R.drawable.selector_btn_defaultscreen:
                        R.drawable.selector_btn_fullscreen);
                break;
        }
    }

    /**
     * 播放下一个
     */
    private void playNext() {
        if (mCurrntVideo < (mVideoList.size() - 1)) {
            mCurrntVideo++;
            playVideo();
        }
    }

    /**
     * 播放上一个视频
     */
    private void backPaly() {
        if (mCurrntVideo > 0) {
            mCurrntVideo--;
            playVideo();
        }
    }

    /**
     * 开始切换播放按钮
     */
    private void toglePlay() {
        if (mVideoview.isPlaying()) {
            //暂停暂停
            mVideoview.pause();
            mBtnStart.setBackgroundResource(R.drawable.selector_btn_play);
        } else {

            updataPalyTimeAndProgress();//每次开始播放的时候调用这个方法
            //开始播放
            mVideoview.start();
            mBtnStart.setBackgroundResource(R.drawable.selector_btn_pause);
        }
    }
    /**
     * 定义广播接收者
     */
    class BatterChangeReservice extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取电量等级  0--100
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            //根据电量等级显示图片
            showBatteryByLevel(level);
        }
    }

    //根据电量等级显示图片
    private void showBatteryByLevel(int level) {

        Log.i("tag", "_________________________________________________________----" + level);
        if (level == 0) {
            mIvBattery.setBackgroundResource(R.mipmap.ic_battery_0);
        } else if (level >= 0 && level < 10) {
            mIvBattery.setBackgroundResource(R.mipmap.ic_battery_10);
        } else if (level >= 10 && level < 20) {
            mIvBattery.setBackgroundResource(R.mipmap.ic_battery_20);
        } else if (level >= 20 && level < 40) {
            mIvBattery.setBackgroundResource(R.mipmap.ic_battery_40);
        } else if (level >= 40 && level < 60) {
            mIvBattery.setBackgroundResource(R.mipmap.ic_battery_60);
        } else if (level >= 60 && level < 80) {
            mIvBattery.setBackgroundResource(R.mipmap.ic_battery_80);
        } else if (level >= 80) {
            mIvBattery.setBackgroundResource(R.mipmap.ic_battery_100);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //如果按下的是音量向上或者向下
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //获取音量并且更新
            mCurrentVolum = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            mSbSeekbar.setProgress(mCurrentVolum);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batterChangeReservice);
        handler.removeCallbacksAndMessages(null);
    }
}
