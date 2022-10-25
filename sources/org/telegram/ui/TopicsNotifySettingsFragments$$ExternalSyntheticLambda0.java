package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class TopicsNotifySettingsFragments$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ TopicsNotifySettingsFragments$$ExternalSyntheticLambda0 INSTANCE = new TopicsNotifySettingsFragments$$ExternalSyntheticLambda0();

    private /* synthetic */ TopicsNotifySettingsFragments$$ExternalSyntheticLambda0() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TopicsNotifySettingsFragments.lambda$removeException$0(tLObject, tLRPC$TL_error);
    }
}
