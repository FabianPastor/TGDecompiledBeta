package org.telegram.ui;

import org.telegram.messenger.LocaleController.LocaleInfo;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$zUSUlC8LSla6kKMHoMSEb1G2TkE implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ LocaleInfo[] f$1;
    private final /* synthetic */ String f$2;

    public /* synthetic */ -$$Lambda$LaunchActivity$zUSUlC8LSla6kKMHoMSEb1G2TkE(LaunchActivity launchActivity, LocaleInfo[] localeInfoArr, String str) {
        this.f$0 = launchActivity;
        this.f$1 = localeInfoArr;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$showLanguageAlert$53$LaunchActivity(this.f$1, this.f$2, tLObject, tL_error);
    }
}
