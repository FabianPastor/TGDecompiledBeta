package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.ui.-$$Lambda$ChangeNameActivity$6bo7pFwsMx83tkMlu0ygZxpDMfM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$ChangeNameActivity$6bo7pFwsMx83tkMlu0ygZxpDMfM implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$ChangeNameActivity$6bo7pFwsMx83tkMlu0ygZxpDMfM INSTANCE = new $$Lambda$ChangeNameActivity$6bo7pFwsMx83tkMlu0ygZxpDMfM();

    private /* synthetic */ $$Lambda$ChangeNameActivity$6bo7pFwsMx83tkMlu0ygZxpDMfM() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        ChangeNameActivity.lambda$saveName$3(tLObject, tL_error);
    }
}
