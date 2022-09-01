package org.telegram.ui;

import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda90 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ LocaleController.LocaleInfo[] f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda90(LaunchActivity launchActivity, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.f$0 = launchActivity;
        this.f$1 = localeInfoArr;
        this.f$2 = str;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$showLanguageAlert$105(this.f$1, this.f$2, tLObject, tLRPC$TL_error);
    }
}
