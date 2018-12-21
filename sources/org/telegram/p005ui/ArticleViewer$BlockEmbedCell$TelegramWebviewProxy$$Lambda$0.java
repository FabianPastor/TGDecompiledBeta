package org.telegram.p005ui;

import org.telegram.p005ui.ArticleViewer.BlockEmbedCell.TelegramWebviewProxy;

/* renamed from: org.telegram.ui.ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$$Lambda$0 */
final /* synthetic */ class ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$$Lambda$0 implements Runnable {
    private final TelegramWebviewProxy arg$1;
    private final String arg$2;
    private final String arg$3;

    ArticleViewer$BlockEmbedCell$TelegramWebviewProxy$$Lambda$0(TelegramWebviewProxy telegramWebviewProxy, String str, String str2) {
        this.arg$1 = telegramWebviewProxy;
        this.arg$2 = str;
        this.arg$3 = str2;
    }

    public void run() {
        this.arg$1.mo13868x2b8da535(this.arg$2, this.arg$3);
    }
}
