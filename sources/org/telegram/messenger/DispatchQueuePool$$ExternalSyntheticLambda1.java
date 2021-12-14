package org.telegram.messenger;

public final /* synthetic */ class DispatchQueuePool$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ DispatchQueuePool f$0;
    public final /* synthetic */ DispatchQueue f$1;

    public /* synthetic */ DispatchQueuePool$$ExternalSyntheticLambda1(DispatchQueuePool dispatchQueuePool, DispatchQueue dispatchQueue) {
        this.f$0 = dispatchQueuePool;
        this.f$1 = dispatchQueue;
    }

    public final void run() {
        this.f$0.lambda$execute$0(this.f$1);
    }
}
