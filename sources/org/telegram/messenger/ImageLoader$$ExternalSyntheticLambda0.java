package org.telegram.messenger;

import android.util.SparseArray;

public final /* synthetic */ class ImageLoader$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ SparseArray f$0;

    public /* synthetic */ ImageLoader$$ExternalSyntheticLambda0(SparseArray sparseArray) {
        this.f$0 = sparseArray;
    }

    public final void run() {
        FileLoader.setMediaDirs(this.f$0);
    }
}
