package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda9 implements Runnable {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda9(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void run() {
        NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new AndroidUtilities$$ExternalSyntheticLambda8(this.f$0));
    }
}
