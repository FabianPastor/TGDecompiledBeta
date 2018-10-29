package org.telegram.ui;

import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class LaunchActivity$$Lambda$31 implements RequestDelegate {
    private final LaunchActivity arg$1;
    private final LocaleInfo[] arg$2;
    private final String arg$3;

    LaunchActivity$$Lambda$31(LaunchActivity launchActivity, LocaleInfo[] localeInfoArr, String str) {
        this.arg$1 = launchActivity;
        this.arg$2 = localeInfoArr;
        this.arg$3 = str;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$showLanguageAlert$47$LaunchActivity(this.arg$2, this.arg$3, tLObject, tL_error);
    }
}
