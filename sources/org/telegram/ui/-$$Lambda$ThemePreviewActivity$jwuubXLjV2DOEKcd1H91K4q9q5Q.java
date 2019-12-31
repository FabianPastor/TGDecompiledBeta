package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ThemePreviewActivity$jwuubXLjV2DOEKcd1H91K4q9q5Q implements RequestDelegate {
    private final /* synthetic */ ThemePreviewActivity f$0;

    public /* synthetic */ -$$Lambda$ThemePreviewActivity$jwuubXLjV2DOEKcd1H91K4q9q5Q(ThemePreviewActivity themePreviewActivity) {
        this.f$0 = themePreviewActivity;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$didReceivedNotification$19$ThemePreviewActivity(tLObject, tL_error);
    }
}
