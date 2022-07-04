package org.telegram.ui;

import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.ChatLinkActivity;

public final /* synthetic */ class ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda7 implements Runnable {
    public final /* synthetic */ ChatLinkActivity.ListAdapter.AnonymousClass1 f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ ChatLinkActivity$ListAdapter$1$$ExternalSyntheticLambda7(ChatLinkActivity.ListAdapter.AnonymousClass1 r1, TLRPC$Chat tLRPC$Chat, boolean z, Runnable runnable) {
        this.f$0 = r1;
        this.f$1 = tLRPC$Chat;
        this.f$2 = z;
        this.f$3 = runnable;
    }

    public final void run() {
        this.f$0.lambda$onJoinToSendToggle$9(this.f$1, this.f$2, this.f$3);
    }
}
