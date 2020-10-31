package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$GMfRrRczdGZhrMpEpIprfPWdOIo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$GMfRrRczdGZhrMpEpIprfPWdOIo implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$GMfRrRczdGZhrMpEpIprfPWdOIo INSTANCE = new $$Lambda$MessagesController$GMfRrRczdGZhrMpEpIprfPWdOIo();

    private /* synthetic */ $$Lambda$MessagesController$GMfRrRczdGZhrMpEpIprfPWdOIo() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$181(tLObject, tLRPC$TL_error);
    }
}
