package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
/* loaded from: classes.dex */
public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda34 implements RequestDelegate {
    public static final /* synthetic */ FileRefController$$ExternalSyntheticLambda34 INSTANCE = new FileRefController$$ExternalSyntheticLambda34();

    private /* synthetic */ FileRefController$$ExternalSyntheticLambda34() {
    }

    @Override // org.telegram.tgnet.RequestDelegate
    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$27(tLObject, tLRPC$TL_error);
    }
}
