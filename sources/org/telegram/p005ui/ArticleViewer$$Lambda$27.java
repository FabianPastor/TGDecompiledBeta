package org.telegram.p005ui;

import org.telegram.p005ui.ArticleViewer.BlockChannelCell;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_error;

/* renamed from: org.telegram.ui.ArticleViewer$$Lambda$27 */
final /* synthetic */ class ArticleViewer$$Lambda$27 implements RequestDelegate {
    private final ArticleViewer arg$1;
    private final BlockChannelCell arg$2;
    private final int arg$3;
    private final TL_channels_joinChannel arg$4;
    private final Chat arg$5;

    ArticleViewer$$Lambda$27(ArticleViewer articleViewer, BlockChannelCell blockChannelCell, int i, TL_channels_joinChannel tL_channels_joinChannel, Chat chat) {
        this.arg$1 = articleViewer;
        this.arg$2 = blockChannelCell;
        this.arg$3 = i;
        this.arg$4 = tL_channels_joinChannel;
        this.arg$5 = chat;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$joinChannel$35$ArticleViewer(this.arg$2, this.arg$3, this.arg$4, this.arg$5, tLObject, tL_error);
    }
}
