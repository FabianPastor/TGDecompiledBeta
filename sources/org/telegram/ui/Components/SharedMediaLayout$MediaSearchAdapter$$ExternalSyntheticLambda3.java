package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SharedMediaLayout.MediaSearchAdapter f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ SharedMediaLayout$MediaSearchAdapter$$ExternalSyntheticLambda3(SharedMediaLayout.MediaSearchAdapter mediaSearchAdapter, ArrayList arrayList) {
        this.f$0 = mediaSearchAdapter;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$4(this.f$1);
    }
}
