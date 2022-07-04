package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatUsersActivity;

public final /* synthetic */ class ChatUsersActivity$8$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ChatUsersActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC.User f$1;

    public /* synthetic */ ChatUsersActivity$8$$ExternalSyntheticLambda0(ChatUsersActivity.AnonymousClass8 r1, TLRPC.User user) {
        this.f$0 = r1;
        this.f$1 = user;
    }

    public final void run() {
        this.f$0.m3315lambda$didSelectUser$0$orgtelegramuiChatUsersActivity$8(this.f$1);
    }
}
