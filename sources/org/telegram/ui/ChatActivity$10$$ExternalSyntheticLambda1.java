package org.telegram.ui;

import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.ChatActivity;

public final /* synthetic */ class ChatActivity$10$$ExternalSyntheticLambda1 implements MessagesStorage.BooleanCallback {
    public final /* synthetic */ ChatActivity.AnonymousClass10 f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ ChatActivity$10$$ExternalSyntheticLambda1(ChatActivity.AnonymousClass10 r1, int i) {
        this.f$0 = r1;
        this.f$1 = i;
    }

    public final void run(boolean z) {
        this.f$0.lambda$onItemClick$1(this.f$1, z);
    }
}
