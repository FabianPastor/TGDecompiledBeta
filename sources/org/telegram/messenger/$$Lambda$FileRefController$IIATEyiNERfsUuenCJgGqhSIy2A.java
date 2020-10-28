package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$FileRefController$IIATEyiNERfsUuenCJgGqhSIy2A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FileRefController$IIATEyiNERfsUuenCJgGqhSIy2A implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FileRefController$IIATEyiNERfsUuenCJgGqhSIy2A INSTANCE = new $$Lambda$FileRefController$IIATEyiNERfsUuenCJgGqhSIy2A();

    private /* synthetic */ $$Lambda$FileRefController$IIATEyiNERfsUuenCJgGqhSIy2A() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$23(tLObject, tLRPC$TL_error);
    }
}
