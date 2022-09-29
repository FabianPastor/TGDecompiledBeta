package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda10 implements Runnable {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda10(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void run() {
        Utilities.globalQueue.postRunnable(new AndroidUtilities$$ExternalSyntheticLambda9(this.f$0));
    }
}
