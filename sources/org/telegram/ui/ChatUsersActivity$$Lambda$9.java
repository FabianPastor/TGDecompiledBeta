package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class ChatUsersActivity$$Lambda$9 implements RequestDelegate {
    private final ChatUsersActivity arg$1;

    ChatUsersActivity$$Lambda$9(ChatUsersActivity chatUsersActivity) {
        this.arg$1 = chatUsersActivity;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$loadChatParticipants$22$ChatUsersActivity(tLObject, tL_error);
    }
}
