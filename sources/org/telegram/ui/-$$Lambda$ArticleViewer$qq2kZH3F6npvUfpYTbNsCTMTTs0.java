package org.telegram.ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Chat;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$qq2kZH3F6npvUfpYTbNsCTMTTs0 implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ Chat f$1;

    public /* synthetic */ -$$Lambda$ArticleViewer$qq2kZH3F6npvUfpYTbNsCTMTTs0(int i, Chat chat) {
        this.f$0 = i;
        this.f$1 = chat;
    }

    public final void run() {
        MessagesController.getInstance(this.f$0).loadFullChat(this.f$1.id, 0, true);
    }
}
