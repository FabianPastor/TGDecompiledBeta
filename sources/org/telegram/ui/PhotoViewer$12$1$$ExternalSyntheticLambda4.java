package org.telegram.ui;

import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import org.telegram.ui.PhotoViewer;

public final /* synthetic */ class PhotoViewer$12$1$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ PhotoViewer.AnonymousClass12.AnonymousClass1 f$0;
    public final /* synthetic */ FrameLayout f$1;
    public final /* synthetic */ LongSparseArray f$2;
    public final /* synthetic */ int f$3;

    public /* synthetic */ PhotoViewer$12$1$$ExternalSyntheticLambda4(PhotoViewer.AnonymousClass12.AnonymousClass1 r1, FrameLayout frameLayout, LongSparseArray longSparseArray, int i) {
        this.f$0 = r1;
        this.f$1 = frameLayout;
        this.f$2 = longSparseArray;
        this.f$3 = i;
    }

    public final void run() {
        this.f$0.lambda$onSend$0(this.f$1, this.f$2, this.f$3);
    }
}
