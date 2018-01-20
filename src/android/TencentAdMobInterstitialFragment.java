package cn.liangyongxiong.cordova.plugin.admob.tencent;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qq.e.ads.interstitial.AbstractInterstitialADListener;
import com.qq.e.ads.interstitial.InterstitialAD;
import com.qq.e.comm.util.AdError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shion on 2017/12/15.
 */

public class TencentAdMobInterstitialFragment extends DialogFragment {
    public static final String APPID = "APPID";//应用id
    public static final String InterteristalPosID = "InterteristalPosID";
    public static final String POPUP = "popup";
    private String appId = "";//应用id
    private String interteristalPosID = "";
    private int popup;
    private Context mContext;
    InterstitialAD iad;

    public static TencentAdMobInterstitialFragment newInstance(String appid, String bannerPosID, int popup) {
        TencentAdMobInterstitialFragment fragment = new TencentAdMobInterstitialFragment();
        Bundle bundle = new Bundle();
        bundle.putString(APPID, appid);
        bundle.putString(InterteristalPosID, bannerPosID);
        bundle.putInt(POPUP, popup);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        appId = getArguments().getString(APPID);
        interteristalPosID = getArguments().getString(InterteristalPosID);
        popup = getArguments().getInt(POPUP);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent);
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.windowAnimations = android.R.style.Animation_Dialog;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            lp.gravity = Gravity.CENTER;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
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
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return frameLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkAndRequestPermission();
        } else {
            // 如果是Android6.0以下的机器，默认在安装时获得了所有权限，可以直接调用SDK
            if (popup == 0) {
                showAD();
            } else {
                showAsPopup();
            }
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
            if (popup == 0) {//show
                showAD();
            } else {
                showAsPopup();
            }
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
            if (popup == 0) {
                showAD();
            } else {
                showAsPopup();
            }
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
                    if (popup == 0) {//show
                        showAD();
                    } else {
                        showAsPopup();
                    }
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

    private InterstitialAD getIAD() {
        if (iad == null) {
            iad = new InterstitialAD((Activity) mContext, appId, interteristalPosID);
        }
        return iad;
    }

    private void showAD() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

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

                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(TencentAdMobInterstitialFragment.this);
                ft.commitAllowingStateLoss();
            }

            @Override
            public void onADReceive() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onSuccess");
                    sendUpdate(obj, true);
                } catch (Exception e) {
                }

                iad.show();
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
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.remove(TencentAdMobInterstitialFragment.this);
                ft.commitAllowingStateLoss();
            }
        });
        iad.loadAD();
    }

    private void showAsPopup() {
        getIAD().setADListener(new AbstractInterstitialADListener() {

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

                doCloseBanner();
                TencentAdMobInterstitialFragment.this.dismissAllowingStateLoss();
            }

            @Override
            public void onADReceive() {
                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onSuccess");
                    sendUpdate(obj, true);
                } catch (Exception e) {
                }

                iad.showAsPopupWindow();
            }

            @Override
            public void onADClosed() {
                super.onADClosed();

                try {
                    JSONObject obj = new JSONObject();
                    obj.put("type", "onClose");
                    sendUpdate(obj, false);
                } catch (Exception e) {
                }

                doCloseBanner();
                TencentAdMobInterstitialFragment.this.dismissAllowingStateLoss();
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

        });
        iad.loadAD();
    }

    private void doCloseBanner() {
        if (iad != null) {
            iad.closePopupWindow();
        }
        dismissAllowingStateLoss();
    }

    public void finishFragment() {
        if (popup == 0) {
            //不用处理
        } else {//关闭showPopup
            doCloseBanner();
        }
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
