package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$uEqYweCvrn23Bevm_nS-YGp9ls8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$uEqYweCvrn23Bevm_nSYGp9ls8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$uEqYweCvrn23Bevm_nSYGp9ls8 INSTANCE = new $$Lambda$MediaDataController$uEqYweCvrn23Bevm_nSYGp9ls8();

    private /* synthetic */ $$Lambda$MediaDataController$uEqYweCvrn23Bevm_nSYGp9ls8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$29(tLObject, tLRPC$TL_error);
    }
}
