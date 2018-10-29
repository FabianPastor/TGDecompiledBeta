package org.telegram.ui.Adapters;

final /* synthetic */ class DialogsSearchAdapter$$Lambda$2 implements Runnable {
    private final DialogsSearchAdapter arg$1;
    private final long arg$2;

    DialogsSearchAdapter$$Lambda$2(DialogsSearchAdapter dialogsSearchAdapter, long j) {
        this.arg$1 = dialogsSearchAdapter;
        this.arg$2 = j;
    }

    public void run() {
        this.arg$1.lambda$putRecentSearch$5$DialogsSearchAdapter(this.arg$2);
    }
}
