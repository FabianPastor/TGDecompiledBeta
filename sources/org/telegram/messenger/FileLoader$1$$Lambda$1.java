package org.telegram.messenger;

import org.telegram.messenger.FileLoader.C02051;

final /* synthetic */ class FileLoader$1$$Lambda$1 implements Runnable {
    private final C02051 arg$1;
    private final boolean arg$2;
    private final String arg$3;
    private final boolean arg$4;

    FileLoader$1$$Lambda$1(C02051 c02051, boolean z, String str, boolean z2) {
        this.arg$1 = c02051;
        this.arg$2 = z;
        this.arg$3 = str;
        this.arg$4 = z2;
    }

    public void run() {
        this.arg$1.lambda$didFailedUploadingFile$1$FileLoader$1(this.arg$2, this.arg$3, this.arg$4);
    }
}
