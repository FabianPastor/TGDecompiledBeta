package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class ChatUsersActivity$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ ChatUsersActivity f$0;
    public final /* synthetic */ TLRPC$Updates f$1;

    public /* synthetic */ ChatUsersActivity$$ExternalSyntheticLambda9(ChatUsersActivity chatUsersActivity, TLRPC$Updates tLRPC$Updates) {
        this.f$0 = chatUsersActivity;
        this.f$1 = tLRPC$Updates;
    }

    public final void run() {
        this.f$0.lambda$createMenuForParticipant$7(this.f$1);
    }
}
