
var exec = require('cordova/exec');
var channel = require('cordova/channel');

var BaseAd = function() {
    this.channels = {};
};

BaseAd.prototype = {
    _eventHandler: function(event) {
        if (event && (event.type in this.channels)) {
            this.channels[event.type].fire(event);
        }
    },
    addEventListener: function(eventname,f) {
        if (!(eventname in this.channels)) {
            this.channels[eventname] = channel.create(eventname);
        }
        this.channels[eventname].subscribe(f);
        return this;
    },
};

var BannerAd = function() { BaseAd.call(this); };
BannerAd.prototype = new BaseAd();
BannerAd.prototype.hide = function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'TencentAdMob', 'hideBannerAd', [JSON.stringify(options)]);
};

BannerAd.show = function(options) {
    var ad = new BannerAd();
    var cb = function(eventname) {
       ad._eventHandler(eventname);
    };
    exec(cb, cb, 'TencentAdMob', 'showBannerAd', [JSON.stringify(options)]);
    return ad;
};

var InterstitialAd = function() { BaseAd.call(this); };
InterstitialAd.prototype = new BaseAd();
InterstitialAd.prototype.hide = function(options, successCallback, errorCallback) {
    exec(successCallback, errorCallback, 'TencentAdMob', 'hideInterstitialAd', [JSON.stringify(options)]);
};

InterstitialAd.show = function(options) {
    var ad = new InterstitialAd();
    var cb = function(eventname) {
       ad._eventHandler(eventname);
    };
    exec(cb, cb, 'TencentAdMob', 'showInterstitialAd', [JSON.stringify(options)]);
    return ad;
};

var SplashAd = function() { BaseAd.call(this); };
SplashAd.prototype = new BaseAd();

SplashAd.show = function(options) {
    var ad = new SplashAd();
    var cb = function(eventname) {
       ad._eventHandler(eventname);
    };
    exec(cb, cb, 'TencentAdMob', 'showSplashAd', [JSON.stringify(options)]);
    return ad;
};

var NativeAd = function() { BaseAd.call(this); };
NativeAd.prototype = new BaseAd();

NativeAd.load = function(options) {
    var ad = new NativeAd();
    var cb = function(eventname) {
       ad._eventHandler(eventname);
    };
    exec(cb, cb, 'TencentAdMob', 'loadNativeAd', [JSON.stringify(options)]);
    return ad;
};

module.exports = {
    BannerAd: BannerAd,
    InterstitialAd: InterstitialAd,
    SplashAd: SplashAd,
    NativeAd: NativeAd,
};

