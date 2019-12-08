package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.MediaDataController.KeywordResultCallback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MediaDataController$HcGNepsOp3SXKX2Wn4r7lKkrM0g implements Runnable {
    private final /* synthetic */ MediaDataController f$0;
    private final /* synthetic */ String[] f$1;
    private final /* synthetic */ KeywordResultCallback f$2;
    private final /* synthetic */ ArrayList f$3;

    public /* synthetic */ -$$Lambda$MediaDataController$HcGNepsOp3SXKX2Wn4r7lKkrM0g(MediaDataController mediaDataController, String[] strArr, KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        this.f$0 = mediaDataController;
        this.f$1 = strArr;
        this.f$2 = keywordResultCallback;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$null$121$MediaDataController(this.f$1, this.f$2, this.f$3);
    }
}
