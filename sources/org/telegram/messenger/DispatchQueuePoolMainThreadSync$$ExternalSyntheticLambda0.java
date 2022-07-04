package org.telegram.messenger;

public final /* synthetic */ class DispatchQueuePoolMainThreadSync$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DispatchQueuePoolMainThreadSync f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ DispatchQueueMainThreadSync f$2;

    public /* synthetic */ DispatchQueuePoolMainThreadSync$$ExternalSyntheticLambda0(DispatchQueuePoolMainThreadSync dispatchQueuePoolMainThreadSync, Runnable runnable, DispatchQueueMainThreadSync dispatchQueueMainThreadSync) {
        this.f$0 = dispatchQueuePoolMainThreadSync;
        this.f$1 = runnable;
        this.f$2 = dispatchQueueMainThreadSync;
    }

    public final void run() {
        this.f$0.m1801xa66884f3(this.f$1, this.f$2);
    }
}
