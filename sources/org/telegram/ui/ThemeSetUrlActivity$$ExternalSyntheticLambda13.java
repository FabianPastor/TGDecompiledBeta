package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_account_updateTheme;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ThemeSetUrlActivity$$ExternalSyntheticLambda13 implements RequestDelegate {
    public final /* synthetic */ ThemeSetUrlActivity f$0;
    public final /* synthetic */ TLRPC$TL_account_updateTheme f$1;

    public /* synthetic */ ThemeSetUrlActivity$$ExternalSyntheticLambda13(ThemeSetUrlActivity themeSetUrlActivity, TLRPC$TL_account_updateTheme tLRPC$TL_account_updateTheme) {
        this.f$0 = themeSetUrlActivity;
        this.f$1 = tLRPC$TL_account_updateTheme;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$saveTheme$12(this.f$1, tLObject, tLRPC$TL_error);
    }
}
