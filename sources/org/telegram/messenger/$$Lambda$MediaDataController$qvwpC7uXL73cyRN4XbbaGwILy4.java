package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$qvwpC7uXL73cyRN4X-bbaGwILy4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$qvwpC7uXL73cyRN4XbbaGwILy4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$qvwpC7uXL73cyRN4XbbaGwILy4 INSTANCE = new $$Lambda$MediaDataController$qvwpC7uXL73cyRN4XbbaGwILy4();

    private /* synthetic */ $$Lambda$MediaDataController$qvwpC7uXL73cyRN4XbbaGwILy4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$removePeer$98(tLObject, tLRPC$TL_error);
    }
}
