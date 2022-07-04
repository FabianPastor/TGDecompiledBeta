package org.telegram.ui;

import java.util.HashMap;
import org.telegram.messenger.LocaleController;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda36 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ LocaleController.LocaleInfo[] f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda36(LaunchActivity launchActivity, HashMap hashMap, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.f$0 = launchActivity;
        this.f$1 = hashMap;
        this.f$2 = localeInfoArr;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.m3703lambda$showLanguageAlert$101$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3);
    }
}
