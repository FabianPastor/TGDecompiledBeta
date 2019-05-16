package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$Cq82CY1mMDVABDekDXqZnXYfB8o implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$ArticleViewer$Cq82CY1mMDVABDekDXqZnXYfB8o(int i, Chat chat) {
        this.f$0 = i;
        this.f$1 = chat;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).loadFullChat(this.f$1.id, 0, true);
    }
}
