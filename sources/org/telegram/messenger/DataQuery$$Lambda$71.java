package org.telegram.messenger;

final /* synthetic */ class DataQuery$$Lambda$71 implements Runnable {
    private final DataQuery arg$1;
    private final String arg$2;

    DataQuery$$Lambda$71(DataQuery dataQuery, String str) {
        this.arg$1 = dataQuery;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$fetchNewEmojiKeywords$115$DataQuery(this.arg$2);
    }
}
