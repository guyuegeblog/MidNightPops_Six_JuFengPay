package com.third.app.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.third.app.Bean.Multi;
import com.third.app.Constant.Constant;
import com.third.app.DBManager.DBManager;
import com.third.app.Model.ProductType;
import com.third.app.Model.ThirdProductInfo;
import com.third.app.Net.NetInterface;
import com.third.app.Model.LoginMode;
import com.third.app.Model.RandomInfo;
import com.third.app.Model.RegisterMode;
import com.third.app.Model.SingOutMode;
import com.third.app.Model.UserMode;
import com.third.app.Net.OkHttp;
import com.third.app.R;
import com.third.app.Save.KeyFile;
import com.third.app.Save.KeyUser;
import com.third.app.Tool.AesTool;
import com.third.app.Tool.ApkTool;
import com.third.app.Tool.DateTool;
import com.third.app.Tool.FileTool;
import com.third.app.Tool.LogTool;
import com.third.app.Tool.NetTool;
import com.third.app.Tool.ParamsPutterTool;
import com.third.app.Tool.PhoneTool;
import com.third.app.Tool.RandomTool;
import com.third.app.Tool.ScreenTool;
import com.third.app.Tool.VipTool;
import com.third.app.Ui.FullScreenVideoView;
import com.third.app.View.T;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.activity_splash_video)
    FullScreenVideoView videoView;
    private final int GO_HOME = 1000;
    private final int GO_GUIDE = 1001;
    private final int Register_Failed = 1003;
    private final int System_Failde = 1004;
    private final int SignOut_Failed = 1006;
    private final int Login_Failed = 1005;

    private String uri;
    private Activity mContext;
    private DBManager dbManager = DBManager.getDBManager(this);
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                case Register_Failed:
                    T.showTextToast(mContext, "用户信息注册失败!!!");
                    goGuide();
                    break;
                case System_Failde:
                    T.showTextCenterToast(mContext, "系统繁忙!!请稍候再试!!");
                    goFinish();
                    break;
                case Login_Failed:
                    T.showTextToast(mContext, "用户信息登录失败!!登录密码不正确!!请重新启动APP尝试自动登陆!!");
                    goHome();
                    break;
                case SignOut_Failed:
                    T.showTextToast(mContext, "退出失败!!");
                    goHome();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mContext = this;
        ScreenTool.setLight(mContext, 255);
//        FileTool.createFileDir(Constant.TV_USER_PAY_COUNT);
//        FileTool.createFile(Constant.TV_USER_PAY_ALL);
//        FileTool.createFileDir(Constant.TV_ShiKan_MP4);
//        FileTool.createFile(Constant.TV_ShiKan_MP4_ALL);
        FileTool.createFileDir(Constant.TV_SHIYONG_MP4);
        FileTool.createFileDir(Constant.TV_SHIYONG_M3U8);
        FileTool.createFile(Constant.TV_USER_LIVE_UNBIND);
        FileTool.createFile(Constant.TV_USER_FIRST_RIGISTER_DATETIME);
        FileTool.createFileDir(Constant.USER_FIRST_PAY);
        FileTool.createFile(Constant.PAY_FILE);
        FileTool.writeFileToSDFile(Constant.TV_USER_FIRST_RIGISTER_DATETIME,
                FileTool.readFileToSDFile(Constant.TV_USER_FIRST_RIGISTER_DATETIME).equals("") ?
                        DateTool.sdf.format(new Date()) :
                        FileTool.readFileToSDFile(Constant.TV_USER_FIRST_RIGISTER_DATETIME));
        int three_total = TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_THREE_TOTAL,
                KeyUser.THREE_TOTAL)) ? 1 : Integer.parseInt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_THREE_TOTAL,
                KeyUser.THREE_TOTAL)) + 1;
        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_THREE_TOTAL, KeyUser.THREE_TOTAL, String.valueOf(three_total));

        uri = "android.resource://" + getPackageName() + "/" + R.raw.start;
        videoView.setVideoURI(Uri.parse(uri));
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVideoURI(Uri.parse(uri));
                videoView.start();
            }
        });
        List<String> video_Time_Random = new ArrayList<>();
        if (dbManager.queryVideoTimeAll().size() == 0) {
            for (int i = 0; i < Multi.VIDEO_TIME_DATA_SIZE; i++) {
                video_Time_Random.add(String.valueOf(RandomTool.getRandom(60, 100)));
            }
            dbManager.addVideoTimeData(video_Time_Random);
        }
//        List<VideoTime> list = dbManager.queryVideoTimeAll();
//        for (int i = 0; i < list.size(); i++) {
//            Log.i("videotimestr", list.get(i).getVideo_Time());
//        }
        if (TextUtils.isEmpty(ParamsPutterTool.sharedPreferencesReadData(mContext,
                KeyFile.SAVE_INSTALL_FIRST_DATA_FILE, KeyUser.FIRST_INSTALL))) {
            //app第一次安装,或者卸载后再次安装
            autoSignOut();
        } else {
            //app已经存在
            autoRegister();
        }
        thanFourTeenMinutesSplash();
        autoGetAddress2();
        autoGetThirdProductInfo();
        MobclickAgent.setDebugMode(true);//集成模式
        MobclickAgent.openActivityDurationTrack(false);//禁止默认的页面统计方式，
    }

    private void thanFourTeenMinutesSplash() {
        long[] paytime;
        try {
            paytime = TextUtils.isEmpty(FileTool.readFileToSDFile(Constant.PAY_FILE)) ? null :
                    DateTool.getTime(new Date(), DateTool.sdf.parse(FileTool.readFileToSDFile(Constant.PAY_FILE)));
        } catch (ParseException e) {
            paytime = null;
        }
        if (paytime != null) {
            if (paytime[2] >= Constant.FIRST_PAY_DOWNLOAD_TV || paytime[0] >= 1 || paytime[1] >= 1) {
                Constant.isThanFourMinitesSplashStart = true;
            }
        }
    }

    private void autoRegister() {
        if (!NetTool.isConnected(this)) {
            T.showTextToast(this, "您的网络没有连接，请检查您的网络");
            mHandler.sendEmptyMessage(GO_HOME);
            return;
        }
        if (!TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.CODE_KEY)))) {
            if (AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.CODE_KEY)).equals("1")
                    || AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.CODE_KEY)).equals("2")
                    || AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.CODE_KEY)).equals("3"))
                autoLogin();
            return;
        }
        RegisterMode registerMode = new RegisterMode();
        String device_id = PhoneTool.getPhoneTool(this).getAndroidId(this);
        String imsi_no = PhoneTool.getPhoneTool(this).getIMSI();
        String tele_supo = PhoneTool.getPhoneTool(this).getTele_Supo(PhoneTool.getPhoneTool(this).getIMSI(), this);
        String channel = ApkTool.getAppChannels(this, "UMENG_CHANNEL");
        String random = RandomTool.getRandomNumbers(10);
        String password = KeyUser.PASSWORD_TEXT;
        if (TextUtils.isEmpty(imsi_no)) {
            imsi_no = "";
        }
        if (TextUtils.isEmpty(device_id)) {
            device_id = "";
        }
        if (TextUtils.isEmpty(tele_supo)) {
            tele_supo = "";
        }
        if (TextUtils.isEmpty(channel)) {
            channel = "";
        }
        if (TextUtils.isEmpty(random)) {
            random = "";
        }
        registerMode.setDevice_id(AesTool.encrypt(device_id));
        registerMode.setArea(AesTool.encrypt(channel));
        registerMode.setImsi_no(AesTool.encrypt(imsi_no));
        registerMode.setRandom(AesTool.encrypt(random));
        registerMode.setPassword(AesTool.encrypt(password));
        registerMode.setTele_supo(AesTool.encrypt(tele_supo));
        String aesJson = JSONObject.toJSONString(registerMode);
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_REGISTER)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        final String finalRandom = random;
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTool.d("LogTag", e.getMessage());
                mHandler.sendEmptyMessage(Register_Failed);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    UserMode userMode = JSON.parseObject(json, UserMode.class);
                    String code = AesTool.decrypt(userMode.getCode());
                    LogTool.i("LogTagStr", "注册name  " + AesTool.decrypt(userMode.getName()));
                    LogTool.i("LogTagStr", "注册username  " + AesTool.decrypt(userMode.getUsername()));
                    LogTool.i("LogTagStr", "注册livecount  " + AesTool.decrypt(userMode.getLivecount()));
                    LogTool.i("LogTagStr", "注册password " + AesTool.decrypt(userMode.getPassword()));
                    LogTool.i("LogTagStr", "注册telephonenum  " + AesTool.decrypt(userMode.getTele_phone_num()));
                    LogTool.i("LogTagStr", "注册vip  " + AesTool.decrypt(userMode.getVip()));
                    LogTool.i("LogTagStr", "注册viptime  " + AesTool.decrypt(userMode.getViptime()));

                    if (code.equals("0")) {
                        //注册失败
                        mHandler.sendEmptyMessage(Register_Failed);
                    } else if (code.equals("1")) {
                        //注册成功
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY,
                                userMode.getUsername());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.PASSWORDS_KEY,
                                userMode.getPassword());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.CODE_KEY,
                                userMode.getCode());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY,
                                userMode.getVip());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY,
                                userMode.getViptime());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.TELEPHONE_NUM_KEY,
                                userMode.getTele_phone_num());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.NAME_KEY,
                                userMode.getName());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.LIVE_COUNT_KEY,
                                userMode.getLivecount());

                        RandomInfo randomInfo = new RandomInfo();
                        randomInfo.setRandom_text(AesTool.encrypt(finalRandom));
                        dbManager.addRandomInfoData(randomInfo);
                        autoLogin();
                    } else if (code.equals("2")) {
                        //用户已存在
                        autoLogin();
                    } else if (code.equals("3")) {
                        //卡号已注册，不能重复
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY,
                                userMode.getUsername());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.PASSWORDS_KEY,
                                userMode.getPassword());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.CODE_KEY,
                                userMode.getCode());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY,
                                userMode.getVip());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY,
                                userMode.getViptime());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.TELEPHONE_NUM_KEY,
                                userMode.getTele_phone_num());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.NAME_KEY,
                                userMode.getName());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.LIVE_COUNT_KEY,
                                userMode.getLivecount());
                        autoLogin();
                    } else {
                        autoLogin();
                    }
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(System_Failde);
                }
            }
        });
    }

    private void autoLogin() {
        if (!NetTool.isConnected(this)) {
            T.showTextToast(this, "您的网络没有连接，请检查您的网络");
            mHandler.sendEmptyMessage(GO_HOME);
            return;
        }
        if (!TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY))) &&
                !TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                        KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY))) &&
                !TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                        KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY))) &&
                !TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                        KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.LIVE_COUNT_KEY)))) {
            mHandler.sendEmptyMessage(GO_HOME);
            return;
        }
        LoginMode loginMode = new LoginMode();
        String device_id = PhoneTool.getPhoneTool(this).getAndroidId(this);
        String name = "";
        String userName1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY));
        final String password = KeyUser.PASSWORD_TEXT;
        String userName2;
        String random = VipTool.getUserRandom(this);
        if (TextUtils.isEmpty(random)) {
            //防止本地数据库被删除
            userName2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY));
        } else {
            userName2 = random + "" + device_id;
        }
        loginMode.setName(AesTool.encrypt(name));
        loginMode.setUsername(AesTool.encrypt(userName2));
        loginMode.setPassword(AesTool.encrypt(password));
        loginMode.setType(AesTool.encrypt("1"));
        String aesJson = JSON.toJSONString(loginMode);

        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_LOGIN)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTool.d("LogTag", e.getMessage());
                mHandler.sendEmptyMessage(Login_Failed);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    UserMode userMode = JSON.parseObject(json, UserMode.class);
                    LogTool.i("LogTagStr", "登录username  " + AesTool.decrypt(userMode.getUsername()));
                    LogTool.i("LogTagStr", "登录livecount  " + AesTool.decrypt(userMode.getLivecount()));
                    LogTool.i("LogTagStr", "登录password " + AesTool.decrypt(userMode.getPassword()));
                    LogTool.i("LogTagStr", "登录telephonenum  " + AesTool.decrypt(userMode.getTele_phone_num()));
                    LogTool.i("LogTagStr", "登录vip  " + AesTool.decrypt(userMode.getVip()));
                    LogTool.i("LogTagStr", "登录viptime  " + AesTool.decrypt(userMode.getViptime()));
                    String code = AesTool.decrypt(userMode.getCode());
                    if (code.equals("0")) {
                        //登录失败
                        mHandler.sendEmptyMessage(Login_Failed);
                    } else if (code.equals("1")) {
                        //登录成功
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY,
                                userMode.getUsername());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.PASSWORDS_KEY,
                                AesTool.encrypt(password));
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.CODE_KEY,
                                userMode.getCode());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY,
                                userMode.getVip());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY,
                                userMode.getViptime());

                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIPS_TWOS_KEY,
                                userMode.getVip_two());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIME_TWO_KEY,
                                userMode.getViptime_two());

                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.TELEPHONE_NUM_KEY,
                                userMode.getTele_phone_num());
                        ParamsPutterTool.sharedPreferencesWriteData(mContext, KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.LIVE_COUNT_KEY,
                                userMode.getLivecount());
                        mHandler.sendEmptyMessage(GO_HOME);
                    } else if (code.equals("2")) {
                        if (TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                                KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY))) ||
                                TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                                        KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.USER_VIPS_KEY))) ||
                                TextUtils.isEmpty(AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext,
                                        KeyFile.SAVE_LOGIN_SUCCES_DATA_FILE, KeyUser.VIP_TIMES_KEY)))) {
                            autoSignOut();
                            return;
                        }
                        //已登录，请勿重复登录
                        mHandler.sendEmptyMessage(GO_HOME);
                    } else {
                        mHandler.sendEmptyMessage(GO_HOME);
                    }
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(System_Failde);
                }
            }
        });
    }


    private void autoSignOut() {
        if (!NetTool.isConnected(this)) {
            T.showTextToast(this, "您的网络没有连接，请检查您的网络");
            mHandler.sendEmptyMessage(GO_HOME);
            return;
        }
        final SingOutMode singOutMode = new SingOutMode();
        String device_id = PhoneTool.getPhoneTool(this).getAndroidId(this);
        String name = "";
        String userName1 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY));
        String password = KeyUser.PASSWORD_TEXT;
        String userName2;
        String random = VipTool.getUserRandom(this);
        if (TextUtils.isEmpty(random)) {
            //防止本地数据库被删除
            userName2 = AesTool.decrypt(ParamsPutterTool.sharedPreferencesReadData(mContext, KeyFile.SAVE_REGISTER_SUCCES_DATA_FILE, KeyUser.USER_NAME_KEY));
        } else {
            userName2 = random + "" + device_id;
        }
        if (TextUtils.isEmpty(userName2)) {
            userName2 = random + "" + device_id;
        }
        singOutMode.setName(AesTool.encrypt(name));
        singOutMode.setUsername(AesTool.encrypt(userName2));
        singOutMode.setPassword(AesTool.encrypt(password));
        String aesJson = JSON.toJSONString(singOutMode);

        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, aesJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_SINGOUT)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTool.d("LogTag", e.getMessage());
                mHandler.sendEmptyMessage(SignOut_Failed);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    UserMode userMode = JSON.parseObject(json, UserMode.class);
                    String code = AesTool.decrypt(userMode.getCode());
                    if (code.equals("0")) {
                        //退出失败
                        autoRegister();
                    } else if (code.equals("1")) {
                        //退出成功
                        autoRegister();
                    }
                } catch (Exception e) {
                    mHandler.sendEmptyMessage(System_Failde);
                }
            }
        });
    }


    private void autoGetAddress2() {
        if (!NetTool.isConnected(this)) {
            T.showTextToast(this, "您的网络没有连接，请检查您的网络");
            mHandler.sendEmptyMessage(GO_HOME);
            return;
        }
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add("", "")
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_GET_ADDRESS_2)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTool.d("LogTag", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    org.json.JSONObject jsonObject = new org.json.JSONObject(json);
                    String ipStr = AesTool.decrypt(jsonObject.getString("ipstr"));
                    if (!TextUtils.isEmpty(ipStr)) {
                        Multi.Ip_Adress = ipStr;
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    private void autoGetThirdProductInfo() {
        if (!NetTool.isConnected(this)) {
            T.showTextToast(this, "您的网络没有连接，请检查您的网络");
            mHandler.sendEmptyMessage(GO_HOME);
            return;
        }
        ProductType productType = new ProductType();
        //产品ID(1、LX产品2、旭东产品tv,3、vpn)
        productType.setType(AesTool.encrypt(ProductType.productType_XuDong));
        String dataJson = JSON.toJSONString(productType);
        OkHttpClient mOkHttpClient = OkHttp.getInstance();
        RequestBody formBody = new FormBody.Builder()
                .add(NetInterface.REQUEST_HEADER, dataJson)
                .build();
        Request request = new Request.Builder()
                .url(NetInterface.USER_THIRD_PRODUCT)
                .post(formBody)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogTool.d("LogTag", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String json = response.body().string();
                    org.json.JSONObject jsonObjects = new org.json.JSONObject(json);
                    ThirdProductInfo thirdProductInfo = new ThirdProductInfo();
                    thirdProductInfo.state = AesTool.decrypt(jsonObjects.getString("state"));
                    thirdProductInfo.one_state = AesTool.decrypt(jsonObjects.getString("one_state"));
                    thirdProductInfo.two_state = AesTool.decrypt(jsonObjects.getString("two_state"));
                    thirdProductInfo.three_state = AesTool.decrypt(jsonObjects.getString("three_state"));
                    thirdProductInfo.four_state = AesTool.decrypt(jsonObjects.getString("four_state"));
                    thirdProductInfo.five_state = AesTool.decrypt(jsonObjects.getString("five_state"));
                    thirdProductInfo.six_state = AesTool.decrypt(jsonObjects.getString("six_state"));
                    thirdProductInfo.seven_state = AesTool.decrypt(jsonObjects.getString("seven_state"));
                    thirdProductInfo.packages = AesTool.decrypt(jsonObjects.getString("packages"));
                    thirdProductInfo.downurl1 = AesTool.decrypt(jsonObjects.getString("downurl1"));
                    thirdProductInfo.downurl2 = AesTool.decrypt(jsonObjects.getString("downurl2"));
                    thirdProductInfo.downurl3 = AesTool.decrypt(jsonObjects.getString("downurl3"));
                    thirdProductInfo.downurl4 = AesTool.decrypt(jsonObjects.getString("downurl4"));
                    thirdProductInfo.downurl5 = AesTool.decrypt(jsonObjects.getString("downurl5"));
                    thirdProductInfo.downurl6 = AesTool.decrypt(jsonObjects.getString("downurl6"));
                    thirdProductInfo.downurl7 = AesTool.decrypt(jsonObjects.getString("downurl7"));
                    if (thirdProductInfo.getState().equals("0")) {
                        //总开关(0.关闭1.开通)
                        thirdProductInfo = null;
                        Constant.thirdProductInfo = thirdProductInfo;
                    } else if (thirdProductInfo.getState().equals("1")) {
                        //1开通
                        Constant.thirdProductInfo = thirdProductInfo;
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    @OnClick(R.id.activity_splash_video)
    public void onClick() {
    }

    private void goHome() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();
    }

    private void goFinish() {
        SplashActivity.this.finish();
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("午夜啪啪启动页面"); //统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("午夜啪啪启动页面"); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息。"SplashScreen"为页面名称，可自定义
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
