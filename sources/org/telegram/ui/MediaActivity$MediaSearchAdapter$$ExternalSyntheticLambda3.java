package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.MediaActivity;

public final /* synthetic */ class MediaActivity$MediaSearchAdapter$$ExternalSyntheticLambda3 implements Runnable {
    public final /* synthetic */ MediaActivity.MediaSearchAdapter f$0;
    public final /* synthetic */ ArrayList f$1;

    public /* synthetic */ MediaActivity$MediaSearchAdapter$$ExternalSyntheticLambda3(MediaActivity.MediaSearchAdapter mediaSearchAdapter, ArrayList arrayList) {
        this.f$0 = mediaSearchAdapter;
        this.f$1 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$updateSearchResults$4(this.f$1);
    }
}
