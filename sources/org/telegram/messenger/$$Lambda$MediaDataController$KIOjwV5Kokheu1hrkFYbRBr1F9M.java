package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$KIOjwV5Kokheu1hrkFYbRBr1F9M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$KIOjwV5Kokheu1hrkFYbRBr1F9M implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$KIOjwV5Kokheu1hrkFYbRBr1F9M INSTANCE = new $$Lambda$MediaDataController$KIOjwV5Kokheu1hrkFYbRBr1F9M();

    private /* synthetic */ $$Lambda$MediaDataController$KIOjwV5Kokheu1hrkFYbRBr1F9M() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$98(tLObject, tLRPC$TL_error);
    }
}
