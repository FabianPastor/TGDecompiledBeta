package org.telegram.messenger;

import android.util.SparseArray;

final /* synthetic */ class ImageLoader$$Lambda$11 implements Runnable {
    private final SparseArray arg$1;

    ImageLoader$$Lambda$11(SparseArray sparseArray) {
        this.arg$1 = sparseArray;
    }

    public void run() {
        FileLoader.setMediaDirs(this.arg$1);
    }
}
