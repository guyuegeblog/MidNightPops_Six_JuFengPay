package com.third.app.Constant;

import com.third.app.Model.PayPrice;
import com.third.app.Model.ThirdProductInfo;
import com.third.app.Model.VpnModel;
import com.third.app.Tool.FileTool;

import java.util.ArrayList;
import java.util.List;

/**
 * 接口存储
 */
public class Constant {

    public static final String APP_ID = "wx5dc64fe4e8c3ce27";
    /***
     * 试用文件路径
     */
    public static final String TV_SHIYONG_MP4 = FileTool.getSDCardPath() + "//" + ".night4mp4_six_8";
    public static final String TV_SHIYONG_MP4_FILE = ".tvmp5";
    public static final String TV_SHIYONG_MP4_ALL = Constant.TV_SHIYONG_MP4 + "/" + Constant.TV_SHIYONG_MP4_FILE;

    public static final String TV_SHIYONG_M3U8 = FileTool.getSDCardPath() + "//" + ".nightM3u8_six_8";
    public static final String TV_SHIYONG_FILE = ".tvm3u8ss";
    public static final String TV_SHIYONG_M3U8_ALL = Constant.TV_SHIYONG_M3U8 + "/" + Constant.TV_SHIYONG_FILE;

    public static final String TV_USER_FIRST_RIGISTER_DATETIME = FileTool.getSDCardPath() + "//" + ".nightpopFirst_six_11";
    public static final String TV_USER_LIVE_UNBIND = FileTool.getSDCardPath() + "//" + ".nightunbindsilver_six_9";

    //限制8个试看
//    public static final String TV_ShiKan_MP4 = FileTool.getSDCardPath() + "//" + ".nightSix_six_1009_8";
//    public static final String TV_ShiKan_MP4_FILE = ".sixx";
//    public static final String TV_ShiKan_MP4_ALL = Constant.TV_ShiKan_MP4 + "/" + Constant.TV_ShiKan_MP4_FILE;

    //用户充值次数
//    public static final String TV_USER_PAY_COUNT = FileTool.getSDCardPath() + "//" + ".nightuserpaycounts_six_12";//不能改动nightuserpaycounts_four_11
//    public static final String TV_PAY_COUNT_FILE = ".paycountsys";
//    public static final String TV_USER_PAY_ALL = Constant.TV_USER_PAY_COUNT + "/" + Constant.TV_PAY_COUNT_FILE;

    //用户第一次充值时间
    public static final String USER_FIRST_PAY = FileTool.getSDCardPath() + "//" + ".nightSix_six_first_pay";
    public static final String PAY_FILE_PATH = ".sixfirstpay";
    public static final String PAY_FILE = Constant.USER_FIRST_PAY + "/" + Constant.PAY_FILE_PATH;


    /***
     * uuid文件路径
     */
    public static final String UUID_AUTO_CREATE_DIRECTORY = FileTool.getSDCardPath() + "//.nightUuid_six_2";
    //二级目录
    public static final String UUID_AUTO_TWOFILE_DIRECTORY = "//.tvnight";
    public static final String UUID_AUTO_FILE_PATH = ".night";
    public static final int doDate = 30;//30秒
    public static final int LIVE_DATE = 1800;//30分钟 `
    public static final int Three_DATE = 1800;//30分钟
    public static final int FIRST_PAY_DOWNLOAD_TV = 40;//用户第一次付费45分钟后下载tv
    public static boolean isThanFourMinitesSplashStart = false;

    public static boolean barra_show = true;
    public static PayPrice payPrice;//(服务器获取停用)
    public static VpnModel vpnModel;
    public static ThirdProductInfo thirdProductInfo;
    public static List<String> urlString = new ArrayList<>();

}
