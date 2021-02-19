package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$p966i4jdTYMJNlF_uJ6ECgqtqXs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$p966i4jdTYMJNlF_uJ6ECgqtqXs implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$p966i4jdTYMJNlF_uJ6ECgqtqXs INSTANCE = new $$Lambda$MessagesController$p966i4jdTYMJNlF_uJ6ECgqtqXs();

    private /* synthetic */ $$Lambda$MessagesController$p966i4jdTYMJNlF_uJ6ECgqtqXs() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$189(tLObject, tLRPC$TL_error);
    }
}
