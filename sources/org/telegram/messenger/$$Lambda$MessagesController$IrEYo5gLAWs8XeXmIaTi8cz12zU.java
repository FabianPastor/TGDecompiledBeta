package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$IrEYo5gLAWs8XeXmIaTi8cz12zU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$IrEYo5gLAWs8XeXmIaTi8cz12zU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$IrEYo5gLAWs8XeXmIaTi8cz12zU INSTANCE = new $$Lambda$MessagesController$IrEYo5gLAWs8XeXmIaTi8cz12zU();

    private /* synthetic */ $$Lambda$MessagesController$IrEYo5gLAWs8XeXmIaTi8cz12zU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$173(tLObject, tLRPC$TL_error);
    }
}
