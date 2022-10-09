package org.telegram.messenger;
/* loaded from: classes.dex */
public final /* synthetic */ class SharedConfig$$ExternalSyntheticLambda4 implements Runnable {
    public static final /* synthetic */ SharedConfig$$ExternalSyntheticLambda4 INSTANCE = new SharedConfig$$ExternalSyntheticLambda4();

    private /* synthetic */ SharedConfig$$ExternalSyntheticLambda4() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        SharedConfig.saveConfig();
    }
}
