package org.telegram.messenger;

import java.io.File;
import org.telegram.messenger.ImageLoader.C02222;

final /* synthetic */ class ImageLoader$2$$Lambda$3 implements Runnable {
    private final C02222 arg$1;
    private final File arg$2;
    private final String arg$3;
    private final int arg$4;
    private final int arg$5;

    ImageLoader$2$$Lambda$3(C02222 c02222, File file, String str, int i, int i2) {
        this.arg$1 = c02222;
        this.arg$2 = file;
        this.arg$3 = str;
        this.arg$4 = i;
        this.arg$5 = i2;
    }

    public void run() {
        this.arg$1.lambda$fileDidLoaded$5$ImageLoader$2(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}
