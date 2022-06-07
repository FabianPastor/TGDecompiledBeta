package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.ChatLinkActivity;

public final /* synthetic */ class ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ ChatLinkActivity.ListAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$Chat f$1;

    public /* synthetic */ ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda5(ChatLinkActivity.ListAdapter.AnonymousClass1 r1, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = r1;
        this.f$1 = tLRPC$Chat;
    }

    public final void run() {
        this.f$0.lambda$onJoinToSendToggle$6(this.f$1);
    }
}
