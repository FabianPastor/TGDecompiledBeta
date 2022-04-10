package org.telegram.messenger;

public final /* synthetic */ class SharedConfig$$ExternalSyntheticLambda2 implements Runnable {
    public static final /* synthetic */ SharedConfig$$ExternalSyntheticLambda2 INSTANCE = new SharedConfig$$ExternalSyntheticLambda2();

    private /* synthetic */ SharedConfig$$ExternalSyntheticLambda2() {
    }

    public final void run() {
        SharedConfig.checkSaveToGalleryFiles();
    }
}
