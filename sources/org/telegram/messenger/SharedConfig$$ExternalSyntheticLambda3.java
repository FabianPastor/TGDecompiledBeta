package org.telegram.messenger;
/* loaded from: classes.dex */
public final /* synthetic */ class SharedConfig$$ExternalSyntheticLambda3 implements Runnable {
    public static final /* synthetic */ SharedConfig$$ExternalSyntheticLambda3 INSTANCE = new SharedConfig$$ExternalSyntheticLambda3();

    private /* synthetic */ SharedConfig$$ExternalSyntheticLambda3() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        SharedConfig.checkSaveToGalleryFiles();
    }
}
