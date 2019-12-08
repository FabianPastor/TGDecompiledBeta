package org.telegram.messenger;

import android.util.SparseArray;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$jlNDOQP2VJfXmHpCRXcLyvLdSBg implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ StringBuilder f$1;
    private final /* synthetic */ long f$2;
    private final /* synthetic */ ArrayList f$3;
    private final /* synthetic */ SparseArray f$4;
    private final /* synthetic */ int f$5;
    private final /* synthetic */ boolean f$6;

    public /* synthetic */ -$$Lambda$MediaDataController$jlNDOQP2VJfXmHpCRXcLyvLdSBg(MediaDataController mediaDataController, StringBuilder stringBuilder, long j, ArrayList arrayList, SparseArray sparseArray, int i, boolean z) {
        this.f$0 = mediaDataController;
        this.f$1 = stringBuilder;
        this.f$2 = j;
        this.f$3 = arrayList;
        this.f$4 = sparseArray;
        this.f$5 = i;
        this.f$6 = z;
    }

    public final void run() {
        this.f$0.lambda$loadReplyMessagesForMessages$96$MediaDataController(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}
