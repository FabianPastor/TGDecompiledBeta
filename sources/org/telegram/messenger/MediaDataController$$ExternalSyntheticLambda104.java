package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda104 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String[] f$1;
    public final /* synthetic */ MediaDataController.KeywordResultCallback f$2;
    public final /* synthetic */ String f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ ArrayList f$5;
    public final /* synthetic */ CountDownLatch f$6;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda104(MediaDataController mediaDataController, String[] strArr, MediaDataController.KeywordResultCallback keywordResultCallback, String str, boolean z, ArrayList arrayList, CountDownLatch countDownLatch) {
        this.f$0 = mediaDataController;
        this.f$1 = strArr;
        this.f$2 = keywordResultCallback;
        this.f$3 = str;
        this.f$4 = z;
        this.f$5 = arrayList;
        this.f$6 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getEmojiSuggestions$148(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}