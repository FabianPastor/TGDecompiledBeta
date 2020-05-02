package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$mgE5qww7TwarA_XgoCBRmNkauu4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$mgE5qww7TwarA_XgoCBRmNkauu4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$mgE5qww7TwarA_XgoCBRmNkauu4 INSTANCE = new $$Lambda$MessagesController$mgE5qww7TwarA_XgoCBRmNkauu4();

    private /* synthetic */ $$Lambda$MessagesController$mgE5qww7TwarA_XgoCBRmNkauu4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionMessageAsRead$169(tLObject, tLRPC$TL_error);
    }
}
