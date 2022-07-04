package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC$Chat;

public final /* synthetic */ class ArticleViewer$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ TLRPC$Chat f$1;

    public /* synthetic */ ArticleViewer$$ExternalSyntheticLambda17(int i, TLRPC$Chat tLRPC$Chat) {
        this.f$0 = i;
        this.f$1 = tLRPC$Chat;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).loadFullChat(this.f$1.id, 0, true);
    }
}
