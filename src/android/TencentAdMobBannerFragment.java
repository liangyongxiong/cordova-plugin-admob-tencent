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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qq.e.ads.banner.ADSize;
import com.qq.e.ads.banner.AbstractBannerADListener;
import com.qq.e.ads.banner.BannerView;
import com.qq.e.comm.util.AdError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by shion on 2017/12/15.
 */

public class TencentAdMobBannerFragment extends DialogFragment {
    public static final String APPID = "APPID";//应用id
    public static final String BannerPosID = "BannerPosID";
    public static final String CLOSE = "CLOSE";
    public static final String INTERVAL = "interval";
    public static final String REFRESH = "refresh";
    private String appId = "";//应用id
    private String bannerPosId = "";
    private int close = 1;
    private int interval = 30;

    private Context mContext;
    ViewGroup bannerContainer;
    BannerView bv;

    public static TencentAdMobBannerFragment newInstance(String appid, String bannerPosID,
                                                int close, int interval) {
        TencentAdMobBannerFragment fragment = new TencentAdMobBannerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APPID, appid);
        bundle.putString(BannerPosID, bannerPosID);
        bundle.putInt(CLOSE, close);
        bundle.putInt(INTERVAL, interval);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        appId = getArguments().getString(APPID);
        bannerPosId = getArguments().getString(BannerPosID);
        close = getArguments().getInt(CLOSE);
        interval = getArguments().getInt(INTERVAL);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
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
        dialog.setCanceledOnTouchOutside(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bannerContainer = new FrameLayout(mContext);
        frameLayout.addView(bannerContainer, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return frameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
            initBanner();
            this.bv.loadAD();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        View v = getView();
        ViewGroup group = (ViewGroup) v.getParent();
        if (group != null) {
            Log.e("TencentAdMob", "VIEWGROUP-----------------" + group.toString());
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
            initBanner();
            this.bv.loadAD();
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
            initBanner();
            this.bv.loadAD();
        } else {
            // 如果用户没有授权，那么应该说明意图，引导用户去设置里面授权。
            Toast.makeText(mContext.getApplicationContext(), "应用缺少必要的权限！请点击\"权限\"，打开所需要的权限。", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + mContext.getPackageName()));
            startActivityForResult(intent, 0x100);
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
                    initBanner();
                    this.bv.loadAD();
                } else {
                    Toast.makeText(mContext.getApplicationContext(), "应用缺少必要的权限!", Toast.LENGTH_LONG).show();
                    dismissAllowingStateLoss();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        doCloseBanner();
        super.onDestroy();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        doCloseBanner();
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        doCloseBanner();
        super.onCancel(dialog);
    }

    private void initBanner() {
        this.bv = new BannerView((Activity) mContext, ADSize.BANNER, appId, bannerPosId);
        // 注意：如果开发者的banner不是始终展示在屏幕中的话，请关闭自动刷新，否则将导致曝光率过低。
        // 并且应该自行处理：当banner广告区域出现在屏幕后，再手动loadAD。
        bv.setRefresh(interval);
        bv.setShowClose(close == 1);
        bv.setADListener(new AbstractBannerADListener() {

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
            }

            @Override
            public void onADReceiv() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onSuccess");
                    sendUpdate(obj, true);
                } catch (Exception e) {
                }
            }

            @Override
            public void onADClosed() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onClose");
                    sendUpdate(obj, false);
                } catch (Exception e) {
                }

                super.onADClosed();
            }

            @Override
            public void onADClicked() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onClick");
                    sendUpdate(obj, true);
                } catch (Exception e) {
                }

                super.onADClicked();
            }

            @Override
            public void onADLeftApplication() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onLeftApplication");
                    sendUpdate(obj, true);
                } catch (Exception e) {
                }

                super.onADLeftApplication();
            }

            @Override
            public void onADOpenOverlay() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onOpenOverlay");
                    sendUpdate(obj, true);
                } catch (Exception e) {
                }

                super.onADOpenOverlay();
            }
        });
        bannerContainer.addView(bv);
    }

    private void doCloseBanner() {
        if (bannerContainer != null) {
            bannerContainer.removeAllViews();
            if (bv != null) {
                bv.destroy();
                bv = null;
            }
        }
        dismissAllowingStateLoss();
    }

    public Bitmap getImageFromAssets(String imageName) {
        AssetManager am = mContext.getAssets();
        InputStream is = null;
        try {
            is = am.open("www/" + imageName);//得到图片流
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is);
    }

    public int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
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
