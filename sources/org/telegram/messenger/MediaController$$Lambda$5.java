package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class MediaController$$Lambda$5 implements Runnable {
    private final MediaController arg$1;
    private final ArrayList arg$2;

    MediaController$$Lambda$5(MediaController mediaController, ArrayList arrayList) {
        this.arg$1 = mediaController;
        this.arg$2 = arrayList;
    }

    public void run() {
        this.arg$1.lambda$processMediaObserver$5$MediaController(this.arg$2);
    }
}
