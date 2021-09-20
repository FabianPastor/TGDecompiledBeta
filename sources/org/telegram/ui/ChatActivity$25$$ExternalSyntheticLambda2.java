package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$25$$ExternalSyntheticLambda2 implements RequestDelegate {
    public final /* synthetic */ ChatActivity.AnonymousClass25 f$0;
    public final /* synthetic */ MessagesStorage f$1;

    public /* synthetic */ ChatActivity$25$$ExternalSyntheticLambda2(ChatActivity.AnonymousClass25 r1, MessagesStorage messagesStorage) {
        this.f$0 = r1;
        this.f$1 = messagesStorage;
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        this.f$0.lambda$loadLastUnreadMention$2(this.f$1, tLObject, tLRPC$TL_error);
    }
}
