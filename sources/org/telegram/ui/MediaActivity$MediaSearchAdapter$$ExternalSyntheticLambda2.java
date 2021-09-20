package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.MediaActivity;

public final /* synthetic */ class MediaActivity$MediaSearchAdapter$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ MediaActivity.MediaSearchAdapter f$0;
    public final /* synthetic */ String f$1;
    public final /* synthetic */ ArrayList f$2;

    public /* synthetic */ MediaActivity$MediaSearchAdapter$$ExternalSyntheticLambda2(MediaActivity.MediaSearchAdapter mediaSearchAdapter, String str, ArrayList arrayList) {
        this.f$0 = mediaSearchAdapter;
        this.f$1 = str;
        this.f$2 = arrayList;
    }

    public final void run() {
        this.f$0.lambda$search$2(this.f$1, this.f$2);
    }
}
