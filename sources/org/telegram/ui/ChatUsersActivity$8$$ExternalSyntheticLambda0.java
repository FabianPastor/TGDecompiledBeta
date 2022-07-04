package org.telegram.ui;

import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ChatUsersActivity;

public final /* synthetic */ class ChatUsersActivity$8$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ChatUsersActivity.AnonymousClass8 f$0;
    public final /* synthetic */ TLRPC$User f$1;

    public /* synthetic */ ChatUsersActivity$8$$ExternalSyntheticLambda0(ChatUsersActivity.AnonymousClass8 r1, TLRPC$User tLRPC$User) {
        this.f$0 = r1;
        this.f$1 = tLRPC$User;
    }

    public final void run() {
        this.f$0.lambda$didSelectUser$0(this.f$1);
    }
}
