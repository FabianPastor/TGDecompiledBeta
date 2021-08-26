package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.MediaActivity;

public final /* synthetic */ class MediaActivity$MediaSearchAdapter$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ MediaActivity.MediaSearchAdapter f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MediaActivity$MediaSearchAdapter$$ExternalSyntheticLambda0(MediaActivity.MediaSearchAdapter mediaSearchAdapter, int i, ArrayList arrayList) {
        this.f$0 = mediaSearchAdapter;
        this.f$1 = i;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$queryServerSearch$0(this.f$1, this.f$2);
    }
}
