package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes3.dex */
public final /* synthetic */ class SessionBottomSheet$$ExternalSyntheticLambda1 implements RequestDelegate {
    public static final /* synthetic */ SessionBottomSheet$$ExternalSyntheticLambda1 INSTANCE = new SessionBottomSheet$$ExternalSyntheticLambda1();

    private /* synthetic */ SessionBottomSheet$$ExternalSyntheticLambda1() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        SessionBottomSheet.lambda$uploadSessionSettings$0(tLObject, tLRPC$TL_error);
    }
}
