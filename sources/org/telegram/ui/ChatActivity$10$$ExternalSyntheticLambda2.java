package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$10$$ExternalSyntheticLambda2 implements MessagesStorage.IntCallback {
    public final /* synthetic */ ChatActivity.AnonymousClass10 f$0;
    public final /* synthetic */ boolean f$1;

    public /* synthetic */ ChatActivity$10$$ExternalSyntheticLambda2(ChatActivity.AnonymousClass10 r1, boolean z) {
        this.f$0 = r1;
        this.f$1 = z;
    }

    public final void run(int i) {
        this.f$0.lambda$onItemClick$0(this.f$1, i);
    }
}
