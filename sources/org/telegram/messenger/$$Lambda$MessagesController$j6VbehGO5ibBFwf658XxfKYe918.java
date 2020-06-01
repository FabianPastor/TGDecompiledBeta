package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$j6VbehGO5ibBFwvar_XxfKYe918  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$j6VbehGO5ibBFwvar_XxfKYe918 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$j6VbehGO5ibBFwvar_XxfKYe918 INSTANCE = new $$Lambda$MessagesController$j6VbehGO5ibBFwvar_XxfKYe918();

    private /* synthetic */ $$Lambda$MessagesController$j6VbehGO5ibBFwvar_XxfKYe918() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$170(tLObject, tLRPC$TL_error);
    }
}
