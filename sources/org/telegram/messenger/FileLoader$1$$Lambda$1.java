package org.telegram.messenger;

import org.telegram.messenger.FileLoader.CLASSNAME;

final /* synthetic */ class FileLoader$1$$Lambda$1 implements Runnable {
    private final CLASSNAME arg$1;
    private final boolean arg$2;
    private final String arg$3;
    private final boolean arg$4;

    FileLoader$1$$Lambda$1(CLASSNAME CLASSNAME, boolean z, String str, boolean z2) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = z;
        this.arg$3 = str;
        this.arg$4 = z2;
    }

    public void run() {
        this.arg$1.lambda$didFailedUploadingFile$1$FileLoader$1(this.arg$2, this.arg$3, this.arg$4);
    }
}
