package org.telegram.messenger;

import android.util.LongSparseArray;
import java.util.HashMap;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda54 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ StringBuilder f$1;
    public final /* synthetic */ long f$2;
    public final /* synthetic */ HashMap f$3;
    public final /* synthetic */ LongSparseArray f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ Runnable f$6;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda54(MediaDataController mediaDataController, StringBuilder sb, long j, HashMap hashMap, LongSparseArray longSparseArray, boolean z, Runnable runnable) {
        this.f$0 = mediaDataController;
        this.f$1 = sb;
        this.f$2 = j;
        this.f$3 = hashMap;
        this.f$4 = longSparseArray;
        this.f$5 = z;
        this.f$6 = runnable;
    }

    public final void run() {
        this.f$0.lambda$loadReplyMessagesForMessages$117(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
