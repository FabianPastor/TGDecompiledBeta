package org.telegram.ui;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

final /* synthetic */ class WallpapersListActivity$SearchAdapter$$Lambda$1 implements RequestDelegate {
    private final SearchAdapter arg$1;

    WallpapersListActivity$SearchAdapter$$Lambda$1(SearchAdapter searchAdapter) {
        this.arg$1 = searchAdapter;
    }

    public void run(TLObject tLObject, TL_error tL_error) {
        this.arg$1.lambda$searchBotUser$2$WallpapersListActivity$SearchAdapter(tLObject, tL_error);
    }
}
