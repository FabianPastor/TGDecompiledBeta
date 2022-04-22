package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.ui.Components.SharedMediaLayout;

public final /* synthetic */ class SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ SharedMediaLayout.GroupUsersSearchAdapter f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ SharedMediaLayout$GroupUsersSearchAdapter$$ExternalSyntheticLambda3(SharedMediaLayout.GroupUsersSearchAdapter groupUsersSearchAdapter, ArrayList arrayList, ArrayList arrayList2) {
        this.f$0 = groupUsersSearchAdapter;
        this.f$1 = arrayList;
        this.f$2 = arrayList2;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$4(this.f$1, this.f$2);
    }
}
