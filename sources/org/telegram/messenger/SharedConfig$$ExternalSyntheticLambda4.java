package org.telegram.messenger;

public final /* synthetic */ class SharedConfig$$ExternalSyntheticLambda4 implements Runnable {
    public static final /* synthetic */ SharedConfig$$ExternalSyntheticLambda4 INSTANCE = new SharedConfig$$ExternalSyntheticLambda4();

    private /* synthetic */ SharedConfig$$ExternalSyntheticLambda4() {
    }

    public final void run() {
        SharedConfig.saveConfig();
    }
}
