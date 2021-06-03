package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$FileRefController$Wu_dPbVPA9LVjlj_AKyoSpxiwfs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FileRefController$Wu_dPbVPA9LVjlj_AKyoSpxiwfs implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FileRefController$Wu_dPbVPA9LVjlj_AKyoSpxiwfs INSTANCE = new $$Lambda$FileRefController$Wu_dPbVPA9LVjlj_AKyoSpxiwfs();

    private /* synthetic */ $$Lambda$FileRefController$Wu_dPbVPA9LVjlj_AKyoSpxiwfs() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$26(tLObject, tLRPC$TL_error);
    }
}
