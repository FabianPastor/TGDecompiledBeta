package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$24$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ChatActivity.AnonymousClass24 f$0;
    public final /* synthetic */ TLObject f$1;
    public final /* synthetic */ TLRPC.TL_error f$2;
    public final /* synthetic */ MessagesStorage f$3;

    public /* synthetic */ ChatActivity$24$$ExternalSyntheticLambda0(ChatActivity.AnonymousClass24 r1, TLObject tLObject, TLRPC.TL_error tL_error, MessagesStorage messagesStorage) {
        this.f$0 = r1;
        this.f$1 = tLObject;
        this.f$2 = tL_error;
        this.f$3 = messagesStorage;
    }

    public final void run() {
        this.f$0.m3154lambda$loadLastUnreadMention$1$orgtelegramuiChatActivity$24(this.f$1, this.f$2, this.f$3);
    }
}
