package org.telegram.messenger;

public final /* synthetic */ class DispatchQueuePoolBackground$$ExternalSyntheticLambda2 implements Runnable {
    public final /* synthetic */ DispatchQueuePoolBackground f$0;
    public final /* synthetic */ Runnable f$1;
    public final /* synthetic */ DispatchQueue f$2;

    public /* synthetic */ DispatchQueuePoolBackground$$ExternalSyntheticLambda2(DispatchQueuePoolBackground dispatchQueuePoolBackground, Runnable runnable, DispatchQueue dispatchQueue) {
        this.f$0 = dispatchQueuePoolBackground;
        this.f$1 = runnable;
        this.f$2 = dispatchQueue;
    }

    public final void run() {
        this.f$0.lambda$execute$1(this.f$1, this.f$2);
    }
}
