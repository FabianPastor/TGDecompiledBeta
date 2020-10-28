package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$TA3NoG9OxYyk4MjL8W2KOLln2ik  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$TA3NoG9OxYyk4MjL8W2KOLln2ik implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$TA3NoG9OxYyk4MjL8W2KOLln2ik INSTANCE = new $$Lambda$MessagesController$TA3NoG9OxYyk4MjL8W2KOLln2ik();

    private /* synthetic */ $$Lambda$MessagesController$TA3NoG9OxYyk4MjL8W2KOLln2ik() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$unblockPeer$74(tLObject, tLRPC$TL_error);
    }
}
