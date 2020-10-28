package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$N4i6MWL2IIKEsiQSKxvyXEoR06w  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$N4i6MWL2IIKEsiQSKxvyXEoR06w implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$N4i6MWL2IIKEsiQSKxvyXEoR06w INSTANCE = new $$Lambda$MessagesController$N4i6MWL2IIKEsiQSKxvyXEoR06w();

    private /* synthetic */ $$Lambda$MessagesController$N4i6MWL2IIKEsiQSKxvyXEoR06w() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$reportSpam$45(tLObject, tLRPC$TL_error);
    }
}
