package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.TL_channels_joinChannel;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ArticleViewer$tOg7TGz-CemIZKoQ4SmkPVL-N7w implements RequestDelegate {
    private final /* synthetic */ ArticleViewer f$0;
    private final /* synthetic */ BlockChannelCell f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ TL_channels_joinChannel f$3;
    private final /* synthetic */ Chat f$4;

    public /* synthetic */ -$$Lambda$ArticleViewer$tOg7TGz-CemIZKoQ4SmkPVL-N7w(ArticleViewer articleViewer, BlockChannelCell blockChannelCell, int i, TL_channels_joinChannel tL_channels_joinChannel, Chat chat) {
        this.f$0 = articleViewer;
        this.f$1 = blockChannelCell;
        this.f$2 = i;
        this.f$3 = tL_channels_joinChannel;
        this.f$4 = chat;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$joinChannel$35$ArticleViewer(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}
