## Purpose
通过 Javascript 调用腾讯广点通（GDT）SDK 接口，渲染广告内容

## NPM
https://www.npmjs.com/package/cordova-plugin-admob-tencent

## Installation
    cordova plugin add cordova-plugin-admob-tecent

## Usage

#### 横幅广告（Banner）

`interval` : 刷新间隔（秒）

`gps` : 是否开启GPS（0-否，1-是）

`close` : 是否显示关闭按钮（0-否，1-是）

`animation` : 是否显示动画效果（0-否，1-是）

`align` : 显示位置（top-顶部，bottom-底部）


    cordova.TencentAdMob.BannerAd.show({
        app: YOUR_APP_ID,
        position: YOUR_POSITION_ID,
        interval: 30,
        gps: 0,
        close: 1,
        animation: 0,
        align: 'bottom'
    }).addEventListener('onSuccess',function(event) {
        console.log('Tencent AdMob banner onSuccess');
    }).addEventListener('onError',function(event) {
        console.log('Tencent AdMob banner onError');
    }).addEventListener('onClose',function(event) {
        console.log('Tencent AdMob banner onClose');
    }).addEventListener('onClick',function(event) {
        console.log('Tencent AdMob banner onClick');
    }).addEventListener('onOpenOverlay',function(event) {
        console.log('Tencent AdMob banner onOpenOverlay');
    }).addEventListener('onLeftApplication',function(event) {
        console.log('Tencent AdMob banner onLeftApplication');
    });


#### 插屏广告（Interstitial）

`gps` : 是否开启GPS（0-否，1-是）

`popup` : 是否使用弹出模式（0-否，1-是），只针对 Android 平台


    cordova.TencentAdMob.InterstitialAd.show({
        app: YOUR_APP_ID,
        position: YOUR_POSITION_ID,
        gps: 0,
        popup: 1
    }).addEventListener('onSuccess',function(event) {
        console.log('Tencent AdMob interstitial onSuccess');
    }).addEventListener('onError',function(event) {
        console.log('Tencent AdMob interstitial onError');
    }).addEventListener('onClose',function(event) {
        console.log('Tencent AdMob interstitial onClose');
    }).addEventListener('onClick',function(event) {
        console.log('Tencent AdMob interstitial onClick');
    }).addEventListener('onOpenOverlay',function(event) {
        console.log('Tencent AdMob interstitial onOpenOverlay');
    }).addEventListener('onLeftApplication',function(event) {
        console.log('Tencent AdMob interstitial onLeftApplication');
    });


#### 开屏广告（Splash）

`delay` : 延迟加载时间上限（秒）

`bottom` : 底部填充图片，包括图片路径和填充区域高度（单位：dp）


    cordova.TencentAdMob.SplashAd.show({
        app: YOUR_APP_ID,
        position: YOUR_POSITION_ID,
        delay: 3,
        bottom: {
            image: 'images/bottom.jpg',
            height: 120
        }
    }).addEventListener('onSuccess',function(event) {
        console.log('Tencent AdMob splash onSuccess');
    }).addEventListener('onError',function(event) {
        console.log('Tencent AdMob splash onError');
    }).addEventListener('onClose',function(event) {
        console.log('Tencent AdMob splash onClose');
    }).addEventListener('onClick',function(event) {
        console.log('Tencent AdMob splash onClick');
    }).addEventListener('onTick',function(event) {
        console.log('Tencent AdMob splash onTick');
    }).addEventListener('onLeftApplication',function(event) {
        console.log('Tencent AdMob splash onLeftApplication');
    });


#### 原生广告（Native）

`count` : 单次加载广告数量


    cordova.TencentAdMob.NativeAd.load({
        app: YOUR_APP_ID,
        position: YOUR_POSITION_ID,
        count: 3,
    }).addEventListener('onSuccess',function(event) {
        alert(JSON.stringify(event.ads));
        console.log('Tencent AdMob native onSuccess');
    }).addEventListener('onError',function(event) {
        console.log('Tencent AdMob native onError');
    });


## Credits
Empty
