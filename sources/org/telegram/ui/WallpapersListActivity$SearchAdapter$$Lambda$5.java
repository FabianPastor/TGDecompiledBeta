package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class WallpapersListActivity$SearchAdapter$$Lambda$5 implements Runnable {
    private final SearchAdapter arg$1;
    private final TLObject arg$2;

    WallpapersListActivity$SearchAdapter$$Lambda$5(SearchAdapter searchAdapter, TLObject tLObject) {
        this.arg$1 = searchAdapter;
        this.arg$2 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$1$WallpapersListActivity$SearchAdapter(this.arg$2);
    }
}
