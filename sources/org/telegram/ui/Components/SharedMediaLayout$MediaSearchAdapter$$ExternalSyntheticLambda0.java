package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SharedMediaLayout.MediaSearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ String f$3;

    public /* synthetic */ SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda0(SharedMediaLayout.MediaSearchAdapter mediaSearchAdapter, int i, ArrayList arrayList, String str) {
        this.f$0 = mediaSearchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = str;
    }

    public final void run() {
        this.f$0.lambda$queryServerSearch$0(this.f$1, this.f$2, this.f$3);
    }
}
