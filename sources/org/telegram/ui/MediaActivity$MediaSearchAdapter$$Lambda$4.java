package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.ui.MediaActivity.MediaSearchAdapter;

final /* synthetic */ class MediaActivity$MediaSearchAdapter$$Lambda$4 implements Runnable {
    private final MediaSearchAdapter arg$1;
    private final int arg$2;
    private final ArrayList arg$3;

    MediaActivity$MediaSearchAdapter$$Lambda$4(MediaSearchAdapter mediaSearchAdapter, int i, ArrayList arrayList) {
        this.arg$1 = mediaSearchAdapter;
        this.arg$2 = i;
        this.arg$3 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$null$0$MediaActivity$MediaSearchAdapter(this.arg$2, this.arg$3);
    }
}
