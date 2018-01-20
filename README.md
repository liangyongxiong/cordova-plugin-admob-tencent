# Tencent Admob for GDT / Cordova Plugin

[![release](https://img.shields.io/badge/release-1.0.11-blue.svg)](https://github.com/liangyongxiong/cordova-plugin-admob-tencent/releases)
[![platforms](https://img.shields.io/badge/platforms-iOS%20%7C%20Android-lightgrey.svg)](https://github.com/liangyongxiong/cordova-plugin-admob-tencent)
[![qq](https://img.shields.io/badge/contact-qq-blue.svg)](http://wpa.qq.com/msgrd?v=3&uin=331338391&menu=yes)

通过 Javascript 调用腾讯广点通（GDT）SDK 接口，渲染广告内容

官方 SDK 下载：

+ [Android 标准版](http://imgcache.qq.com/qzone/biz/gdt/dev/sdk/android/release/GDT_Android_SDK.zip)

+ [Android X5 内核加强版](http://imgcache.qq.com/qzone/biz/gdt/dev/sdk/android/release/GDT_TBS_Android_SDK.zip)

+ [iOS 标准版](http://imgcache.qq.com/qzone/biz/gdt/dev/sdk/ios/release/GDT_iOS_SDK.zip)

## NPM
https://www.npmjs.com/package/cordova-plugin-admob-tencent

## Installation

通过 Cordova Plugins 安装

```shell
$ cordova plugin add cordova-plugin-admob-tecent
```

通过 URL 安装

```shell
$ cordova plugin add https://github.com/liangyongxiong/cordova-plugin-admob-tencent.git
```

## Usage

`YOUR_APP_ID` : 应用媒体ID

`YOUR_POSITION_ID` : 广告位ID

#### 横幅广告（Banner）

`interval` : 刷新间隔（秒）

`gps` : 是否开启GPS（0-否，1-是）

`close` : 是否显示关闭按钮（0-否，1-是）

`animation` : 是否显示动画效果（0-否，1-是）

`align` : 显示位置（top-顶部，bottom-底部）

```javascript
var banner = cordova.TencentAdMob.BannerAd.show({
    app: YOUR_APP_ID,
    position: YOUR_POSITION_ID,
    interval: 30,
    gps: 0,
    close: 1,
    animation: 0,
    align: 'bottom'
}).addEventListener('onSuccess', function(event) {
    console.log('Tencent AdMob banner onSuccess');

    // 5秒后自动关闭
    setTimeout(function() {
        banner.hide();
    }, 1000*5);
}).addEventListener('onError', function(event) {
    console.log('Tencent AdMob banner onError');
    console.error(event.msg);
}).addEventListener('onClose', function(event) {
    console.log('Tencent AdMob banner onClose');
}).addEventListener('onClick', function(event) {
    console.log('Tencent AdMob banner onClick');
}).addEventListener('onOpenOverlay', function(event) {
    console.log('Tencent AdMob banner onOpenOverlay');
}).addEventListener('onLeftApplication', function(event) {
    console.log('Tencent AdMob banner onLeftApplication');
});
```

#### 插屏广告（Interstitial）

`gps` : 是否开启GPS（0-否，1-是）

`popup` : 是否使用弹出模式（0-否，1-是），仅针对 Android 平台

```javascript
var interstitial = cordova.TencentAdMob.InterstitialAd.show({
    app: YOUR_APP_ID,
    position: YOUR_POSITION_ID,
    gps: 0,
    popup: 1
}).addEventListener('onSuccess', function(event) {
    console.log('Tencent AdMob interstitial onSuccess');

    // 5秒后自动关闭（仅针对 Android 平台下 popup = 1 的插屏广告有效）
    setTimeout(function() {
        interstitial.hide();
    }, 1000*5);
}).addEventListener('onError', function(event) {
    console.log('Tencent AdMob interstitial onError');
    console.error(event.msg);
}).addEventListener('onClose', function(event) {
    console.log('Tencent AdMob interstitial onClose');
}).addEventListener('onClick', function(event) {
    console.log('Tencent AdMob interstitial onClick');
}).addEventListener('onOpenOverlay', function(event) {
    console.log('Tencent AdMob interstitial onOpenOverlay');
}).addEventListener('onLeftApplication', function(event) {
    console.log('Tencent AdMob interstitial onLeftApplication');
});
```

#### 开屏广告（Splash）

`delay` : 延迟加载时间上限（秒）

`bottom` : 底部填充区域，包括图片路径和填充区域高度（单位：dp）

```javascript
cordova.TencentAdMob.SplashAd.show({
    app: YOUR_APP_ID,
    position: YOUR_POSITION_ID,
    delay: 3,
    bottom: {
        image: 'images/bottom.jpg',
        height: 120
    }
}).addEventListener('onSuccess', function(event) {
    console.log('Tencent AdMob splash onSuccess');
}).addEventListener('onError', function(event) {
    console.log('Tencent AdMob splash onError');
    console.error(event.msg);
}).addEventListener('onClose', function(event) {
    console.log('Tencent AdMob splash onClose');
}).addEventListener('onClick', function(event) {
    console.log('Tencent AdMob splash onClick');
}).addEventListener('onTick', function(event) {
    console.log('Tencent AdMob splash onTick');
    console.log('Milliseconds Until Finished : ' + event.milliseconds);
}).addEventListener('onLeftApplication', function(event) {
    console.log('Tencent AdMob splash onLeftApplication');
});
```

#### 原生广告（Native）

`count` : 单次加载广告数量

```javascript
cordova.TencentAdMob.NativeAd.load({
    app: YOUR_APP_ID,
    position: YOUR_POSITION_ID,
    count: 3,
}).addEventListener('onSuccess', function(event) {
    console.log('Tencent AdMob native onSuccess');

    // 原生广告的 JSON 数据
    alert(JSON.stringify(event.ads));
}).addEventListener('onError', function(event) {
    console.log('Tencent AdMob native onError');
});
```

## FAQ
Empty

## Support
+ [QQ](http://wpa.qq.com/msgrd?v=3&uin=331338391&menu=yes)
+ [WeChat](https://raw.githubusercontent.com/liangyongxiong/liangyongxiong.github.com/master/weixin.jpg)
+ [Email](mailto:331338391@qq.com)

## Contribute
Please contribute! Look at the [issues](https://github.com/liangyongxiong/cordova-plugin-admob-tencent/issues).

## License
This project is licensed under [MIT](https://github.com/liangyongxiong/cordova-plugin-admob-tencent/blob/master/LICENSE).

