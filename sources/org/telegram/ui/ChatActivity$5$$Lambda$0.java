package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.BooleanCallback;
import org.telegram.ui.ChatActivity.AnonymousClass5;

final /* synthetic */ class ChatActivity$5$$Lambda$0 implements BooleanCallback {
    private final AnonymousClass5 arg$1;
    private final int arg$2;
    private final boolean arg$3;

    ChatActivity$5$$Lambda$0(AnonymousClass5 anonymousClass5, int i, boolean z) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = i;
        this.arg$3 = z;
    }

    public void run(boolean z) {
        this.arg$1.lambda$onItemClick$2$ChatActivity$5(this.arg$2, this.arg$3, z);
    }
}
