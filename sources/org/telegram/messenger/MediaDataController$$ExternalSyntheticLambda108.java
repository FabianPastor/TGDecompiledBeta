package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.MediaDataController;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda108 implements Runnable {
    public final /* synthetic */ MediaDataController f$0;
    public final /* synthetic */ String[] f$1;
    public final /* synthetic */ MediaDataController.KeywordResultCallback f$2;
    public final /* synthetic */ ArrayList f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda108(MediaDataController mediaDataController, String[] strArr, MediaDataController.KeywordResultCallback keywordResultCallback, ArrayList arrayList) {
        this.f$0 = mediaDataController;
        this.f$1 = strArr;
        this.f$2 = keywordResultCallback;
        this.f$3 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$getEmojiSuggestions$153(this.f$1, this.f$2, this.f$3);
    }
}
