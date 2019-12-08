package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$pgwzpweYL-lhFQhSee8lhKf-igc implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$pgwzpweYL-lhFQhSee8lhKf-igc INSTANCE = new -$$Lambda$MessagesController$pgwzpweYL-lhFQhSee8lhKf-igc();

    private /* synthetic */ -$$Lambda$MessagesController$pgwzpweYL-lhFQhSee8lhKf-igc() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$157(tLObject, tL_error);
    }
}
