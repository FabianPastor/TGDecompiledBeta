package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$g2YBB-4QmtMAednqDnrXkqg2TRQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$g2YBB4QmtMAednqDnrXkqg2TRQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$g2YBB4QmtMAednqDnrXkqg2TRQ INSTANCE = new $$Lambda$MediaDataController$g2YBB4QmtMAednqDnrXkqg2TRQ();

    private /* synthetic */ $$Lambda$MediaDataController$g2YBB4QmtMAednqDnrXkqg2TRQ() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MediaDataController.lambda$markFaturedStickersAsRead$30(tLObject, tLRPC$TL_error);
    }
}
