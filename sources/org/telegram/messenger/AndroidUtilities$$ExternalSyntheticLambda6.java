package org.telegram.messenger;

import android.view.Window;
import java.util.ArrayList;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ long f$1;
    public final /* synthetic */ Window f$2;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda6(ArrayList arrayList, long j, Window window) {
        this.f$0 = arrayList;
        this.f$1 = j;
        this.f$2 = window;
    }

    public final void run() {
        AndroidUtilities.lambda$registerFlagSecure$9(this.f$0, this.f$1, this.f$2);
    }
}
