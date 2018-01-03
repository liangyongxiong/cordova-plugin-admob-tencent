## Purpose
A plugin to load ads of Tencent GDT via Javascript

## NPM
https://www.npmjs.com/package/cordova-plugin-admob-tencent

## Usage
cordova plugin add cordova-plugin-admob-tecent

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


## Credits
https://github.com/apache/cordova-labs/tree/cdvtest
