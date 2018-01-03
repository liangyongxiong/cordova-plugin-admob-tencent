//
//  TencentAdMob.h
//
//

#import <Cordova/CDV.h>
#import <Cordova/CDVViewController.h>

#import "GDTMobBannerView.h"
#import "GDTMobInterstitial.h"
#import "GDTSplashAd.h"
#import "GDTNativeAd.h"
@interface TencentAdMob : CDVPlugin<GDTMobBannerViewDelegate,GDTMobInterstitialDelegate,GDTSplashAdDelegate,GDTNativeAdDelegate>

@property(nonatomic, strong)CDVInvokedUrlCommand *bannerCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *interstitialCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *splashCommand;
@property(nonatomic, strong)CDVInvokedUrlCommand *nativeCommand;
- (void)showBannerAd:(CDVInvokedUrlCommand*)command;
- (void)hideBannerAd:(CDVInvokedUrlCommand*)command;
- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command;
- (void)showSplashAd:(CDVInvokedUrlCommand*)command;
- (void)loadNativeAd:(CDVInvokedUrlCommand*)command;
@end



