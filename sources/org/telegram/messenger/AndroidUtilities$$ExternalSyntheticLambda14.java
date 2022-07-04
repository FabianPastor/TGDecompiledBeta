package org.telegram.messenger;

import java.util.ArrayList;

public final /* synthetic */ class AndroidUtilities$$ExternalSyntheticLambda14 implements Runnable {
    public final /* synthetic */ ArrayList f$0;

    public /* synthetic */ AndroidUtilities$$ExternalSyntheticLambda14(ArrayList arrayList) {
        this.f$0 = arrayList;
    }

    public final void run() {
        NotificationCenter.getInstance(UserConfig.selectedAccount).doOnIdle(new AndroidUtilities$$ExternalSyntheticLambda13(this.f$0));
    }
}
