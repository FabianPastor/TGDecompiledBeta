package org.telegram.messenger;

import java.util.ArrayList;
import org.telegram.messenger.Utilities;

public final /* synthetic */ class MediaDataController$$ExternalSyntheticLambda133 implements Runnable {
    public final /* synthetic */ Utilities.Callback f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ MediaDataController$$ExternalSyntheticLambda133(Utilities.Callback callback, ArrayList arrayList) {
        this.f$0 = callback;
        this.f$1 = arrayList;
    }

    public final void run() {
        MediaDataController.lambda$loadStickers$71(this.f$0, this.f$1);
    }
}
