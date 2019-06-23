package org.telegram.messenger;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import org.telegram.messenger.MediaDataController.KeywordResultCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$8sjmATlhe9Ln0JHkLqJNaDq-md8 implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ String[] f$1;
    private final /* synthetic */ KeywordResultCallback f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ boolean f$4;
    private final /* synthetic */ ArrayList f$5;
    private final /* synthetic */ CountDownLatch f$6;

    public /* synthetic */ -$$Lambda$MediaDataController$8sjmATlhe9Ln0JHkLqJNaDq-md8(MediaDataController mediaDataController, String[] strArr, KeywordResultCallback keywordResultCallback, String str, boolean z, ArrayList arrayList, CountDownLatch countDownLatch) {
        this.f$0 = mediaDataController;
        this.f$1 = strArr;
        this.f$2 = keywordResultCallback;
        this.f$3 = str;
        this.f$4 = z;
        this.f$5 = arrayList;
        this.f$6 = countDownLatch;
    }

    public final void run() {
        this.f$0.lambda$getEmojiSuggestions$123$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
