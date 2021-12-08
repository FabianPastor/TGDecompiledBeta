package org.telegram.ui;

import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda68 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ LocaleController.LocaleInfo[] f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda68(LaunchActivity launchActivity, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.f$0 = launchActivity;
        this.f$1 = localeInfoArr;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m3122lambda$showLanguageAlert$82$orgtelegramuiLaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
