package org.telegram.ui;

final /* synthetic */ class WallpapersListActivity$SearchAdapter$$Lambda$0 implements Runnable {
    private final SearchAdapter arg$1;
    private final String arg$2;

    WallpapersListActivity$SearchAdapter$$Lambda$0(SearchAdapter searchAdapter, String str) {
        this.arg$1 = searchAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$processSearch$0$WallpapersListActivity$SearchAdapter(this.arg$2);
    }
}
