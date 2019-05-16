package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$1W6W_t-pAiSBx5i38jWX5eUMkro implements RequestDelegate {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$LaunchActivity$1W6W_t-pAiSBx5i38jWX5eUMkro(LaunchActivity launchActivity, int i) {
        this.f$0 = launchActivity;
        this.f$1 = i;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$checkAppUpdate$35$LaunchActivity(this.f$1, tLObject, tL_error);
    }
}
