package org.telegram.ui;

import android.window.SplashScreen;
import android.window.SplashScreenView;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda18 implements SplashScreen.OnExitAnimationListener {
    public final /* synthetic */ LaunchActivity f$0;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda18(LaunchActivity launchActivity) {
        this.f$0 = launchActivity;
    }

    public final void onSplashScreenExit(SplashScreenView splashScreenView) {
        this.f$0.lambda$onCreate$0(splashScreenView);
    }
}
