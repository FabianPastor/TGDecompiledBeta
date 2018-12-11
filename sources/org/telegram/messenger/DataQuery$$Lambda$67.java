package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$67 implements Runnable {
    private final DataQuery arg$1;
    private final long arg$2;

    DataQuery$$Lambda$67(DataQuery dataQuery, long j) {
        this.arg$1 = dataQuery;
        this.arg$2 = j;
    }

    public void run() {
        this.arg$1.lambda$loadBotKeyboard$106$DataQuery(this.arg$2);
    }
}
