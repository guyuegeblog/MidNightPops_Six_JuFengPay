package com.third.app.Net;

import android.text.TextUtils;

import com.third.app.Bean.Multi;

/**
 * Created by ASUS on 2016/12/6.
 */
public class NetInterface {

    //请求ip
    public static final String REQUEST_IP = "" + getIpAddress() + "";

    //请求头
    public static final String REQUEST_HEADER = "JSONString";

    //请求头
    public static final String REQUEST_LIVE_HEADER = "data";

    //获取服务器地址2的动态Ip地址 //地址二
    public static final String USER_GET_ADDRESS_2 = "http://60.205.56.207:8080/userserver/servlet/IPDistributeServlet";

    //注册相关接口 //地址一
    public static final String USER_REGISTER = "http://101.201.114.51:8080/userserver/servlet/NewRegisterServlet";

    //登录相关接口  //地址一
    public static final String USER_LOGIN = "http://101.201.114.51:8080/userserver/servlet/NewUserLoginServlet";

    //退出相关接口  //地址一
    public static final String USER_SINGOUT = "http://101.201.114.51:8080/userserver/servlet/NewUserOutServlet";


    //.....................

    //试看相关接口  //地址三
    public static final String USER_LOOK_DATA = "http://" + getIpAddress() + ":8080/userserver/servlet/XDHomePageServlet";

    //试看更多   //地址三
    public static final String USER_LOOK_TWO_DATA = "http://" + getIpAddress() + ":8080/userserver/servlet/NewHengBannerTwoServlet";

    //综合(三级和短视频)数据接口  //地址三
    public static final String USER_COMPREHENSIVE_DATA = "http://" + getIpAddress() + ":8080/userserver/servlet/PlayerAllInfoServlet";

    //获取一级分类信息  //地址三
    public static final String USER_FIRST_LEVEL_TYPE_DATA = "http://" + getIpAddress() + ":8080/userserver/servlet/NewOneTypeServlet";

    //获取二级分类信息  //地址三
    public static final String USER_TWO_LEVEL_TYPE_DATA = "http://" + getIpAddress() + ":8080/userserver/servlet/NewTwoTypeServlet";

    //获取直播信息
    public static final String USER_LIVE_INFO_DATA = "http://139.129.97.33:8080/LiveVideo/VideoServlet.shtml";

    //支付宝订单查询接口
    //public static final String USER_QUERY_ZHIFUBAO = "http://101.251.251.115:8080/userserver/servlet/ZfbOrderServlet";


    //微信订单查询接口
    //public static final String USER_QUERY_WEIXIN = "http://101.251.251.115:8080/userserver/servlet/WxOrderServlet";

    //支付宝异步通知接口
    public static final String USER_NOTIFY_ZFB = "http://101.201.114.51:8080/userserver/notify_url.jsp";


    //微信异步通知接口(异步通知)
    //http://101.201.114.51:8080/userserver/servlet/JuHeZFServlet(聚合停用)
    public static final String USER_NOTIFY_WEIXIN = "http://101.201.114.51:8080/userserver/servlet/WxPayServlet";

    //中青支付同步通知
    public static final String ZHONGQIN_NOTIFY_WEIXIN = "http://101.201.114.51:8080/userserver/servlet/ZhongQingZFServlet";

    //获取v2支付价格(暂时停用)
    //http://101.201.114.51:8080/userserver/servlet/AreaPriceServlet?
    public static final String USER_QUERY_PAY_PRICE = "http://101.201.114.51:8080/userserver/servlet/AreaPriceServlets";


    //充值会员接口(-VIP1)（地址一）（短视频充值）(验证)
    public static final String USER_QUERY_PAY_RESULT_VIP1 = "http://101.201.114.51:8080/userserver/servlet/NewVipOrderServlet?";

    //充值会员接口(-VIP2)（地址一）（直播充值）（验证）
    public static final String USER_QUERY_PAY_RESULT_VIP2 = "http://101.201.114.51:8080/userserver/servlet/NewVipTwoServlet";


    //vpn下载接口
    public static final String USER_VPN_DOWNLOAD = "http://101.201.114.51:8080/userserver/servlet/DownVpnServlet";

    /***
     * tv节目单接口
     */
    public static final String TV_JIEMU_LIST = "http://139.129.97.33:8080/LiveVideo/ProgramServlet.shtml";

    //多轮产品下载相关（开关接口）//地址一
    public static final String USER_THIRD_PRODUCT = "http://101.201.114.51:8080/userserver/servlet/LXManyUrlServlet";


    public static String getIpAddress() {
        if (TextUtils.isEmpty(Multi.Ip_Adress)) {
            return Multi.getDynamicIp();
        } else {
            return Multi.Ip_Adress;
        }
    }


}
