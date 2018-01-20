package cn.liangyongxiong.cordova.plugin.admob.tencent;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by shion on 2017/12/15.
 */

public class TencentAdMobSplashFragment extends DialogFragment implements SplashADListener {
    public static final String APPID = "APPID";//应用id
    public static final String SplashPosID = "SplashPosID";
    public static final String DELAY = "delay";
    public static final String IMAGE = "image";
    public static final String HEIGHT = "height";
    private String appId = "";//应用id
    private String splashPosID = "";
    private int delay;
    private String image;
    private int height;

    private SplashAD splashAD;
    private ViewGroup container;
    private TextView skipView;
    private ImageView bgImageView;
    private static final String SKIP_TEXT = "跳过 %d 秒";
    private Context mContext;

    public static TencentAdMobSplashFragment newInstance(String appid, String bannerPosID, int delay, String image, int height) {
        TencentAdMobSplashFragment fragment = new TencentAdMobSplashFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APPID, appid);
        bundle.putString(SplashPosID, bannerPosID);
        bundle.putInt(DELAY, delay);
        bundle.putString(IMAGE, image);
        bundle.putInt(HEIGHT, height);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        appId = getArguments().getString(APPID);
        splashPosID = getArguments().getString(SplashPosID);
        delay = getArguments().getInt(DELAY);
        image = getArguments().getString(IMAGE);
        height = getArguments().getInt(HEIGHT);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = android.R.style.Animation_Dialog;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
                    return true;
                }
                return false;
            }
        });


        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup c, Bundle savedInstanceState) {
        RelativeLayout relativeLayout = new RelativeLayout(mContext);
        relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //1.背景图片
        bgImageView = new ImageView(mContext);
        bgImageView.setAdjustViewBounds(true);
        bgImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bgImageView.setBackgroundColor(Color.WHITE);
        bgImageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(bgImageView);
        //2.广告容器+底部的Logo
        LinearLayout linearLayout = new LinearLayout(mContext);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(linearLayout);

        this.container = new FrameLayout(mContext);
        this.container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
        linearLayout.addView(this.container);

        ImageView bottomImageView = new ImageView(mContext);
        bottomImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bottomImageView.setAdjustViewBounds(true);
        bottomImageView.setImageBitmap(getImageFromAssets(image));
        bottomImageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(mContext, height)));
        linearLayout.addView(bottomImageView);

        //第三部分 计时器
        skipView = new TextView(mContext);
        RelativeLayout.LayoutParams skipLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        skipLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        skipLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        skipLayoutParams.topMargin = dp2px(mContext, 16);
        skipLayoutParams.rightMargin = dp2px(mContext, 16);
        skipView.setLayoutParams(skipLayoutParams);
        skipView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        skipView.setTextColor(Color.WHITE);
        skipView.setGravity(Gravity.CENTER);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#80000000"));
        drawable.setCornerRadius(dp2px(mContext, 45));
        drawable.setStroke(dp2px(mContext, 1), Color.WHITE);
        skipView.setBackground(drawable);
        skipView.setPadding(dp2px(mContext, 9), dp2px(mContext, 5), dp2px(mContext, 9), dp2px(mContext, 5));
        skipView.setVisibility(View.INVISIBLE);
        relativeLayout.addView(skipView);
        return relativeLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 如果targetSDKVersion >= 23，就要申请好权限。如果您的App没有适配到Android6.0（即targetSDKVersion < 23），那么只需要在这里直接调用fetchSplashAD接口。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
            fetchSplashAD((Activity) mContext, container, skipView, appId, splashPosID, this, delay * 1000);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkAndRequestPermission() {
        List<String> lackedPermission = new ArrayList<String>();
        if (!(mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (!(mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!(mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // 权限都已经有了，那么直接调用SDK
        if (lackedPermission.size() == 0) {
            fetchSplashAD((Activity) mContext, container, skipView, appId, splashPosID, this, delay * 1000);
        } else {
            // 请求所缺少的权限，在onRequestPermissionsResult中再看是否获得权限，如果获得权限就可以调用SDK，否则不要调用SDK。
            String[] requestPermissions = new String[lackedPermission.size()];
            lackedPermission.toArray(requestPermissions);
            this.requestPermissions(requestPermissions, 1024);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1024 && hasAllPermissionsGranted(grantResults)) {
            fetchSplashAD((Activity) mContext, container, skipView, appId, splashPosID, this, delay * 1000);
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(mContext.getApplicationContext(), "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            startActivityForResult(intent, 0x100);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x100) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                List<String> lackedPermission = new ArrayList<String>();
                if (!(mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)) {
                    lackedPermission.add(Manifest.permission.READ_PHONE_STATE);
                }

                if (!(mContext.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    lackedPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (!(mContext.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
                    lackedPermission.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }

                // 权限都已经有了，那么直接调用SDK
                if (lackedPermission.size() == 0) {
                    fetchSplashAD((Activity) mContext, container, skipView, appId, splashPosID, this, delay * 1000);
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "应用缺少必要的权限!", Toast.LENGTH_LONG).show();
                    dismissAllowingStateLoss();
                }
            }
        }
    }

    private boolean hasAllPermissionsGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void fetchSplashAD(Activity activity, ViewGroup adContainer, View skipContainer,
                               String appId, String posId, SplashADListener adListener, int fetchDelay) {
        splashAD = new SplashAD(activity, adContainer, skipContainer, appId, posId, adListener, fetchDelay);
    }

    @Override
    public void onADPresent() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "onSuccess");
            sendUpdate(obj, true);
        } catch (Exception e) {
        }

        TranslateAnimation t3 = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0);
        t3.setFillAfter(true);
        t3.setDuration(150);
        t3.setInterpolator(new LinearInterpolator());
        container.startAnimation(t3);

    }

    @Override
    public void onADClicked() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "onClick");
            sendUpdate(obj, true);
        } catch (Exception e) {
        }
    }

    /**
     * 倒计时回调，返回广告还将被展示的剩余时间。
     * 通过这个接口，开发者可以自行决定是否显示倒计时提示，或者还剩几秒的时候显示倒计时
     *
     * @param millisUntilFinished 剩余毫秒数
     */
    @Override
    public void onADTick(long millisUntilFinished) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "onTick");
            obj.put("milliseconds", millisUntilFinished);
            sendUpdate(obj, true);
        } catch (Exception e) {
        }

        skipView.setVisibility(View.VISIBLE);
        skipView.setText(String.format(Locale.CHINA, SKIP_TEXT, Math.round(millisUntilFinished / 1000f)));
    }

    @Override
    public void onADDismissed() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "onClose");
            sendUpdate(obj, false);
        } catch (Exception e) {
        }

        dismissAllowingStateLoss();
    }

    @Override
    public void onNoAD(AdError error) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("type", "onError");
            obj.put("code", error.getErrorCode());
            obj.put("msg", error.getErrorMsg());
            sendUpdate(obj, false);
        } catch (Exception e) {
        }

        /** 如果加载广告失败，则直接跳转 */
        this.dismissAllowingStateLoss();
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    private Bitmap getImageFromAssets(String imageName) {
        AssetManager am = mContext.getAssets();
        InputStream is = null;
        try {
            is = am.open("www/" + imageName);//得到图片流
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is);
    }

    private int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    @Override
    public void onDestroy() {
        if (container != null) container.clearAnimation();
        System.gc();
        super.onDestroy();
    }

    public CallbackContext callbackContext;

    public void setCallbackContext(CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
    }

    private void sendUpdate(JSONObject obj, boolean keepCallback) {
        sendUpdate(obj, keepCallback, PluginResult.Status.OK);
    }

    private void sendUpdate(JSONObject obj, boolean keepCallback, PluginResult.Status status) {
        PluginResult result = new PluginResult(status, obj);
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

}
