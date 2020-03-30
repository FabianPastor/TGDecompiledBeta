package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$xjJcEdMtqMkGbFdUKYvar_TtNdtI  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$xjJcEdMtqMkGbFdUKYvar_TtNdtI implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$xjJcEdMtqMkGbFdUKYvar_TtNdtI INSTANCE = new $$Lambda$MessagesController$xjJcEdMtqMkGbFdUKYvar_TtNdtI();

    private /* synthetic */ $$Lambda$MessagesController$xjJcEdMtqMkGbFdUKYvar_TtNdtI() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$167(tLObject, tLRPC$TL_error);
    }
}
