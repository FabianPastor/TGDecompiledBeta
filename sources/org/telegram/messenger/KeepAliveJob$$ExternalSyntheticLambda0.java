package org.telegram.messenger;

public final /* synthetic */ class KeepAliveJob$$ExternalSyntheticLambda0 implements Runnable {
    public static final /* synthetic */ KeepAliveJob$$ExternalSyntheticLambda0 INSTANCE = new KeepAliveJob$$ExternalSyntheticLambda0();

    private /* synthetic */ KeepAliveJob$$ExternalSyntheticLambda0() {
    }

    public final void run() {
        KeepAliveJob.finishJobInternal();
    }
}
