//
//  TencentAdMob.m
//

#import "TencentAdMob.h"

#define ScreenWidth [UIScreen mainScreen].bounds.size.width
#define ScreenHeight [UIScreen mainScreen].bounds.size.height
#define IS_IPHONEX ([UIScreen instancesRespondToSelector:@selector(currentMode)] ? CGSizeEqualToSize(CGSizeMake(1125, 2436), [[UIScreen mainScreen] currentMode].size) : NO)


@implementation TencentAdMob
{
    GDTMobBannerView *_bannerView;          //Banner广告
    GDTMobInterstitial *_interstitialObj;   //插屏广告
    GDTNativeAd *_nativeAd;                 //原生广告
    GDTSplashAd *_splash;                   //开屏广告
    UIView *_bottomView;
}

#pragma mark - Banner
- (void)showBannerAd:(CDVInvokedUrlCommand*)command
{
    self.bannerCommand = command;
    NSDictionary *dic = [self returnDicWithJsonStr:command];
    if (dic) {
        NSString *appkey = [dic objectForKey:@"app"];
        NSString *posId = [dic objectForKey:@"position"];
        if ([dic objectForKey:@"align"] && [@"top" isEqualToString:[dic objectForKey:@"align"]]) {
            if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
                _bannerView = [[GDTMobBannerView alloc] initWithFrame:CGRectMake(0,20,ScreenWidth,GDTMOB_AD_SUGGEST_SIZE_728x90.height) appkey:appkey placementId:posId];
            } else {
                _bannerView = [[GDTMobBannerView alloc] initWithFrame:CGRectMake(0,20,ScreenWidth,GDTMOB_AD_SUGGEST_SIZE_320x50.height) appkey:appkey placementId:posId];
            }
        }else{
            if (UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad) {
                _bannerView = [[GDTMobBannerView alloc] initWithFrame:CGRectMake(0,ScreenHeight-GDTMOB_AD_SUGGEST_SIZE_728x90.height,ScreenWidth,GDTMOB_AD_SUGGEST_SIZE_728x90.height) appkey:appkey placementId:posId];
            } else {
                _bannerView = [[GDTMobBannerView alloc] initWithFrame:CGRectMake(0,ScreenHeight-GDTMOB_AD_SUGGEST_SIZE_320x50.height,ScreenWidth,GDTMOB_AD_SUGGEST_SIZE_320x50.height) appkey:appkey placementId:posId];
            }
        }

        _bannerView.delegate = self;
        _bannerView.currentViewController = [[UIApplication sharedApplication] keyWindow].rootViewController;
        if ([@"1" isEqualToString:[NSString stringWithFormat:@"%@",[dic objectForKey:@"animation"]]]) {
             _bannerView.isAnimationOn = YES;
        } else {
             _bannerView.isAnimationOn = NO;
        }
        if ([@"1" isEqualToString:[NSString stringWithFormat:@"%@",[dic objectForKey:@"close"]]]) {
            _bannerView.showCloseBtn = YES;
        } else {
            _bannerView.showCloseBtn = NO;
        }
        if ([@"1" isEqualToString:[NSString stringWithFormat:@"%@",[dic objectForKey:@"gps"]]]) {
            _bannerView.isGpsOn = YES;
        } else {
            _bannerView.isGpsOn = NO;
        }
        _bannerView.interval = [[dic objectForKey:@"interval"] intValue];//广告刷新间隔 [可选]
        [_bannerView loadAdAndShow];
        [[[UIApplication sharedApplication] keyWindow] addSubview:_bannerView];
    }
}

- (void)hideBannerAd:(CDVInvokedUrlCommand*)command
{
    _bannerView.delegate = nil;
    _bannerView.currentViewController = nil;
    [_bannerView removeFromSuperview];
    _bannerView = nil;
    [self bannerViewWillClose];
}

#pragma mark GDTMobBannerViewDelegate
- (void)bannerViewFailToReceived:(NSError *)error{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onError"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
- (void)bannerViewDidReceived{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onSuccess"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
- (void)bannerViewWillClose{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
- (void)bannerViewClicked{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onClick"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
- (void)bannerViewDidPresentFullScreenModal{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onOpenOverlay"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}
- (void)bannerViewWillLeaveApplication{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onLeftApplication"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.bannerCommand.callbackId];
}


#pragma mark - Interstitial 插屏广告
- (void)showInterstitialAd:(CDVInvokedUrlCommand*)command
{
    self.interstitialCommand = command;
    NSDictionary *dic = [self returnDicWithJsonStr:command];
    if (dic) {
        NSString *appkey = [dic objectForKey:@"app"];
        NSString *posId = [dic objectForKey:@"position"];
        NSString *gps = [NSString stringWithFormat:@"%@",[dic objectForKey:@"gps"]];
        _interstitialObj = [[GDTMobInterstitial alloc] initWithAppkey:appkey placementId:posId];
        _interstitialObj.delegate = self;
        if ([gps isEqualToString:@"0"]) {
            _interstitialObj.isGpsOn = YES; //【可选】设置GPS开关
        }else if ([gps isEqualToString:@"1"]){
            _interstitialObj.isGpsOn = YES;
        }
        [_interstitialObj loadAd];
    }
}

#pragma mark GDTMobInterstitialDelegate
//广告预加载成功回调
- (void)interstitialFailToLoadAd:(GDTMobInterstitial *)interstitial error:(NSError *)error{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onError"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)interstitialSuccessToLoadAd:(GDTMobInterstitial *)interstitial{
     [_interstitialObj presentFromRootViewController:[[[UIApplication sharedApplication] keyWindow] rootViewController]];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onSuccess"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)interstitialDidDismissScreen:(GDTMobInterstitial *)interstitial{
    [_interstitialObj loadAd];

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)interstitialClicked:(GDTMobInterstitial *)interstitial{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onClick"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)interstitialAdDidPresentFullScreenModal:(GDTMobInterstitial *)interstitial{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onOpenOverlay"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}
- (void)interstitialApplicationWillEnterBackground:(GDTMobInterstitial *)interstitial{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onLeftApplication"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.interstitialCommand.callbackId];
}


#pragma mark - Splash
- (void)showSplashAd:(CDVInvokedUrlCommand*)command
{
    self.splashCommand = command;
    NSDictionary *dic = [self returnDicWithJsonStr:command];
    if (dic) {
        NSString *appkey = [dic objectForKey:@"app"];
        NSString *posId = [dic objectForKey:@"position"];
        _splash = [[GDTSplashAd alloc] initWithAppkey:appkey placementId:posId];
        _splash.delegate = self;
        //针对不同设备尺寸设置不同的默认启动图片，拉取广告等待时间会展示该默认图片。
        _splash.backgroundColor = [UIColor colorWithPatternImage:[UIImage imageNamed:@""]];
        _splash.fetchDelay = [[dic objectForKey:@"delay"]  floatValue];//设置开屏拉取时间，超时则放弃展示
        //[可选]拉取并展示全屏开屏广告
        //[self.splash loadAdAndShowInWindow:self.window];
        //设置开屏底部自定义LogoView，展示半屏开屏广告
        _bottomView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, ScreenWidth, [[[dic objectForKey:@"bottom"] objectForKey:@"height"] floatValue])];
        _bottomView.backgroundColor = [UIColor whiteColor];

        UIImage *img = [self createImgWithPath:[[dic objectForKey:@"bottom"] objectForKey:@"image"]];
        UIImageView *logo = [[UIImageView alloc] initWithImage:img];
        logo.contentMode = UIViewContentModeScaleAspectFill;
        logo.frame = CGRectMake(0, 0, _bottomView.frame.size.width, _bottomView.frame.size.height);
        [_bottomView addSubview:logo];

        if (IS_IPHONEX) {
            UIButton *skipBtn = [UIButton buttonWithType:UIButtonTypeCustom];
            skipBtn.backgroundColor =  [UIColor colorWithRed:114/255.0f green:114/255.0f blue:114/255.0f alpha:1];
            [skipBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
            skipBtn.frame = CGRectMake(ScreenWidth-90, 35, 75, 35);
            skipBtn.layer.cornerRadius = 35*0.5;
            skipBtn.layer.masksToBounds = YES;
            skipBtn.backgroundColor = [UIColor clearColor];
            skipBtn.titleLabel.font = [UIFont systemFontOfSize:14];
            [skipBtn setTitle:@"跳过" forState:UIControlStateNormal];
            [_bottomView addSubview:skipBtn];
            [_splash loadAdAndShowInWindow:[UIApplication sharedApplication].keyWindow withBottomView:_bottomView skipView:skipBtn];
        } else {
            [_splash loadAdAndShowInWindow:[UIApplication sharedApplication].keyWindow withBottomView:_bottomView];
        }
    }
}

#pragma mark GDTSplashAdDelegate
-(void)splashAdFailToPresent:(GDTSplashAd *)splashAd withError:(NSError *)error{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onError"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];
}

-(void)splashAdSuccessPresentScreen:(GDTSplashAd *)splashAd{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onSuccess"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];
}
- (void)splashAdClosed:(GDTSplashAd *)splashAd{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onClose"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];

    _splash = nil;
}
- (void)splashAdClicked:(GDTSplashAd *)splashAd{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onClick"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];
}
- (void)splashAdLifeTime:(NSUInteger)time{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onTick"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:YES]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.splashCommand.callbackId];
}

#pragma mark - Native 原生广告
- (void)loadNativeAd:(CDVInvokedUrlCommand*)command
{
    self.nativeCommand = command;
    CDVPluginResult* result = nil;
    NSArray* options = command.arguments;
    if (!options) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"no setting keys"];
        [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
        return;
    }
    @try {
        NSMutableDictionary *settings = [[NSMutableDictionary alloc] initWithCapacity:20];

        NSDictionary *sets = self.commandDelegate.settings;
        for (NSString* settingName in options) {
          if(sets[ [settingName lowercaseString]] != nil) {
             settings[[settingName lowercaseString ]] = sets[ [settingName lowercaseString]];
          }
        }
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary: [settings copy]];

        NSDictionary *dic = [self returnDicWithJsonStr:command];
        if (dic) {
            //NSString *appkey = [dic objectForKey:@"app"];
            //NSString *posId = [dic objectForKey:@"position"];
            _nativeAd = [[GDTNativeAd alloc] initWithAppkey:@"1105344611" placementId:@"5080023687202663"];
            _nativeAd.controller = [[UIApplication sharedApplication] keyWindow].rootViewController;
            _nativeAd.delegate = self;
            [_nativeAd loadAd:[[dic objectForKey:@"count"] intValue]]; //这里以一次拉取一条原生广告为例
        }
    } @catch (NSException * e) {
        result = [CDVPluginResult resultWithStatus:CDVCommandStatus_NO_RESULT messageAsString:[e reason]];
        [self.commandDelegate sendPluginResult:result callbackId:[command callbackId]];
    } @finally {

    }
}

#pragma mark  GDTNativeAdDelegate
-(void)nativeAdFailToLoad:(NSError *)error{
    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"type":@"onError"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.nativeCommand.callbackId];
}
-(void)nativeAdSuccessToLoad:(NSArray *)nativeAdDataArray{
    NSMutableArray *ads = [NSMutableArray array];
    for (GDTNativeAdData *data in nativeAdDataArray) {
        NSMutableDictionary *dic = [NSMutableDictionary dictionary];
        if ([data.properties objectForKey:GDTNativeAdDataKeyTitle]) {
              [dic setObject:[data.properties objectForKey:GDTNativeAdDataKeyTitle] forKey:@"title"];
        } else {
              [dic setObject:@"" forKey:@"title"];
        }
        if ([data.properties objectForKey:GDTNativeAdDataKeyIconUrl]) {
            [dic setObject:[data.properties objectForKey:GDTNativeAdDataKeyIconUrl] forKey:@"icon"];
        } else {
            [dic setObject:@"" forKey:@"icon"];
        }
        if ([data.properties objectForKey:GDTNativeAdDataKeyDesc]) {
            [dic setObject:[data.properties objectForKey:GDTNativeAdDataKeyDesc] forKey:@"desc"];
        } else {
            [dic setObject:@"" forKey:@"desc"];
        }
        if ([data.properties objectForKey:GDTNativeAdDataKeyImgUrl]) {
            [dic setObject:[data.properties objectForKey:GDTNativeAdDataKeyImgUrl] forKey:@"image"];
        } else {
            [dic setObject:@"" forKey:@"image"];
        }
        if ([data.properties objectForKey:GDTNativeAdDataKeyImgList]) {
             [dic setObject:[data.properties objectForKey:GDTNativeAdDataKeyImgList] forKey:@"images"];
        } else {
             [dic setObject:@[] forKey:@"images"];
        }
        if ([data.properties objectForKey:GDTNativeAdDataKeyAppRating]) {
             [dic setObject:[data.properties objectForKey:GDTNativeAdDataKeyAppRating]  forKey:@"score"];
        } else {
             [dic setObject:@(0) forKey:@"score"];
        }
        if ([data.properties objectForKey:GDTNativeAdDataKeyAppPrice]) {
             [dic setObject:[data.properties objectForKey:GDTNativeAdDataKeyAppPrice] forKey:@"price"];
        } else {
             [dic setObject:@(0) forKey:@"price"];
        }
        [ads addObject:dic];
    }

    CDVPluginResult* pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK
                                                  messageAsDictionary:@{@"ads":ads,@"type":@"onSuccess"}];
    [pluginResult setKeepCallback:[NSNumber numberWithBool:NO]];
    [self.commandDelegate sendPluginResult:pluginResult callbackId:self.nativeCommand.callbackId];
}

#pragma mark - json解析
- (NSDictionary *)returnDicWithJsonStr:(CDVInvokedUrlCommand*)command{
    NSArray* options = command.arguments;
    NSString *jsonStr = [options firstObject];
    if (jsonStr.length>0) {
        NSData *jsonData = [jsonStr dataUsingEncoding:NSUTF8StringEncoding];
        NSError *err;
        NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:jsonData options:NSJSONReadingMutableContainers error:&err];
        if(!err)
        {
            return dic;
        } else {
            NSLog(@"======解析失败====%@",err);
            return nil;
        }
    } else {
        NSLog(@"======字符串为空====");
        return nil;
    }
}

//生成img
- (UIImage*)createImgWithPath:(NSString*)altPath
{
    UIImage* result = nil;
    NSString* path = [[[NSBundle mainBundle] bundlePath] stringByAppendingPathComponent:[NSString pathWithComponents:@[@"www", altPath]]];
    NSData* data = [NSData dataWithContentsOfFile:path];
    result = [UIImage imageWithData:data];
    return result;
}
@end
