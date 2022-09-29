package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.Utilities;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ Utilities.Callback f$3;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda0(String str, ArrayList arrayList, ArrayList arrayList2, Utilities.Callback callback) {
        this.f$0 = str;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
        this.f$3 = callback;
    }

    public final void run() {
        MediaDataController.lambda$getAnimatedEmojiByKeywords$189(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}
