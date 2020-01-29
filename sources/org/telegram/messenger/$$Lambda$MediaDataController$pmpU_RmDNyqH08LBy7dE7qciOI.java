package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$pmpU_RmDNyqH08LBy7dE-7qciOI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$pmpU_RmDNyqH08LBy7dE7qciOI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$pmpU_RmDNyqH08LBy7dE7qciOI INSTANCE = new $$Lambda$MediaDataController$pmpU_RmDNyqH08LBy7dE7qciOI();

    private /* synthetic */ $$Lambda$MediaDataController$pmpU_RmDNyqH08LBy7dE7qciOI() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$removePeer$80(tLObject, tL_error);
    }
}
