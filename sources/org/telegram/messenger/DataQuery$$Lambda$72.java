package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_emojiKeywordsDifference;

final /* synthetic */ class DataQuery$$Lambda$72 implements Runnable {
    private final DataQuery arg$1;
    private final TL_emojiKeywordsDifference arg$2;
    private final String arg$3;

    DataQuery$$Lambda$72(DataQuery dataQuery, TL_emojiKeywordsDifference tL_emojiKeywordsDifference, String str) {
        this.arg$1 = dataQuery;
        this.arg$2 = tL_emojiKeywordsDifference;
        this.arg$3 = str;
    }

    public void run() {
        this.arg$1.lambda$putEmojiKeywords$117$DataQuery(this.arg$2, this.arg$3);
    }
}
