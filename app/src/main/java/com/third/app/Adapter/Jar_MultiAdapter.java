package com.third.app.Adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.third.app.Activity.MainActivity;
import com.third.app.Activity.SuperVideoDetailsActivity;
import com.third.app.Bean.Multi;
import com.third.app.Model.ComprehenSiveInfo;
import com.third.app.Model.VideoInfo;
import com.third.app.R;
import com.third.app.Tool.RandomTool;
import com.third.app.Tool.VipTool;
import com.third.app.View.T;

import java.util.List;

/**
 * Created by lin on 2016/11/24.
 */
public class Jar_MultiAdapter extends BaseMultiItemQuickAdapter<Multi, BaseViewHolder> {

    private MainActivity mContext;

    public Jar_MultiAdapter(List<Multi> data, MainActivity mContext) {
        super(data);
        addItemType(Multi.JAR_HEADER, R.layout.jarpanse_header);
        addItemType(Multi.JAR_ITEM, R.layout.jarpanse_item);
        addItemType(Multi.JAR_EMPTY, R.layout.jarpanse_empty);
        this.mContext = mContext;
    }


    @Override
    protected void convert(BaseViewHolder baseViewHolder, final Multi multi) {
        switch (baseViewHolder.getItemViewType()) {
            case Multi.JAR_HEADER:
                //....
                ((TextView) baseViewHolder.getView(R.id.video_count)).setText("当前片库约" + RandomTool.getRandomNumbers(4) + "部");
                break;
            case Multi.JAR_ITEM:
                ImageView imageView_Pic = (ImageView) baseViewHolder.getView(R.id.images);
                TextView textView_name = ((TextView) baseViewHolder.getView(R.id.name));
                TextView textView_seecount = ((TextView) baseViewHolder.getView(R.id.see_count));
                Glide.with(mContext).load(multi.getComprehenSiveInfo().getPic_heng()).placeholder(R.drawable.allloading).
                        error(R.drawable.allloading).into(imageView_Pic);
                textView_seecount.setText(RandomTool.getRandomNumbers(5));
                textView_name.setText(multi.getComprehenSiveInfo().getName());
                imageView_Pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getComprehenSiveInfo());
                    }
                });
                textView_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getComprehenSiveInfo());
                    }
                });
                textView_seecount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doPlay(multi.getComprehenSiveInfo());
                    }
                });


                break;
            case Multi.JAR_EMPTY:
                //....
                break;
        }
    }

    public void doPlay(ComprehenSiveInfo channeLnfo) {
        if (!VipTool.userIsLoginSucces(mContext)) {
            T.showTextToast(mContext, "请您重新启动App,完成自动登录后再播放视频!!");
            return;
        }
        int vipType = VipTool.getUserVipType(mContext);
        if (vipType == Multi.VIP_GOLD_TYPE || vipType== Multi.VIP_PLAT_NIUM_TYPE
                || vipType == Multi.VIP_DIAMOND_TYPE) {
            Intent intent = new Intent(mContext, SuperVideoDetailsActivity.class);
            Bundle bundle = new Bundle();
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setId("0");
            videoInfo.setAddress_hd(channeLnfo.getAddress_hd());
            videoInfo.setAddress_sd(channeLnfo.getAddress_sd());
            videoInfo.setName(channeLnfo.getName());
            videoInfo.setThreeVideo(false);
            Log.i("videoStr", "黄金 " + channeLnfo.getAddress_hd());
            videoInfo.setAddress_hd(videoInfo.getAddress_hd().replace("https","http"));
            videoInfo.setAddress_sd(videoInfo.getAddress_sd().replace("https","http"));
            bundle.putSerializable("videoInfo", videoInfo);
            intent.putExtras(bundle);
            intent.putExtra("isLive", false);
            mContext.startActivity(intent);
        } else {
            mContext.alertDialogPay();
        }
    }

//    Dialog dialog_pay_time;
//
//    public void alertVipPay() {
//        ScreenTool.setLight(mContext, 250);
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.dialog_gold, null);
//        //对话框
//        //一定是dialog,而非dialog.builder,不然不全屏的情况会发生
//        if (dialog_pay_time == null) {
//            dialog_pay_time = new Dialog(mContext, R.style.Dialog);
//            dialog_pay_time.show();
//            dialog_pay_time.setCancelable(true);
//            Window window = dialog_pay_time.getWindow();
//            window.getDecorView().setPadding(0, 0, 0, 0);
//            WindowManager.LayoutParams lp = window.getAttributes();
////            layout.getBackground().setAlpha(150);
//            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;//ScreenTool.getWidth(this) / 5 * 3;
//            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            window.setAttributes(lp);
//            window.setContentView(layout);
//            ImageView pay_gold = (ImageView) layout.findViewById(R.id.pay_gold);
//            pay_gold.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    dialog_pay_time.dismiss();
//                    Multi.Moon_LEVE = Multi.Moon_LEVE1;
//                    Multi.PAY_VIP_LEVEL = Multi.PAY_VIP1;
//                    mContext.startActivity(new Intent(mContext, WXPayEntryActivity.class));
//                }
//            });
//            TextView vip_level_decription = (TextView) layout.findViewById(R.id.vip_level_decription);
//            if (VipTool.getUserVipType() == Multi.VIP_NOT_VIP_TYPE) {
//                vip_level_decription.setText("成为白银会员享受更多...");
//            } else if (VipTool.getUserVipType() == Multi.VIP_SILVER_TYPE) {
//                vip_level_decription.setText("成为黄金会员享受更多...(充值结束后观看TV)");
//            } else if (VipTool.getUserVipType() == Multi.VIP_GOLD_TYPE) {
//                vip_level_decription.setText("成为白金会员享受更多...");
//            } else if (VipTool.getUserVipType() == Multi.VIP_PLAT_NIUM_TYPE) {
//                vip_level_decription.setText("成为钻石会员享受更多...");
//            } else {
//                vip_level_decription.setText("成为钻石会员享受更多...");
//            }
//        } else {
//            dialog_pay_time.show();
//        }
//    }
}
