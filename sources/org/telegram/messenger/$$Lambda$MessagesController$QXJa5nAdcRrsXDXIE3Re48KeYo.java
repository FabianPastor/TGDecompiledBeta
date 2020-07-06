package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$QXJa5nAdcRrsXDXIE3-Re48KeYo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$QXJa5nAdcRrsXDXIE3Re48KeYo implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$QXJa5nAdcRrsXDXIE3Re48KeYo INSTANCE = new $$Lambda$MessagesController$QXJa5nAdcRrsXDXIE3Re48KeYo();

    private /* synthetic */ $$Lambda$MessagesController$QXJa5nAdcRrsXDXIE3Re48KeYo() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$saveTheme$81(tLObject, tLRPC$TL_error);
    }
}
