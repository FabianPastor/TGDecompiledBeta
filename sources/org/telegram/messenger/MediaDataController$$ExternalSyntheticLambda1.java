package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ MediaDataController.KeywordResultCallback f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ String f$2;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda1(MediaDataController.KeywordResultCallback keywordResultCallback, ArrayList arrayList, String str) {
        this.f$0 = keywordResultCallback;
        this.f$1 = arrayList;
        this.f$2 = str;
    }

    public final void run() {
        this.f$0.run(this.f$1, this.f$2);
    }
}
