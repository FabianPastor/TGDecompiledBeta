package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.MediaActivity.MediaSearchAdapter;

final /* synthetic */ class MediaActivity$MediaSearchAdapter$$Lambda$2 implements Runnable {
    private final MediaSearchAdapter arg$1;
    private final ArrayList arg$2;

    MediaActivity$MediaSearchAdapter$$Lambda$2(MediaSearchAdapter mediaSearchAdapter, ArrayList arrayList) {
        this.arg$1 = mediaSearchAdapter;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$updateSearchResults$4$MediaActivity$MediaSearchAdapter(this.arg$2);
    }
}
