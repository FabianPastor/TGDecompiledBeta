package org.telegram.ui;

import org.telegram.tgnet.TLRPC.TL_account_verifyEmail;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.PassportActivity.AnonymousClass3;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$3$H95RgqCbEw0JF2FdjyTyxP1Zzp8 implements Runnable {
    private final /* synthetic */ AnonymousClass3 f$0;
    private final /* synthetic */ TL_error f$1;
    private final /* synthetic */ Runnable f$2;
    private final /* synthetic */ ErrorRunnable f$3;
    private final /* synthetic */ TL_account_verifyEmail f$4;

    public /* synthetic */ -$$Lambda$PassportActivity$3$H95RgqCbEw0JF2FdjyTyxP1Zzp8(AnonymousClass3 anonymousClass3, TL_error tL_error, Runnable runnable, ErrorRunnable errorRunnable, TL_account_verifyEmail tL_account_verifyEmail) {
        this.f$0 = anonymousClass3;
        this.f$1 = tL_error;
        this.f$2 = runnable;
        this.f$3 = errorRunnable;
        this.f$4 = tL_account_verifyEmail;
    }

    public final void run() {
        this.f$0.lambda$null$5$PassportActivity$3(this.f$1, this.f$2, this.f$3, this.f$4);
    }
}
