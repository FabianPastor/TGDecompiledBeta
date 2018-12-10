package org.telegram.p005ui;

import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.TLRPC.Chat;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$38 */
final /* synthetic */ class ArticleViewer$$Lambda$38 implements Runnable {
    private final int arg$1;
    private final Chat arg$2;

    ArticleViewer$$Lambda$38(int i, Chat chat) {
        this.arg$1 = i;
        this.arg$2 = chat;
    }

    public void run() {
        MessagesController.getInstance(this.arg$1).loadFullChat(this.arg$2.f78id, 0, true);
    }
}
