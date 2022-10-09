package org.telegram.messenger;
/* loaded from: classes.dex */
public final /* synthetic */ class KeepAliveJob$$ExternalSyntheticLambda0 implements Runnable {
    public static final /* synthetic */ KeepAliveJob$$ExternalSyntheticLambda0 INSTANCE = new KeepAliveJob$$ExternalSyntheticLambda0();

    private /* synthetic */ KeepAliveJob$$ExternalSyntheticLambda0() {
    }

    @Override // java.lang.Runnable
    public final void run() {
        KeepAliveJob.finishJobInternal();
    }
}
