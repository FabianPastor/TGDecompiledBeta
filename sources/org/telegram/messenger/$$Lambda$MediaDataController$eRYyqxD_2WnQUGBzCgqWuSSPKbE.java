package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$eRYyqxD_2WnQUGBzCgqWuSSPKbE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$eRYyqxD_2WnQUGBzCgqWuSSPKbE implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$eRYyqxD_2WnQUGBzCgqWuSSPKbE INSTANCE = new $$Lambda$MediaDataController$eRYyqxD_2WnQUGBzCgqWuSSPKbE();

    private /* synthetic */ $$Lambda$MediaDataController$eRYyqxD_2WnQUGBzCgqWuSSPKbE() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$30(tLObject, tLRPC$TL_error);
    }
}
