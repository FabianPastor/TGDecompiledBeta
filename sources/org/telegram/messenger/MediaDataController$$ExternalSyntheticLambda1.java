package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ CountDownLatch f$0;
    public final /* synthetic */ MediaDataController.KeywordResultCallback f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda1(CountDownLatch countDownLatch, MediaDataController.KeywordResultCallback keywordResultCallback, ArrayList arrayList, String str) {
        this.f$0 = countDownLatch;
        this.f$1 = keywordResultCallback;
        this.f$2 = arrayList;
        this.f$3 = str;
    }

    public final void run() {
        MediaDataController.lambda$getEmojiSuggestions$193(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
