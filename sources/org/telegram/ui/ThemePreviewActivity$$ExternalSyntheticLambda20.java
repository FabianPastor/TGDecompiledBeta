package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

public final /* synthetic */ class ThemePreviewActivity$$ExternalSyntheticLambda20 implements RequestDelegate {
    public final /* synthetic */ ThemePreviewActivity f$0;

    public /* synthetic */ ThemePreviewActivity$$ExternalSyntheticLambda20(ThemePreviewActivity themePreviewActivity) {
        this.f$0 = themePreviewActivity;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$didReceivedNotification$22(tLObject, tLRPC$TL_error);
    }
}
