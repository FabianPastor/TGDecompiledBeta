package org.telegram.ui;

import org.telegram.messenger.MessagesStorage.BooleanCallback;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.ui.DialogsActivity.AnonymousClass5;

final /* synthetic */ class DialogsActivity$5$$Lambda$5 implements BooleanCallback {
    private final AnonymousClass5 arg$1;
    private final int arg$2;
    private final Chat arg$3;
    private final long arg$4;

    DialogsActivity$5$$Lambda$5(AnonymousClass5 anonymousClass5, int i, Chat chat, long j) {
        this.arg$1 = anonymousClass5;
        this.arg$2 = i;
        this.arg$3 = chat;
        this.arg$4 = j;
    }

    public void run(boolean z) {
        this.arg$1.lambda$null$2$DialogsActivity$5(this.arg$2, this.arg$3, this.arg$4, z);
    }
}
