package org.telegram.messenger;

public final /* synthetic */ class DispatchQueuePool$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ DispatchQueuePool f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ DispatchQueue f$2;

    public /* synthetic */ DispatchQueuePool$$ExternalSyntheticLambda0(DispatchQueuePool dispatchQueuePool, Runnable runnable, DispatchQueue dispatchQueue) {
        this.f$0 = dispatchQueuePool;
        this.f$1 = runnable;
        this.f$2 = dispatchQueue;
    }

    public final void run() {
        this.f$0.m1799lambda$execute$1$orgtelegrammessengerDispatchQueuePool(this.f$1, this.f$2);
    }
}
