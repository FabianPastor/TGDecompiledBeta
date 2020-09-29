package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$yYBszYssveBfYOym0fdczLKIeBA  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$yYBszYssveBfYOym0fdczLKIeBA implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$yYBszYssveBfYOym0fdczLKIeBA INSTANCE = new $$Lambda$MessagesController$yYBszYssveBfYOym0fdczLKIeBA();

    private /* synthetic */ $$Lambda$MessagesController$yYBszYssveBfYOym0fdczLKIeBA() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$processUpdates$271(tLObject, tLRPC$TL_error);
    }
}
