package org.telegram.ui;

import org.telegram.ui.MediaActivity.MediaSearchAdapter;

final /* synthetic */ class MediaActivity$MediaSearchAdapter$$Lambda$1 implements Runnable {
    private final MediaSearchAdapter arg$1;
    private final String arg$2;

    MediaActivity$MediaSearchAdapter$$Lambda$1(MediaSearchAdapter mediaSearchAdapter, String str) {
        this.arg$1 = mediaSearchAdapter;
        this.arg$2 = str;
    }

    public void run() {
        this.arg$1.lambda$processSearch$3$MediaActivity$MediaSearchAdapter(this.arg$2);
    }
}
