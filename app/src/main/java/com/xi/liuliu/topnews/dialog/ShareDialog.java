package com.xi.liuliu.topnews.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xi.liuliu.topnews.R;
import com.xi.liuliu.topnews.bean.NewsItem;
import com.xi.liuliu.topnews.constants.Constants;
import com.xi.liuliu.topnews.event.WeiboShareEvent;
import com.xi.liuliu.topnews.utils.BitmapUtil;
import com.xi.liuliu.topnews.utils.PackageUtil;
import com.xi.liuliu.topnews.utils.ToastUtil;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by liuliu on 2017/6/22.
 */

public class ShareDialog implements View.OnClickListener {
    private static final String TAG = "ShareDialog";
    private Context mContext;
    private DialogView mDialogView;
    private TextView mShareCancle;
    private Bitmap mShareThum;
    private NewsItem mNewsItem;
    private LinearLayout mWeixinCircleLl;
    private ImageView mCircleBtn;
    private TextView mCircleTxt;
    private LinearLayout mWeixinFriendLl;
    private ImageView mFriendBtn;
    private TextView mFriendTxt;
    private LinearLayout mQQLl;
    private ImageView mQQBtn;
    private TextView mQQTxt;
    private LinearLayout mQZoneLl;
    private ImageView mQZoneBtn;
    private TextView mQZoneTxt;
    private LinearLayout mWeiboLl;
    private ImageView mWeiboBtn;
    private TextView mWeiboTxt;

    public ShareDialog(Context context, NewsItem newsItem, Bitmap shareThum) {
        this.mContext = context;
        this.mNewsItem = newsItem;
        this.mShareThum = shareThum;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_share, null);
        mWeixinCircleLl = (LinearLayout) view.findViewById(R.id.share_ll_weixin_circle);
        mCircleBtn = (ImageView) view.findViewById(R.id.share_circle_btn);
        mCircleTxt = (TextView) view.findViewById(R.id.share_circle_txt);
        mWeixinCircleLl.setOnClickListener(this);
        mWeixinFriendLl = (LinearLayout) view.findViewById(R.id.share_ll_weixin_friend);
        mFriendBtn = (ImageView) view.findViewById(R.id.share_friend_btn);
        mFriendTxt = (TextView) view.findViewById(R.id.share_friend_txt);
        mWeixinFriendLl.setOnClickListener(this);
        mQQLl = (LinearLayout) view.findViewById(R.id.share_ll_qq);
        mQQBtn = (ImageView) view.findViewById(R.id.share_qq_btn);
        mQQTxt = (TextView) view.findViewById(R.id.share_qq_txt);
        mQQLl.setOnClickListener(this);
        mQZoneLl = (LinearLayout) view.findViewById(R.id.share_ll_qzone);
        mQZoneBtn = (ImageView) view.findViewById(R.id.share_qzone_btn);
        mQZoneTxt = (TextView) view.findViewById(R.id.share_qzone_txt);
        mQZoneLl.setOnClickListener(this);
        mWeiboLl = (LinearLayout) view.findViewById(R.id.share_ll_weibo);
        mWeiboBtn = (ImageView) view.findViewById(R.id.share_weibo_btn);
        mWeiboTxt = (TextView) view.findViewById(R.id.share_weibo_txt);
        mWeiboLl.setOnClickListener(this);
        mShareCancle = (TextView) view.findViewById(R.id.share_cancle);
        mShareCancle.setOnClickListener(this);
        mDialogView = new DialogView(mContext, view, R.style.share_dialog_animation);
        mDialogView.setGravity(Gravity.BOTTOM);
        mDialogView.setFullWidth(true);
        mDialogView.setCanceledOnTouchOutside(true);
        mDialogView.setDimBehind(true);

    }

    public void show() {
        if (mDialogView != null) {
            mDialogView.showDialog();
        }
    }

    public void dismiss() {
        if (mDialogView != null) {
            mDialogView.dismissDialog();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_ll_weixin_circle:
                shareToWX(SendMessageToWX.Req.WXSceneTimeline);
                break;
            case R.id.share_ll_weixin_friend:
                shareToWX(SendMessageToWX.Req.WXSceneSession);
                break;
            case R.id.share_ll_qq:
                shareToQQ();
                break;
            case R.id.share_ll_qzone:
                shareToQZone();
                break;
            case R.id.share_ll_weibo:
                shareToWeibo();
                break;
            case R.id.share_cancle:
                dismiss();
                break;
            default:
        }
    }

    private void shareToWX(int scene) {
        IWXAPI api = WXAPIFactory.createWXAPI(mContext, Constants.WEI_XIN_APP_ID, true);
        api.registerApp(Constants.WEI_XIN_APP_ID);
        if (api.isWXAppInstalled()) {
            if (mNewsItem != null && mShareThum != null) {
                WXWebpageObject webpageObject = new WXWebpageObject();
                //网页URL
                webpageObject.webpageUrl = mNewsItem.getUrl();
                WXMediaMessage mediaMessage = new WXMediaMessage(webpageObject);
                //网页标题
                mediaMessage.title = mNewsItem.getTitle();
                //缩略图
                Bitmap thumbBmp = Bitmap.createScaledBitmap(mShareThum, 50, 30, true);
                mediaMessage.thumbData = BitmapUtil.bmpToByteArray(thumbBmp, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = mediaMessage;
                req.scene = scene;
                boolean result = api.sendReq(req);
                Log.i(TAG, "share to circle:" + result);
            } else {
                throw new RuntimeException("ShareDialog 133 line,newsItem or shareThum is null");
            }
        } else {
            ToastUtil.toastInCenter(mContext, R.string.share_dialog_weixin_not_installed);
        }
        dismiss();
    }

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private void shareToQQ() {
        if (PackageUtil.isQQClientInstalled(mContext)) {
            Tencent tencent = Tencent.createInstance(Constants.QQ_APP_ID, mContext);
            Bundle params = new Bundle();
            params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
            params.putString(QQShare.SHARE_TO_QQ_TITLE, mNewsItem.getTitle());
            params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mNewsItem.getUrl());
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, mNewsItem.getThumbnailPic());
            params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "TopNews");
            tencent.shareToQQ((Activity) mContext, params, new IUiListener() {
                @Override
                public void onComplete(Object o) {
                }

                @Override
                public void onError(UiError uiError) {
                    Log.i(TAG, "ShareToQQ:" + uiError.errorMessage + " " + uiError.errorCode);
                }

                @Override
                public void onCancel() {

                }
            });
            //3秒后dialog消失
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 3000);

        } else {
            ToastUtil.toastInCenter(mContext, R.string.share_dialog_qq_not_installed);
            dismiss();
        }

    }

    private void shareToQZone() {
        if (PackageUtil.isQQClientInstalled(mContext)) {
            Tencent tencent = Tencent.createInstance(Constants.QQ_APP_ID, mContext);
            Bundle params = new Bundle();
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mNewsItem.getTitle());
            params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mNewsItem.getUrl());
            ArrayList<String> arrayList = new ArrayList<>(1);
            arrayList.add(mNewsItem.getThumbnailPic());
            params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList);
            tencent.shareToQzone((Activity) mContext, params, new IUiListener() {
                @Override
                public void onComplete(Object o) {

                }

                @Override
                public void onError(UiError uiError) {
                    Log.i(TAG, "shareToQZone:" + uiError.errorMessage + " " + uiError.errorCode);
                }

                @Override
                public void onCancel() {

                }
            });
            //3秒后dialog消失
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                }
            }, 3000);
        } else {
            ToastUtil.toastInCenter(mContext, R.string.share_dialog_qq_not_installed);
            dismiss();
        }
    }


    private void shareToWeibo() {
        EventBus.getDefault().post(new WeiboShareEvent());
        dismiss();
    }
}
