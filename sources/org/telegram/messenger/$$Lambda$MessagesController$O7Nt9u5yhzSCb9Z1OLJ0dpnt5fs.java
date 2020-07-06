package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$O7Nt9u5yhzSCb9Z1OLJ0dpnt5fs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$O7Nt9u5yhzSCb9Z1OLJ0dpnt5fs implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$O7Nt9u5yhzSCb9Z1OLJ0dpnt5fs INSTANCE = new $$Lambda$MessagesController$O7Nt9u5yhzSCb9Z1OLJ0dpnt5fs();

    private /* synthetic */ $$Lambda$MessagesController$O7Nt9u5yhzSCb9Z1OLJ0dpnt5fs() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$blockUser$58(tLObject, tLRPC$TL_error);
    }
}
