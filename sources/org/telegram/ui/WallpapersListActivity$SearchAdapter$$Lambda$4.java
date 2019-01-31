package org.telegram.ui;

import org.telegram.tgnet.TLObject;

final /* synthetic */ class WallpapersListActivity$SearchAdapter$$Lambda$4 implements Runnable {
    private final SearchAdapter arg$1;
    private final int arg$2;
    private final TLObject arg$3;

    WallpapersListActivity$SearchAdapter$$Lambda$4(SearchAdapter searchAdapter, int i, TLObject tLObject) {
        this.arg$1 = searchAdapter;
        this.arg$2 = i;
        this.arg$3 = tLObject;
    }

    public void run() {
        this.arg$1.lambda$null$3$WallpapersListActivity$SearchAdapter(this.arg$2, this.arg$3);
    }
}
