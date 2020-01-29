package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MediaDataController$UYr5RNs6jrG0mTEaloNNGqZKeTc  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MediaDataController$UYr5RNs6jrG0mTEaloNNGqZKeTc implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MediaDataController$UYr5RNs6jrG0mTEaloNNGqZKeTc INSTANCE = new $$Lambda$MediaDataController$UYr5RNs6jrG0mTEaloNNGqZKeTc();

    private /* synthetic */ $$Lambda$MediaDataController$UYr5RNs6jrG0mTEaloNNGqZKeTc() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MediaDataController.lambda$removeInline$79(tLObject, tL_error);
    }
}
