package com.third.app.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.third.app.Bean.Multi;
import com.third.app.Constant.Constant;
import com.third.app.DBManager.DBManager;
import com.third.app.Model.CommentInfo;
import com.third.app.Model.VideoInfo;
import com.third.app.R;
import com.third.app.Save.KeyFile;
import com.third.app.Save.KeyUser;
import com.third.app.Tool.AesTool;
import com.third.app.Tool.DateTool;
import com.third.app.Tool.FileTool;
import com.third.app.Tool.NetTool;
import com.third.app.Tool.ParamsPutterTool;
import com.third.app.Tool.ScreenTool;
import com.third.app.Tool.VipTool;
import com.third.app.View.BarrageItem;
import com.third.app.View.BarrageView;
import com.third.app.View.T;
import com.superplayer.library.SuperPlayer;
import com.third.wechatv.wxapi.WXPayEntryActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 类描述：视频详情页
 *
 * @author Super南仔
 * @time 2016-9-19
 */
public class SuperVideoDetailsActivity extends AppCompatActivity implements View.OnClickListener, SuperPlayer.OnNetChangeListener {

    @Bind(R.id.center_images)
    ImageView centerImages;
    @Bind(R.id.diamond_text)
    TextView diamondText;
    private SuperPlayer player;
    private boolean isLive;
    private VideoInfo videoInfo;
    private String video_title;
    private boolean isThree;
    private boolean isLookArea;
    private Activity mContext;
    private List<String> barTexts = new ArrayList<>();

    private Handler videoHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    /**
     * 测试地址
     */
    private String url;
    private BarrageView barrageView;
    private ImageView submit;
    private EditText et_barrage;
    private DBManager dbManager;
    private RelativeLayout barrage_submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 禁止屏幕休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.video_player);
        ButterKnife.bind(this);
        mContext = this;
        initData();
        initView();
        initPlayer();
        registerBoradcastReceiver();
    }


    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("play_video_succes");
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            //接受广播做逻辑处理
            String action = intent.getAction();
            if (action.equals("play_video_succes")) {
                String playContent = intent.getStringExtra("playContent");
                if (playContent.equals("hd")) {
                    if (isLive == true) {
                        //不执行任何代码
                        if (!VipTool.canVip1(mContext)) {
                            player.stop();
                            initShowDialog();
                            return;
                        }
                    } else {
                        if (url.equals(videoInfo.getAddress_hd())) {
                            return;
                        }
                        url = videoInfo.getAddress_hd();
                        video_title = videoInfo.getName();
                        player.setTitle(video_title)//设置视频的titleName
                                .play(url, player.getCurrentPosition());//开始播放视频
                        player.setScaleType(SuperPlayer.SCALETYPE_16_9);
                        player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
                        choiceVip(mContext, false);
                    }

                } else if (playContent.equals("sd")) {
                    if (isLive == true) {
                        //不执行任何代码
                        if (!VipTool.canVip1(mContext)) {
                            player.stop();
                            initShowDialog();
                            return;
                        }
                    } else {
                        if (!VipTool.canVip1(mContext)) {
                            player.stop();
                            initShowDialog();
                            return;
                        }
                        if (url.equals(videoInfo.getAddress_sd())) {
                            return;
                        }
                        if (VipTool.canVip1(mContext)) {
                            url = videoInfo.getAddress_sd();
                            video_title = videoInfo.getName();
                            player.setTitle(video_title)//设置视频的titleName
                                    .play(url, player.getCurrentPosition());//开始播放视频
                            player.setScaleType(SuperPlayer.SCALETYPE_16_9);
                            player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
                            choiceVip(mContext, false);
                        } else {
                            player.stop();
                            initShowDialog();
                        }
                    }

                } else if (playContent.equals("dialog_show")) {
                    if (player != null) {
                        player.stop();
                    }
                    if (isLive == true && !isThree) {
                        //直播结束
                        FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt(Constant.LIVE_DATE + ""));
//                      alerPayFailTiShi();
                    }
                    if (isThree) {
                        //三级结束
                        FileTool.writeFileToSDFile(Constant.TV_SHIYONG_MP4_ALL, AesTool.encrypt(Constant.Three_DATE + ""));
                        initShowDialog();
                    }
                    if (isLookArea) {
                        initShowDialog();
                    }
                } else if (playContent.equals("activity_finish")) {
                    //doPlayData();
                    finish();
                }

                //短视频流程一(停用)
                else if (playContent.equals("playing")) {
                    if (startPlayDate == null) {
//                        sendUserDoData("3", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    }
                    startPlayDate = new Date();
                } else if (playContent.equals("stop")) {
                    doPlayData();
                } else if (playContent.equals("loading")) {
                    doPlayData();
                } else if (playContent.equals("play_finish")) {
                    doPlayData();
                } else if (playContent.equals("resume_timer")) {
                    resumeTimer();
                }
                //短视频流程二(启用)
                else if (playContent.equals("shot_video_stop_play")) {
                    initShowDialog();
                    if (player != null) player.stop();
                }

                //直播处理
                else if (playContent.equals("playing_live")) {
                    if (startPlayDate_live == null) {
//                        sendUserDoData("3", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
                            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
                            if (TextUtils.isEmpty(oldTime)) {
                                FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt("100"));
                            } else {
                                if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
                                    //试用过期
//                                    alerPayFailTiShi();
                                    if (player != null) {
                                        player.stop();
                                    }
                                    player.isStartTime = true;
                                    player.startTime();
                                } else {
                                    //没有过期
                                    player.isStartTime = true;
                                    if (player != null) {
                                        player.timeSecond = Constant.LIVE_DATE - Integer.parseInt(oldTime);
                                    }
                                    player.startTime();
                                }
                            }
                        }
                    }
                    startPlayDate_live = new Date();
                } else if (playContent.equals("stop_live")) {
                    doPlayData_Live();
                } else if (playContent.equals("loading_live")) {
                    doPlayData_Live();
                } else if (playContent.equals("play_finish_live")) {
                    doPlayData_Live();
                } else if (playContent.equals("resume_timer_live")) {
                    resumeTimer_Live();
                }
                //弹幕
                else if (playContent.equals("barrage_close")) {
                    barrageView.setVisibility(View.GONE);
                    Constant.barra_show = false;
                    barrageView.stopBarrage();
                } else if (playContent.equals("barrage_show")) {
                    barrageView.setVisibility(View.VISIBLE);
                    Constant.barra_show = true;
                    initBarrage();
                    barrageView.startBarrage();

                } else if (playContent.equals("send_barra")) {
                    if (barrage_submit.getVisibility() == View.VISIBLE) {
                        barrage_submit.setVisibility(View.GONE);
                    } else {
                        barrage_submit.setVisibility(View.VISIBLE);
                    }
                } else if (playContent.equals("hide_input")) {
                    barrage_submit.setVisibility(View.GONE);
                }
            }
        }
    };

    private Date startPlayDate_live;
    private Date loadAndStopDate_live;

    private void resumeTimer_Live() {
        String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
        if (!TextUtils.isEmpty(vipStatus2)) {
            if (vipStatus2.equals(VipTool.YES_VIP + "")) {
                return;
            }
        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_DIAMOND_TYPE ||
                vipType == Multi.VIP_SILVER_TYPE ||
                vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                vipType == Multi.VIP_GOLD_TYPE ||
                vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                vipType == Multi.VIP_CROWN_TYPE) {
            return;
        }
        try {
            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
            if (TextUtils.isEmpty(oldTime)) {
            } else {
                if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
                    //试用过期
                    initShowDialog();
                    if (player != null) {
                        player.stop();
                    }
                    player.isStartTime = true;
                } else {
                    //没有过期
                    if (player != null) {
                        //player.timeSecond = Constant.doDate - Integer.parseInt(oldTime);
                    }
                    player.isStartTime = true;
                    player.startTime();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resumeTimer() {
        String vipStatus1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY));
        if (!TextUtils.isEmpty(vipStatus1)) {
            if (vipStatus1.equals(VipTool.YES_VIP + "")) {
                return;
            }
        }
        try {
            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL));
            if (TextUtils.isEmpty(oldTime)) {
            } else {
                if (Integer.parseInt(oldTime) > Constant.Three_DATE) {
                    //试用过期
                    initShowDialog();
                    if (player != null) {
                        player.stop();
                    }
                    player.isStartTime = true;
                } else {
                    //没有过期
                    if (player != null) {
                        //player.timeSecond = Constant.doDate - Integer.parseInt(oldTime);
                    }
                    //如果是体验区，则不启动倒计时（开始播启动）
//                    if (!isLookArea) {
//                        player.isStartTime = true;
//                        player.startTime();
//                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doPlayData_Live() {
        //直播用v2
//        String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
//        if (!TextUtils.isEmpty(vipStatus2)) {
//            if (vipStatus2.equals(VipTool.YES_VIP + "")) {
//                return;
//            }
//        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_DIAMOND_TYPE ||
                vipType == Multi.VIP_SILVER_TYPE ||
                vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                vipType == Multi.VIP_GOLD_TYPE ||
                vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                vipType == Multi.VIP_CROWN_TYPE) {
        } else if (isLive == true && !isThree && vipType == Multi.VIP_NOT_VIP_TYPE) {
            loadAndStopDate_live = new Date();
            File file = new File(Constant.TV_SHIYONG_M3U8_ALL);
            if (!file.exists()) {
                FileTool.createFile(Constant.TV_SHIYONG_M3U8_ALL);
            }
            String old = FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL);
            String oldTime = AesTool.decrypt(old);
            if (TextUtils.isEmpty(oldTime)) {
                //第一次写入(给1个小时)
                if (player != null) {
                    player.isStartTime = true;
                    player.timeSecond = Constant.LIVE_DATE;
                }
                FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt("100"));
            } else {
                if (startPlayDate_live != null) {
                    long[] time = DateTool.getTime(loadAndStopDate_live, startPlayDate_live);
                    int second = Integer.parseInt(oldTime) + Integer.parseInt(time[3] + "");
                    FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt(second + ""));
                }
            }
        }
    }

    private void doPlayData() {
        //短视频用v1
        if (isThree && !VipTool.canVip1(mContext)) {
            loadAndStopDate = new Date();
            File file = new File(Constant.TV_SHIYONG_MP4_ALL);
            if (!file.exists()) {
                FileTool.createFile(Constant.TV_SHIYONG_MP4_ALL);
            }
            String old = FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL);
            String oldTime = AesTool.decrypt(old);
            if (TextUtils.isEmpty(oldTime)) {
                //第一次写入(给1个小时)
                if (player != null) {
                    player.isStartTime = true;
                    player.timeSecond = Constant.Three_DATE;
                }
                FileTool.writeFileToSDFile(Constant.TV_SHIYONG_MP4_ALL, AesTool.encrypt("100"));
            } else {
                if (startPlayDate != null) {
                    long[] time = DateTool.getTime(loadAndStopDate, startPlayDate);
                    int minutes = Integer.parseInt(oldTime) + Integer.parseInt(time[3] + "");
                    FileTool.writeFileToSDFile(Constant.TV_SHIYONG_MP4_ALL, AesTool.encrypt(minutes + ""));
                }
            }
        }
    }

    private Date startPlayDate;
    private Date loadAndStopDate;

    /**
     * 初始化相关的信息
     */
    private void initData() {
        dbManager = DBManager.getDBManager(this);
        isLive = getIntent().getBooleanExtra("isLive", false);
        if (isLive == true) {
            url = getIntent().getStringExtra("url");
            video_title = getIntent().getStringExtra("title");
            isThree = false;
        } else {
            videoInfo = (VideoInfo) getIntent().getSerializableExtra("videoInfo");
            url = videoInfo.getAddress_hd();
            video_title = videoInfo.getName();
            isThree = videoInfo.isThreeVideo();
            isLookArea = videoInfo.isLookVideo();
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ScreenTool.setLight(mContext, 150);
//        findViewById(R.id.tv_replay).setOnClickListener(this);
//        findViewById(R.id.tv_play_location).setOnClickListener(this);
//        findViewById(R.id.tv_play_switch).setOnClickListener(this);
        barrage_submit = (RelativeLayout) findViewById(R.id.barrage_submit);
        et_barrage = (EditText) findViewById(R.id.et_barrage);
        barrageView = (BarrageView) findViewById(R.id.containerView);
        barrageView.setVisibility(Constant.barra_show ? View.VISIBLE : View.GONE);
        submit = (ImageView) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String text = et_barrage.getText().toString();
                    barrage_submit.setVisibility(View.GONE);
                    if (TextUtils.isEmpty(text)) {
                        T.showTextToast(mContext, "请输入弹幕内容");
                        return;
                    }
                    barrageView.initBarrageItem(new BarrageItem(), text);
                    T.showTextToast(mContext, "评论成功!!!");
                } catch (Exception e) {
                }
            }
        });
        initBarrage();
    }

    private void initBarrage() {
        barrageView.getShowList().clear();
        List<CommentInfo> commentInfoList = dbManager.queryCommentAll();
        Collections.shuffle(commentInfoList);
        int listSize = 0;
        if (commentInfoList.size() != 0) {
            barrageView.getItemText().clear();
            if (commentInfoList.size() < 10) {
                listSize = commentInfoList.size();
            } else {
                listSize = 10;
            }
            barrageView.textCount = listSize;
            for (int i = 0; i < listSize; i++) {
                barTexts.add(commentInfoList.get(i).getInfo());
            }
        }
        barrageView.setItemText(barTexts);
    }


    /**
     * 初始化播放器
     */
    private void initPlayer() {
        if (player == null) {
            player = (SuperPlayer) findViewById(R.id.view_super_player);
        }
        if (videoInfo != null) {
            if (TextUtils.isEmpty(videoInfo.getSpare1())) {
                player.spareIsNull = true;
            } else {
                String spare1 = videoInfo.getSpare1();
                player.spare_server = Integer.parseInt(spare1);
            }
        } else {
            player.spareIsNull = true;
        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            VipTool.shi_Kan_Six_Video(mContext);
            player.lookCurrentTotal = Integer.parseInt(VipTool.get_ShiKan_Video_Count(mContext)) - 1;
        }
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            player.userIsShowController = true;
        } else {
            player.userIsShowController = true;
        }
        try

        {
            if (!isLive && videoInfo != null) {
                if (TextUtils.isEmpty(videoInfo.getId()) || videoInfo.getId().equals("0")) {
                    player.videoTime = 90;
                } else if (!TextUtils.isEmpty(videoInfo.getId())) {
                    int time = Integer.parseInt(dbManager.queryVideoTimeById(videoInfo.getId()).getVideo_Time());
                    player.videoTime = time;
                } else {
                    player.videoTime = 90;
                }
            }
        } catch (
                Exception e
                )

        {
            player.videoTime = 80;
        }

        if (isLive == true)

        {
            player.setLive(true);//设置该地址是直播的地址
        }

        if (isThree)

        {
            player.isThree = true;
            String M3U8 = "M3U8";
            if (videoInfo.getAddress_hd().toLowerCase().contains(M3U8.toLowerCase())) {
                player.setLive(true);
            } else {
                player.setLive(false);
            }
        }

        player.setNetChangeListener(true)//设置监听手机网络的变化
                .
                        setOnNetChangeListener(this)//实现网络变化的回调
                .
                        onPrepared(new SuperPlayer.OnPreparedListener() {
                                       @Override
                                       public void onPrepared() {
                                           /**
                                            * 监听视频是否已经准备完成开始播放。（可以在这里处理视频封面的显示跟隐藏）
                                            */
                                           //...
                                           int vipType = VipTool.getUserVipType(mContext);
                                           if (isLookArea && vipType == Multi.VIP_NOT_VIP_TYPE) {
                                               player.isStartTime = true;
                                               player.startTime();
                                           }
                                           //....
                                           if (isLive && !isThree) {
                                               if (vipType == Multi.VIP_SILVER_TYPE || vipType == Multi.VIP_DIAMOND_TYPE || vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                                                       vipType == Multi.VIP_GOLD_TYPE || vipType == Multi.VIP_RED_DIAMOND_TYPE || vipType == Multi.VIP_CROWN_TYPE) {
                                                   String vipLastTime2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY));
                                                   String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
                                                   String result2 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime2);
                                                   //result  1过期 2未过期
                                                   if (result2.equals("2")) {
                                                       //继续执行
                                                       player.isStartTime = false;
                                                   } else if (result2.equals("1")) {
                                                       if (!mContext.isFinishing()) {
                                                           initBan();
                                                       } else {
                                                           initBan();
                                                       }
                                                   }
                                               }
                                           }
                                           //........
                                           initBan();
                                       }
                                   }
                        ).

                onComplete(new Runnable() {
                               @Override
                               public void run() {
                                   /**
                                    * 监听视频是否已经播放完成了。（可以在这里处理视频播放完成进行的操作）
                                    */
                                   int vipType = VipTool.getUserVipType(mContext);
                                   if (vipType == Multi.VIP_SILVER_TYPE ||
                                           vipType == Multi.VIP_GOLD_TYPE ||
                                           vipType == Multi.VIP_DIAMOND_TYPE ||
                                           vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                                           vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                                           vipType == Multi.VIP_CROWN_TYPE) {
                                       player.stop();
                                       initShowDialog();
                                   } else {
                                       player.stop();
                                       initShowDialog();
                                   }
                               }
                           }

                ).

                onInfo(new SuperPlayer.OnInfoListener() {
                           @Override
                           public void onInfo(int what, int extra) {
                               /**
                                * 监听视频的相关信息。
                                */
                           }
                       }
                ).

                onError(new SuperPlayer.OnErrorListener() {
                            @Override
                            public void onError(int what, int extra) {
                                /**
                                 * 监听视频播放失败的回调
                                 */
                                if (!NetTool.isConnected(mContext)) {
                                    showTextToast(mContext, "您的网络没有连接!!");
                                } else {
                                    showTextToast(mContext, "视频出了点小问题");
                                }
                            }
                        }

                ).

                setTitle(video_title)//设置视频的titleName

                .

                        play(url);//开始播放视频

        player.setScaleType(SuperPlayer.SCALETYPE_16_9);
        player.setPlayerWH(0, player.getMeasuredHeight());//设置竖屏的时候屏幕的高度，如果不设置会切换后按照16:9的高度重置
        if (isLive == true && !isThree)

        {
            //直播
            player.tv_zhibo.setVisibility(View.VISIBLE);
            choiceVip(mContext, true);
        } else if (isThree)

        {
            //不是直播
            if (isThree) {
                player.isThree = true;
            } else {
                player.isThree = false;
            }
            player.tv_zhibo.setVisibility(View.GONE);
            videoHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    choiceVip(mContext, false);
                }
            }, 10);
        } else if (!isThree && !isLive)

        {
            videoHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    choiceVip(mContext, false);
                }
            }, 10);
        }

//        centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 8);
//        centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 7);
        centerImages.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View view) {
                                                //v2
                                                Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
                                                Intent intent = new Intent(mContext, WXPayEntryActivity.class);
                                                startActivity(intent);
                                                mContext.finish();
                                            }
                                        }

        );
    }

    @Override
    public void onClick(View view) {
//        if(view.getId() == R.id.tv_replay){
//            if(player != null){
//                player.play(url);
//            }
//        } else if(view.getId() == R.id.tv_play_location){
//            if(isLive){
//                Toast.makeText(this,"直播不支持指定播放",Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if(player != null){
//                /**
//                 * 这个节点是根据视频的大小来获取的。不同的视频源节点也会不一致（一般用在退出视频播放后保存对应视频的节点从而来达到记录播放）
//                 */
//                player.play(url,89528);
//            }
//        } else if(view.getId() == R.id.tv_play_switch) {
//            /**
//             * 切换视频播放源（一般是标清，高清的切换ps：由于我没有找到高清，标清的视频源，所以也是换相同的地址）
//             */
//        if(isLive){
//            player.playSwitch(url);
//        } else {
//            player.playSwitch("http://baobab.wandoujia.com/api/v1/playUrl?vid=2614&editionType=high");
//        }
//        }
    }

    private static Toast toast = null;
    private static int toastDuration = 10;

    private void showTextToast(Activity activity, String msg) {
        if (toast == null) {
            toast = Toast.makeText(activity, msg, toastDuration);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    /**
     * 网络链接监听类
     */
    @Override
    public void onWifi() {
//        showTextToast(mContext, "当前网络环境是WIFI");
    }

    @Override
    public void onMobile() {
//        showTextToast(mContext, "当前网络环境是手机网络");
    }

    @Override
    public void onDisConnect() {
        showTextToast(mContext, "网络链接断开");
    }

    @Override
    public void onNoAvailable() {
        showTextToast(mContext, "无网络链接");
    }

    /**
     * 下面的这几个Activity的生命状态很重要
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.onPause();
        }
        if (isLive == false && isThree == true) {
            doPlayData();
        }
        if (isLive == true && !isThree) {
            doPlayData_Live();
        }
        MobclickAgent.onPageEnd("午夜啪啪播放器"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.onResume();
        }
        mContext.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        MobclickAgent.onPageStart("午夜啪啪播放器"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isLive) {
//            sendUserDoData("4", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        }
        if (player != null) {
            player.onDestroy();
        }
        if (videoHandler != null) {
            videoHandler = null;
        }
        if (barrageView != null) {
            barrageView = null;
        }
        //注销广播
        this.unregisterReceiver(mBroadcastReceiver);
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (player != null) {
//            player.onConfigurationChanged(newConfig);
//        }
//    }

    private int PLAY_ISPLAYING = 2;

    @Override
    public void onBackPressed() {
        if (player.getStatus() == PLAY_ISPLAYING && isLive == false && isThree == true) {
            doPlayData();
        }
        if (player.getStatus() == PLAY_ISPLAYING && isLive == true && !isThree) {
            doPlayData_Live();
        }
        Multi.isShowDialog = true;
        finish();
    }

    private void choiceVip(Activity context, boolean isLive) {
        //直播用v2,
        if (isLive == true) {
            int vipType = VipTool.getUserVipType(mContext);
            if (vipType == Multi.VIP_NOT_VIP_TYPE) {
                File file = new File(Constant.TV_SHIYONG_M3U8_ALL);
                if (!file.exists()) {
                    //没有控制试用vpn的文件(試用)
                    FileTool.createFile(Constant.TV_SHIYONG_M3U8_ALL);
                    player.isStartTime = true;
                    player.timeSecond = Constant.LIVE_DATE;
                    //继续执行
                } else {
                    //有文件
                    try {
                        String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL));
                        if (TextUtils.isEmpty(oldTime)) {
                            FileTool.writeFileToSDFile(Constant.TV_SHIYONG_M3U8_ALL, AesTool.encrypt("100"));
                        } else {
                            if (Integer.parseInt(oldTime) > Constant.LIVE_DATE) {
                                //试用过期
                                initShowDialog();
                                if (player != null) {
                                    player.stop();
                                }
                                player.isStartTime = true;
                            } else {
                                //没有过期
                                player.isStartTime = true;
                                if (player != null) {
                                    player.timeSecond = Constant.LIVE_DATE - Integer.parseInt(oldTime);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (vipType == Multi.VIP_SILVER_TYPE ||
                    vipType == Multi.VIP_DIAMOND_TYPE ||
                    vipType == Multi.VIP_PLAT_NIUM_TYPE ||
                    vipType == Multi.VIP_GOLD_TYPE ||
                    vipType == Multi.VIP_RED_DIAMOND_TYPE ||
                    vipType == Multi.VIP_CROWN_TYPE) {
                String vipLastTime2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY));
                String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
                String result2 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime2);
                //result  1过期 2未过期
                if (result2.equals("2")) {
                    //继续执行
                    player.isStartTime = false;
                } else if (result2.equals("1")) {
                    if (!context.isFinishing()) {
//                        alertVipPay();
//                        if (player != null) {
//                            player.stop();
//                        }
                        //initBan();
                    } else {
//                        alertVipPay();
//                        if (player != null) {
//                            player.stop();
//                        }
                        //initBan();
                    }
                }
            }
        } else {
            //不是直播,不是直播用v1,而且三级试看半小时开启
            if (isThree) {
                //三级(必须钻石会员观看完整,钻石充值的v2)
                player.isThree = true;
                String vipLastTime2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY));
                String vipStatus2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY));
                String result2 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime2);

                //是会员
                if (vipStatus2.equals(VipTool.YES_VIP + "")) {
                    //查看vip会员时间是否过期
                    //result  1过期 2未过期
                    if (result2.equals("2")) {
                        //继续执行
                        player.isStartTime = false;
                    } else if (result2.equals("1")) {
                        if (!context.isFinishing()) {
                            initShowDialog();
                            if (player != null) {
                                player.stop();
                            }
                        }
                    }
                    //不是会员
                } else if (vipStatus2.equals(VipTool.NO_VIP + "")) {
                    //查看普通用户试用时间是否过期(本地控制)
                    //result  1过期 2未过期
                    File file = new File(Constant.TV_SHIYONG_MP4_ALL);
                    if (!file.exists()) {
                        //mp4(試用)
                        FileTool.createFile(Constant.TV_SHIYONG_MP4_ALL);
                        //试用流程三(启用)
                        player.isStartTime = true;
                        player.timeSecond = Constant.Three_DATE;
                    } else {
                        //有文件
                        try {
                            //试用流程三(启用)
                            String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL));
                            if (TextUtils.isEmpty(oldTime)) {
                            } else {
                                if (Integer.parseInt(oldTime) > Constant.Three_DATE) {
                                    //试用过期
                                    initShowDialog();
                                    if (player != null) {
                                        player.stop();
                                    }
                                    player.isStartTime = true;
                                } else {
                                    //没有过期
                                    if (player != null) {
                                        player.timeSecond = Constant.Three_DATE - Integer.parseInt(oldTime);
                                    }
                                    player.isStartTime = true;
                                    videoHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            player.startTime();
                                        }
                                    }, 5000);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else {
                //不是三级
                String vipLastTime1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY));
                String vipStatus1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(this, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY));
                String result1 = DateTool.compareTime2(new SimpleDateFormat("yyyy-MM-dd").format(new Date()), vipLastTime1);

                //是会员
                if (vipStatus1.equals(VipTool.YES_VIP + "")) {
                    //查看vip会员时间是否过期
                    //result  1过期 2未过期
//                    if (result1.equals("2")) {
//                        //继续执行
//                        player.isStartTime = false;
//                        player.userIsVip = true;
//                    } else if (result1.equals("1")) {
//                        if (!context.isFinishing()) {
//                            initShowDialog();
//                            if (player != null) {
//                                player.stop();
//                            }
//                        }
//                    }
                    if(isLookArea){
                        player.userIsVip = false;
                        player.isStartTime = false;//试看区开始播放再计时
                        player.timeSecond = 20;
                        player.startTime();
                    }
                    //不是会员
                } else if (vipStatus1.equals(VipTool.NO_VIP + "")) {
                    //查看普通用户试用时间是否过期(本地控制)
                    //result  1过期 2未过期
//                    File file = new File(Constant.TV_SHIYONG_MP4_ALL);
//                    if (!file.exists()) {
//                        //mp4(試用)
//                        player.isStartTime = false;
//                        FileTool.createFile(Constant.TV_SHIYONG_MP4_ALL);
//                        //试用流程一(停用)
////                    player.isStartTime = true;
////                    player.timeSecond = Constant.doDate;
//                        /***
//                         * 试用流程二(启用)
//                         */
//                        player.userIsVip = false;
//                        videoHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvLook.setVisibility(View.VISIBLE);
//                            }
//                        }, 2000);
//                        videoHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                tvLook.setVisibility(View.GONE);
//                            }
//                        }, 5000);
//                    } else {
//                        //有文件
//                        try {
//                            //试用流程一(停用)
////                        String oldTime = AesTool.decrypt(FileTool.readFileToSDFile(Constant.TV_SHIYONG_MP4_ALL));
////                        if (TextUtils.isEmpty(oldTime)) {
////                        } else {
////                            if (Integer.parseInt(oldTime) > Constant.doDate) {
////                                //试用过期
////                                alerPayFailTiShi();
////                                if (player != null) {
////                                    player.stop();
////                                }
////                                player.isStartTime = true;
////                            } else {
////                                //没有过期
////                                if (player != null) {
////                                    player.timeSecond = Constant.doDate - Integer.parseInt(oldTime);
////                                }
////                                player.isStartTime = true;
////                            }
////                        }
////                            /***
////                             * 试用流程二(启用)
////                             */
////
////                            player.userIsVip = false;
////                            player.isStartTime = true;
////                            player.timeSecond = 20;
////                            player.startTime();
////                            videoHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    tvLook.setVisibility(View.VISIBLE);
////                                }
////                            }, 5000);
////                            videoHandler.postDelayed(new Runnable() {
////                                @Override
////                                public void run() {
////                                    tvLook.setVisibility(View.GONE);
////                                }
////                            }, 10000);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
                    /***
                     * 试用流程二(启用)
                     */

                    player.userIsVip = false;
                    player.isStartTime = false;//试看区开始播放再计时
                    player.timeSecond = 20;
                    player.startTime();
                }
            }
        }
    }

    Dialog dialog_pay_time;

//    public void alertVipPay() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_shikan, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_pay_time == null) {
//            dialog_pay_time = new Dialog(mContext, R.style.Dialog);
//            dialog_pay_time.show();
//            dialog_pay_time.setCancelable(false);
//            Window window = dialog_pay_time.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
//            layout.getBackground().setAlpha(150);
//            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
//            window.setContentView(layout);
//
//            ImageButton pay = (ImageButton) layout.findViewById(R.id.pay);
//            pay.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    if (!isLive) {
//                        //v1
//                        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    }
//                    if (isThree) {
//                        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    }
//                    if (isLive) {
//                        //v2
//                        Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    }
//                    Intent intent = new Intent(mContext, WXPayEntryActivity.class);
//                    startActivity(intent);
//                    mContext.finish();
//                }
//            });
//            ImageButton cancel = (ImageButton) layout.findViewById(R.id.cancel);
//            cancel.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    Multi.Moon_LEVE = Multi.Moon_LEVE1;
//                    mContext.finish();
//                }
//            });
//        } else {
//            dialog_pay_time.show();
//        }
//    }

//    private void sendUserDoData(String type, String oprationtime) {
//        //发起请求
//        DoInfo data = new DoInfo();
//        data.setUserName(util.getAndroidId(this));
//        data.setType(type);
//        data.setOperationTime(oprationtime);
//        String json = com.alibaba.fastjson.JSONObject.toJSONString(data);
//        String aesJson = aesUtils.encrypt(json);
//        RequestParams params = new RequestParams(Constant.USER_DO_INTERFACE);
//        params.setCacheMaxAge(0);//最大数据缓存时间
//        params.setConnectTimeout(5000);//连接超时时间
//        params.setCharset("UTF-8");
//        params.addQueryStringParameter("data", aesJson);
//
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//    }

//    Dialog dialog_level;

//    public void alertVipLevel() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_gold, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_level == null) {
//            dialog_level = new Dialog(mContext, R.style.Dialog);
//            dialog_level.show();
//            dialog_level.setCancelable(false);
//            Window window = dialog_level.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
////            layout.getBackground().setAlpha(150);
//            lp.width = ScreenTool.getWidth(this) / 7 * 4;
//            lp.height = ScreenTool.getHeight(this);
//            window.setAttributes(lp);
//            window.setContentView(layout);
//            ImageView pay_gold = (ImageView) layout.findViewById(R.id.pay_gold);
//            pay_gold.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_level.dismiss();
//                    Multi.Moon_LEVE = Multi.Moon_LEVE1;
//                    Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    mContext.startActivity(new Intent(mContext, WXPayEntryActivity.class));
//                    mContext.finish();
//                }
//            });
//            TextView vip_level_decription = (TextView) layout.findViewById(R.id.vip_level_decription);
//            if (VipTool.getUserVipType(mContext) == Multi.VIP_NOT_VIP_TYPE) {
//                vip_level_decription.setText("升级白银会员观看完整视频");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_SILVER_TYPE) {
//                vip_level_decription.setText("升级黄金会员观看完整视频");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_GOLD_TYPE) {
//                vip_level_decription.setText("升级白金会员观看完整视频");
//            } else if (VipTool.getUserVipType(mContext) == Multi.VIP_PLAT_NIUM_TYPE) {
//                vip_level_decription.setText("升级钻石会员观看完整视频");
//            } else {
//                vip_level_decription.setText("升级钻石会员观看完整视频");
//            }
//        } else {
//            dialog_level.show();
//        }
//    }

    private void initShowDialog() {
        Multi.isShowDialog = true;
        mContext.finish();
    }

    private void initBan() {
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_NOT_VIP_TYPE) {
            if (isLive && !isThree)
                centerImages.setVisibility(View.VISIBLE);
        } else if (vipType == Multi.VIP_SILVER_TYPE) {
            //白银
            if (isLive && !isThree) {
                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 8);
                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 7);
                centerImages.setVisibility(View.VISIBLE);
            }
        } else if (vipType == Multi.VIP_GOLD_TYPE) {
            //黄金
            if (isLive && !isThree) {
                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 7);
                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 6);
                centerImages.setVisibility(View.VISIBLE);
            }
        } else if (vipType == Multi.VIP_PLAT_NIUM_TYPE) {
            //白金
            if (isLive && !isThree) {
                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 6);
                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 5);
//                diamondText.setVisibility(View.VISIBLE);
                centerImages.setVisibility(View.VISIBLE);
            }
        } else if (vipType == Multi.VIP_DIAMOND_TYPE) {
            //钻石
            if (isLive && !isThree) {
                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 5);
                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 5);
//              diamondText.setVisibility(View.VISIBLE);
                centerImages.setVisibility(View.VISIBLE);
            }
        } else if (vipType == Multi.VIP_RED_DIAMOND_TYPE) {
            //红钻
            if (isLive && !isThree) {
                centerImages.setMinimumWidth(ScreenTool.getWidth(mContext) / 10 * 4);
                centerImages.setMinimumHeight(ScreenTool.getHeight(mContext) / 10 * 4);
                diamondText.setVisibility(View.VISIBLE);
                centerImages.setVisibility(View.VISIBLE);
            }
        } else if (vipType == Multi.VIP_CROWN_TYPE) {
            //皇冠
            if (isLive && !isThree) {
                centerImages.setVisibility(View.GONE);
            }
        }
    }
}
