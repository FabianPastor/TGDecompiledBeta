package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$hyjd7SD0g6QlJvar_P6qIssa0whU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$hyjd7SD0g6QlJvar_P6qIssa0whU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$hyjd7SD0g6QlJvar_P6qIssa0whU INSTANCE = new $$Lambda$MediaDataController$hyjd7SD0g6QlJvar_P6qIssa0whU();

    private /* synthetic */ $$Lambda$MediaDataController$hyjd7SD0g6QlJvar_P6qIssa0whU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$84(tLObject, tLRPC$TL_error);
    }
}
