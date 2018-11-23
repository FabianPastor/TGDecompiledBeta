package org.telegram.messenger;

import java.util.ArrayList;

final /* synthetic */ class FileLoader$$Lambda$8 implements Runnable {
    private final ArrayList arg$1;
    private final int arg$2;

    FileLoader$$Lambda$8(ArrayList arrayList, int i) {
        this.arg$1 = arrayList;
        this.arg$2 = i;
    }

    public void run() {
        FileLoader.lambda$deleteFiles$8$FileLoader(this.arg$1, this.arg$2);
    }
}
