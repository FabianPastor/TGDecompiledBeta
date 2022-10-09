package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.LaunchActivity;
/* loaded from: classes3.dex */
public final /* synthetic */ class LaunchActivity$10$$ExternalSyntheticLambda0 implements RequestDelegate {
    public static final /* synthetic */ LaunchActivity$10$$ExternalSyntheticLambda0 INSTANCE = new LaunchActivity$10$$ExternalSyntheticLambda0();

    private /* synthetic */ LaunchActivity$10$$ExternalSyntheticLambda0() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        LaunchActivity.AnonymousClass10.lambda$onEmojiSelected$0(tLObject, tLRPC$TL_error);
    }
}
