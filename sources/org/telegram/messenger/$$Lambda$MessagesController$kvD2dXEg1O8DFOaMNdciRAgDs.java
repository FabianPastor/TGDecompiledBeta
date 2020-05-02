package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$kvD2dXEg1O-8DFO-aMNdciRAgDs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$kvD2dXEg1O8DFOaMNdciRAgDs implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$kvD2dXEg1O8DFOaMNdciRAgDs INSTANCE = new $$Lambda$MessagesController$kvD2dXEg1O8DFOaMNdciRAgDs();

    private /* synthetic */ $$Lambda$MessagesController$kvD2dXEg1O8DFOaMNdciRAgDs() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$265(tLObject, tLRPC$TL_error);
    }
}
