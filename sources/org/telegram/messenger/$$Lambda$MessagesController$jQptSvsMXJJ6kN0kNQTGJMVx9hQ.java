package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$jQptSvsMXJJ6kN0kNQTGJMVx9hQ  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$jQptSvsMXJJ6kN0kNQTGJMVx9hQ implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$jQptSvsMXJJ6kN0kNQTGJMVx9hQ INSTANCE = new $$Lambda$MessagesController$jQptSvsMXJJ6kN0kNQTGJMVx9hQ();

    private /* synthetic */ $$Lambda$MessagesController$jQptSvsMXJJ6kN0kNQTGJMVx9hQ() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$hidePeerSettingsBar$33(tLObject, tL_error);
    }
}
