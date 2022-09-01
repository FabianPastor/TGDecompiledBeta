package org.telegram.messenger;

import org.telegram.tgnet.TLRPC$TL_emojiKeywordsDifference;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda101 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ TLRPC$TL_emojiKeywordsDifference f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda101(MediaDataController mediaDataController, TLRPC$TL_emojiKeywordsDifference tLRPC$TL_emojiKeywordsDifference, String str) {
        this.f$0 = mediaDataController;
        this.f$1 = tLRPC$TL_emojiKeywordsDifference;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.lambda$putEmojiKeywords$188(this.f$1, this.f$2);
    }
}
