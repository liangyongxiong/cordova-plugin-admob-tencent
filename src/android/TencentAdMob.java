package cn.liangyongxiong.cordova.plugin.admob.tencent;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.util.Log;

import com.qq.e.ads.nativ.NativeAD;
import com.qq.e.ads.nativ.NativeADDataRef;
import com.qq.e.comm.util.AdError;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TencentAdMob extends CordovaPlugin {

    public static final String TAG = TencentAdMob.class.getSimpleName();
    private RelativeLayout bottomView, contentView;
    private static final int BOTTOM_VIEW_ID = 0x1;

    private TencentAdMobBannerFragment bannerFragment;
    private TencentAdMobInterstitialFragment interstitialFragment;

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        final Activity activity = this.cordova.getActivity();

        if (action.equals("showBannerAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");
            final int close = object.getInt("close");
            final int interval = object.getInt("interval");
            final String align = object.optString("align");

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    bottomView = new RelativeLayout(activity);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );
                    if (align.equalsIgnoreCase("top")) {
                        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    } else {
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                    }
                    params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    bottomView.setLayoutParams(params);//底部容器
                    bottomView.setId(BOTTOM_VIEW_ID);

                    contentView = new RelativeLayout(activity);
                    contentView.addView(bottomView);
                    activity.addContentView(contentView, new RelativeLayout.LayoutParams(
                            RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.MATCH_PARENT));

                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    bannerFragment = TencentAdMobBannerFragment.newInstance(app, position, close, interval);
                    bannerFragment.setCallbackContext(callbackContext);
                    ft.replace(BOTTOM_VIEW_ID, bannerFragment);
                    ft.commitAllowingStateLoss();
                }
            });

        } else if (action.equals("hideBannerAd")) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (bannerFragment != null) {
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("type", "onClose");
                            sendUpdate(bannerFragment.callbackContext, obj, false);
                        } catch (Exception e) {
                        }

                        FragmentManager fm = activity.getFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.remove(bannerFragment);
                        ft.commitAllowingStateLoss();
                    }
                    ViewGroup group = activity.findViewById(android.R.id.content);
                    if (group != null) {
                        group.removeView(contentView);
                    }
                }
            });

        } else if (action.equals("showInterstitialAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");
            final int popup = object.getInt("popup");   // 0:show | 1:showPopup

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    interstitialFragment = TencentAdMobInterstitialFragment.newInstance(app, position, popup);
                    interstitialFragment.setCallbackContext(callbackContext);
                    ft.add(interstitialFragment, TencentAdMobInterstitialFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });
        } else if (action.equals("hideInterstitialAd")) {
            //关闭
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        FragmentManager fm = activity.getFragmentManager();
                        String tag = TencentAdMobInterstitialFragment.class.getSimpleName();
                        TencentAdMobInterstitialFragment fragment = (TencentAdMobInterstitialFragment) fm.findFragmentByTag(tag);
                        if (fragment != null) {
                            fragment.finishFragment();
                        }
                    }
            });

        } else if (action.equals("showSplashAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");
            final int delay = object.getInt("delay");
            JSONObject bottom = object.getJSONObject("bottom");
            final String image = bottom.getString("image");
            final int height = bottom.getInt("height");
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentManager fm = activity.getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    TencentAdMobSplashFragment fragment = TencentAdMobSplashFragment.newInstance(app, position, delay, image, height);
                    fragment.setCallbackContext(callbackContext);
                    ft.add(fragment, TencentAdMobSplashFragment.class.getSimpleName());
                    ft.commitAllowingStateLoss();
                }
            });

        } else if (action.equals("loadNativeAd")) {
            String content = args.getString(0);
            JSONObject object = new JSONObject(content);
            final String app = object.getString("app");
            final String position = object.getString("position");
            final int count = object.getInt("count");

            try {
                activity.runOnUiThread(new Runnable() {
                    NativeAD nativeAD;

                    @Override
                    public void run() {
                        loadAD(count);
                    }

                    //初始化并加载广告
                    public void loadAD(int count) {
                        if (nativeAD == null) {
                            this.nativeAD = new NativeAD(activity,
                                    app, position,
                                    new NativeAD.NativeAdListener() {
                                        @Override
                                        public void onADLoaded(List<NativeADDataRef> list) {
                                            int size = list.size();
                                            JSONArray array = new JSONArray();
                                            try {
                                                for (int i = 0; i < size; i++) {
                                                    JSONObject obj = new JSONObject();
                                                    NativeADDataRef nativeADDataRef = list.get(i);
                                                    String title = nativeADDataRef.getTitle();
                                                    String icon = nativeADDataRef.getIconUrl();
                                                    String img = nativeADDataRef.getImgUrl();
                                                    String desc = nativeADDataRef.getDesc();
                                                    ArrayList<String> imgs = (ArrayList<String>) nativeADDataRef.getImgList();
                                                    if (!TextUtils.isEmpty(title)) {
                                                        obj.put("title", title);
                                                    } else {
                                                        obj.put("title", "");
                                                    }
                                                    if (!TextUtils.isEmpty(icon)) {
                                                        obj.put("icon", icon);
                                                    } else {
                                                        obj.put("icon", "");
                                                    }
                                                    if (!TextUtils.isEmpty(img)) {
                                                        obj.put("img", img);
                                                    } else {
                                                        obj.put("img", "");
                                                    }
                                                    if (!TextUtils.isEmpty(desc)) {
                                                        obj.put("desc", desc);
                                                    } else {
                                                        obj.put("desc", "");
                                                    }
                                                    if (imgs != null && imgs.size() > 0) {
                                                        JSONArray arr = new JSONArray();
                                                        for (int j = 0; j < imgs.size(); i++) {
                                                            arr.put(imgs.get(j));
                                                        }
                                                        obj.put("imgs", arr.toString());
                                                    } else {
                                                        obj.put("imgs", new JSONArray().toString());
                                                    }
                                                    String score = String.valueOf(nativeADDataRef.getAPPScore());
                                                    double p = (double) (nativeADDataRef.getAPPPrice());
                                                    String price = String.valueOf(p);
                                                    if (TextUtils.isEmpty(score) || !TextUtils.isDigitsOnly(score)) {
                                                        obj.put("score", 0);
                                                    } else {
                                                        obj.put("score", Integer.parseInt(score));
                                                    }
                                                    if (TextUtils.isEmpty(price)) {
                                                        obj.put("price", (double) 0);
                                                    } else {
                                                        if (Double.isInfinite(p) || Double.isNaN(p)) {
                                                            obj.put("price", (double) 0);
                                                        } else {
                                                            obj.put("price", p);
                                                        }
                                                    }
                                                    array.put(obj);
                                                }
                                                JSONObject adsJsonObject = new JSONObject();
                                                adsJsonObject.put("ads", array);
                                                adsJsonObject.put("type", "onSuccess");
                                                sendUpdate(callbackContext, adsJsonObject, false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onNoAD(AdError error) {
                                            try {
                                                JSONObject obj = new JSONObject();
                                                obj.put("type", "onError");
                                                obj.put("code", error.getErrorCode());
                                                obj.put("msg", error.getErrorMsg());
                                                sendUpdate(callbackContext, obj, false);
                                            } catch (Exception e) {
                                            }
                                        }

                                        @Override
                                        public void onADStatusChanged(NativeADDataRef nativeADDataRef) {

                                        }

                                        @Override
                                        public void onADError(NativeADDataRef nativeADDataRef, AdError error) {
                                            try {
                                                JSONObject obj = new JSONObject();
                                                obj.put("type", "onError");
                                                obj.put("code", error.getErrorCode());
                                                obj.put("msg", error.getErrorMsg());
                                                sendUpdate(callbackContext, obj, false);
                                            } catch (Exception e) {
                                            }
                                        }
                                    });
                        }
                        nativeAD.loadAD(count);
                    }
                });
            } catch (Exception ex) {
                callbackContext.error(0);
            }
        } else {
            return false;
        }
        return true;
    }

    private void sendUpdate(CallbackContext callbackContext, JSONObject obj, boolean keepCallback) {
        sendUpdate(callbackContext, obj, keepCallback, PluginResult.Status.OK);
    }

    private void sendUpdate(CallbackContext callbackContext, JSONObject obj, boolean keepCallback, PluginResult.Status status) {
        PluginResult result = new PluginResult(status, obj);
        result.setKeepCallback(keepCallback);
        callbackContext.sendPluginResult(result);
    }

}
