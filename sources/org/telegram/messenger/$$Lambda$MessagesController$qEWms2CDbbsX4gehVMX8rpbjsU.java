package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$q-EWms2CDbbsX4gehVMX8rpbjsU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$qEWms2CDbbsX4gehVMX8rpbjsU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$qEWms2CDbbsX4gehVMX8rpbjsU INSTANCE = new $$Lambda$MessagesController$qEWms2CDbbsX4gehVMX8rpbjsU();

    private /* synthetic */ $$Lambda$MessagesController$qEWms2CDbbsX4gehVMX8rpbjsU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMessageContentAsRead$167(tLObject, tLRPC$TL_error);
    }
}
